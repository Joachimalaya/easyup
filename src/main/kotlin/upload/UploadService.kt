package upload

import auth.Authorization
import com.google.api.client.googleapis.media.MediaHttpUploader
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener
import com.google.api.client.http.InputStreamContent
import com.google.api.client.util.DateTime
import com.google.api.services.youtube.model.Video
import com.google.api.services.youtube.model.VideoSnippet
import com.google.api.services.youtube.model.VideoStatus
import com.google.common.base.Stopwatch
import config.jsonMapper
import entity.UploadJob
import javafx.application.Platform
import template.fill.PlaceholderUpdateService.replacePlaceholders
import ui.MainWindow
import upload.resumable.RestorableUpload
import upload.resumable.UnfinishedUploadLoadService
import youtube.video.BinaryPrefix
import youtube.video.PrivacyStatus
import youtube.video.numBytes
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong


/**
 * Handles the actual upload of template to YouTube by calling the API.
 */
object UploadService {

    var cancelUpload = false

    var uploading = false
        private set

    val UPLOAD_QUEUE_FILE = File("${UnfinishedUploadLoadService.UNFINISHED_UPLOAD_DIRECTORY}/queue.json")
    private val uploadQueue = LinkedList<UploadJob>()

    private val uploadBufferSize = numBytes(512, BinaryPrefix.MEBIBYTE)

    private fun beginUpload(uploadJob: UploadJob) {
        // started in new Thread to prevent UI hang
        Thread {
            val video = Video()

            video.status = VideoStatus()
            video.status.privacyStatus = PrivacyStatus.PRIVATE.privacyStatus

            if (uploadJob.scheduledPublish) {
                publishDateToGoogleDateTime(uploadJob.publishDate)
            }

            video.snippet = VideoSnippet()
            video.snippet.title = replacePlaceholders(uploadJob.template.title, uploadJob.placeholders)
            video.snippet.description = replacePlaceholders(uploadJob.template.description, uploadJob.placeholders)

            video.snippet.tags = uploadJob.template.tags.asList()
            uploadJob.template.videoFile.inputStream().buffered(uploadBufferSize).use {
                val mediaContent = InputStreamContent("video/*", it)
                mediaContent.setRetrySupported(true)
                val videoInsert = Authorization.connection.videos().insert("snippet,statistics,status", video, mediaContent)
                val uploader = videoInsert.mediaHttpUploader
                uploader.isDirectUploadEnabled = false
                val stopwatch = Stopwatch.createUnstarted()

                uploader.progressListener = MediaHttpUploaderProgressListener {
                    if (cancelUpload) {
                        throw RuntimeException("termination of upload thread requested")
                    }
                    when (it.uploadState!!) {
                        MediaHttpUploader.UploadState.NOT_STARTED ->
                            uploadJob.progressText.value = "starting..."
                        MediaHttpUploader.UploadState.INITIATION_STARTED ->
                            uploadJob.progressText.value = "initiating upload..."
                        MediaHttpUploader.UploadState.INITIATION_COMPLETE ->
                            stopwatch.start()
                        MediaHttpUploader.UploadState.MEDIA_IN_PROGRESS -> {
                            uploadJob.progressBar.value = percentageDone(it, uploadJob.template.videoFile)
                            uploadJob.progressText.value = progressFeedback(uploadJob.progressBar.value, stopwatch.elapsed(TimeUnit.MILLISECONDS))

                            MainWindow.INSTANCE?.changeTitle(shortProgressFeedback(uploadJob.progressBar.value, stopwatch.elapsed(TimeUnit.MILLISECONDS)))
                        }
                        MediaHttpUploader.UploadState.MEDIA_COMPLETE -> {
                            uploadJob.progressText.value = "upload complete"
                        }
                    }
                }

                uploading = true
                val returnedVideo = videoInsert.execute()

                // add thumbnailFile
                uploadJob.template.thumbnailFile?.inputStream().use {
                    val thumbnailSet = Authorization.connection.thumbnails().set(returnedVideo.id, InputStreamContent("image/png", it))
                    thumbnailSet.mediaHttpUploader.isDirectUploadEnabled = false
                    thumbnailSet.execute()
                }
                // remove entry from queue and update persisted data
                uploadQueue.pollLast()
                persistUploadQueue()

                // start next queued upload
                uploading = false
                Platform.runLater { uploadJob.uploadTab.tabPane.tabs.remove(uploadJob.uploadTab) }

                tryToStartUpload()
            }
        }.start()
    }

    fun publishDateToGoogleDateTime(publishDate: LocalDateTime): DateTime {
        val out = Date.from(publishDate.atZone(ZoneId.systemDefault()).toInstant())
        return DateTime(out)
    }

    private fun percentageDone(uploader: MediaHttpUploader, uploadedFile: File) =
            uploader.numBytesUploaded.toDouble() / uploadedFile.length().toDouble()

    fun progressFeedback(ratioDone: Double, elapsedMillis: Long): String {
        val eta = Duration.ofMillis((elapsedMillis / ratioDone - elapsedMillis).roundToLong()).seconds
        return String.format("%02.1f%% uploaded; ETA: %d:%02d:%02d", ratioDone * 100, eta / 3600, (eta % 3600) / 60, (eta % 60))
    }

    fun shortProgressFeedback(ratioDone: Double, elapsedMillis: Long): String {
        val eta = Duration.ofMillis((elapsedMillis / ratioDone - elapsedMillis).roundToLong()).seconds
        return String.format("%01.0f%% %d:%02d", ratioDone * 100, eta / 3600, (eta % 3600) / 60, (eta % 60))
    }

    fun scheduleUpload(job: UploadJob) {
        uploadQueue.push(job)
        persistUploadQueue()
        tryToStartUpload()
    }

    private fun persistUploadQueue() {
        jsonMapper.writeValue(UPLOAD_QUEUE_FILE, uploadQueue.map { RestorableUpload(it) })
    }

    private fun tryToStartUpload() {
        if (!uploading && !uploadQueue.isEmpty()) {
            beginUpload(uploadQueue.peekLast())
        }
    }

}