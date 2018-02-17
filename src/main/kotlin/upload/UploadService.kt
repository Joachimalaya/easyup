package upload

import auth.Authorization
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
import javafx.scene.control.ProgressBar
import javafx.scene.text.Text
import template.fill.PlaceholderUpdateService.replacePlaceholders
import upload.resumable.RestorableUpload
import upload.resumable.unfinishedUploadDirectory
import youtube.video.ByteSize
import youtube.video.PrivacyStatus
import youtube.video.numBytes
import java.io.File
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong

/**
 * Handles the actual upload of data to YouTube by calling the API.
 */
object UploadService {

    var cancelUpload = false

    private val uploadBufferSize = numBytes(512, ByteSize.MEGABYTE)

    fun beginUpload(uploadData: UploadData, placeholders: List<Placeholder>, progressBar: ProgressBar, progressText: Text) {
        // started in new Thread to prevent UI hang
        Thread {
            val uploadPersistenceFile = getUploadDataFile()
            uploadPersistenceFile.outputStream().use { jsonMapper.writeValue(it, RestorableUpload(placeholders, uploadData)) }

            val video = Video()

            video.status = VideoStatus()
            video.status.privacyStatus = PrivacyStatus.PRIVATE.privacyStatus

            video.snippet = VideoSnippet()
            video.snippet.title = replacePlaceholders(uploadData.title, placeholders)
            video.snippet.description = replacePlaceholders(uploadData.description, placeholders)

            video.snippet.tags = uploadData.tags.asList()
            uploadData.videoFile.inputStream().buffered(uploadBufferSize).use {
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
                            progressText.text = "starting..."
                        MediaHttpUploader.UploadState.INITIATION_STARTED ->
                            progressText.text = "initiating upload..."
                        MediaHttpUploader.UploadState.INITIATION_COMPLETE ->
                            stopwatch.start()
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
                        val thumbnailSet = Authorization.connection.thumbnails().set(returnedVideo.id, InputStreamContent("image/png", it))
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

    private fun percentageDone(uploader: MediaHttpUploader, uploadData: UploadData) =
            uploader.numBytesUploaded.toDouble() / uploadData.videoFile.length().toDouble()

    fun progressFeedback(ratioDone: Double, elapsedMillis: Long): String {
        val eta = Duration.ofMillis((elapsedMillis / ratioDone - elapsedMillis).roundToLong()).seconds
        return String.format("%02.1f%% uploaded; ETA: %d:%02d:%02d", ratioDone * 100, eta / 3600, (eta % 3600) / 60, (eta % 60))
    }

    private fun getUploadDataFile(): File {
        val uploadDataFile = File("$unfinishedUploadDirectory/${UUID.randomUUID()}.json")
        Files.createParentDirs(uploadDataFile)
        return uploadDataFile
    }

}