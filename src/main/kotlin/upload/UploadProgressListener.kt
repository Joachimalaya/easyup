package upload

import com.google.api.client.googleapis.media.MediaHttpUploader
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener
import com.google.common.base.Stopwatch
import entity.UploadJob
import ui.MainWindow
import java.io.File
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong

/**
 * This ProgressListener approximates the percentage done ETA and displays them in several places of the UI.
 */
class UploadProgressListener(private val uploadJob: UploadJob, private val stopwatch: Stopwatch) : MediaHttpUploaderProgressListener {

    companion object {
        private fun percentageDone(uploader: MediaHttpUploader, uploadedFile: File) =
                uploader.numBytesUploaded.toDouble() / uploadedFile.length().toDouble()

        fun progressFeedback(ratioDone: Double, elapsedMillis: Long): String {
            val eta = eta(elapsedMillis, ratioDone)
            return "%02.1f%% uploaded; ETA: %d:%02d:%02d".format(ratioDone * 100, eta / 3600, (eta % 3600) / 60, (eta % 60))
        }

        fun shortProgressFeedback(ratioDone: Double, elapsedMillis: Long): String {
            val eta = eta(elapsedMillis, ratioDone)
            return "%01.0f%% %d:%02d".format(ratioDone * 100, eta / 3600, (eta % 3600) / 60, (eta % 60))
        }

        private fun eta(elapsedMillis: Long, ratioDone: Double) = Duration.ofMillis((elapsedMillis / ratioDone - elapsedMillis).roundToLong()).seconds
    }

    override fun progressChanged(uploader: MediaHttpUploader) {
        if (UploadService.cancelUpload) {
            Thread.currentThread()
                    .interrupt()
        }
        when (uploader.uploadState!!) {
            MediaHttpUploader.UploadState.NOT_STARTED ->
                uploadJob.progressText.value = "starting..."
            MediaHttpUploader.UploadState.INITIATION_STARTED ->
                uploadJob.progressText.value = "initiating upload..."
            MediaHttpUploader.UploadState.INITIATION_COMPLETE ->
                stopwatch.start()
            MediaHttpUploader.UploadState.MEDIA_IN_PROGRESS -> {
                uploadJob.progressBar.value = percentageDone(uploader, uploadJob.inputData.videoFile)
                uploadJob.progressText.value = progressFeedback(uploadJob.progressBar.value, stopwatch.elapsed(TimeUnit.MILLISECONDS))

                MainWindow.INSTANCE?.changeTitle(shortProgressFeedback(uploadJob.progressBar.value, stopwatch.elapsed(TimeUnit.MILLISECONDS)))
            }
            MediaHttpUploader.UploadState.MEDIA_COMPLETE -> {
                uploadJob.progressText.value = "upload complete"
                MainWindow.INSTANCE?.resetTitle()
            }
        }
    }
}