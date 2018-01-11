package upload

import com.google.api.client.googleapis.media.MediaHttpUploader
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener
import com.google.api.client.http.InputStreamContent
import com.google.api.services.youtube.model.Video
import com.google.api.services.youtube.model.VideoSnippet
import com.google.api.services.youtube.model.VideoStatus
import com.google.common.base.Stopwatch
import com.google.common.io.Files
import config.jsonMapper
import entity.Placeholder
import entity.UploadData
import exec.appDirectory
import exec.youTube
import javafx.scene.control.ProgressBar
import javafx.scene.text.Text
import template.fill.replacePlaceholders
import youtube.video.PrivacyStatus
import java.io.File
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong

/**
 * Handles the actual upload of data to YouTube by calling the API.
 */
class UploadService {

    fun beginUpload(uploadData: UploadData, placeholders: List<Placeholder>, progressBar: ProgressBar, progressText: Text) {
        // started in new Thread to prevent UI hang
        Thread {
            val video = Video()

            video.status = VideoStatus()
            video.status.privacyStatus = PrivacyStatus.PRIVATE.privacyStatus

            video.snippet = VideoSnippet()
            video.snippet.title = replacePlaceholders(uploadData.title, placeholders)
            video.snippet.description = replacePlaceholders(uploadData.description, placeholders)

            video.snippet.tags = uploadData.tags.asList()
            uploadData.videoFile.inputStream().use {
                val mediaContent = InputStreamContent("video/*", it)
                val videoInsert = youTube.videos().insert("snippet,statistics,status", video, mediaContent)
                val uploader = videoInsert.mediaHttpUploader
                uploader.isDirectUploadEnabled = false
                val stopwatch = Stopwatch.createUnstarted()
                val uploadPersistenceFile = getUploadDataFile()
                uploader.progressListener = MediaHttpUploaderProgressListener {
                    when (it.uploadState!!) {
                        MediaHttpUploader.UploadState.NOT_STARTED ->
                            progressText.text = "starting..."
                        MediaHttpUploader.UploadState.INITIATION_STARTED ->
                            progressText.text = "initiating upload..."
                        MediaHttpUploader.UploadState.INITIATION_COMPLETE -> {
                            uploadPersistenceFile.outputStream().use { jsonMapper.writeValue(it, uploadData) }
                            stopwatch.start()
                        }
                        MediaHttpUploader.UploadState.MEDIA_IN_PROGRESS -> {
                            progressBar.progress = percentageDone(it, uploadData)
                            progressText.text = progressFeedback(progressBar.progress, stopwatch.elapsed(TimeUnit.MILLISECONDS))
                            // TODO: write progress to window title
                        }
                        MediaHttpUploader.UploadState.MEDIA_COMPLETE -> {
                            progressText.text = "upload complete"

                            // delete persisted upload file
                            uploadPersistenceFile.delete()
                        }
                    }
                }

                val returnedVideo = videoInsert.execute()

                // add thumbnailFile
                if (uploadData.thumbnailFile != null) {
                    uploadData.thumbnailFile!!.inputStream().use {
                        val thumbnailSet = youTube.thumbnails().set(returnedVideo.id, InputStreamContent("image/png", it))
                        thumbnailSet.mediaHttpUploader.isDirectUploadEnabled = false
                        thumbnailSet.mediaHttpUploader.progressListener = MediaHttpUploaderProgressListener {
                            // TODO: implement simple notification
                        }
                        thumbnailSet.execute()
                    }
                }
            }
        }.start()
    }

    private fun percentageDone(it: MediaHttpUploader, uploadData: UploadData) =
            it.numBytesUploaded.toDouble() / uploadData.videoFile.length().toDouble()

    fun progressFeedback(ratioDone: Double, elapsedMillis: Long): String {
        val eta = Duration.ofMillis((elapsedMillis / ratioDone - elapsedMillis).roundToLong()).seconds
        return String.format("%02.1f%% uploaded; ETA: %d:%02d:%02d", ratioDone * 100, eta / 3600, (eta % 3600) / 60, (eta % 60))
    }

    private fun getUploadDataFile(): File {
        val uploadDataFile = File("$appDirectory/uploads/${UUID.randomUUID()}.json")
        Files.createParentDirs(uploadDataFile)
        return uploadDataFile
    }

}