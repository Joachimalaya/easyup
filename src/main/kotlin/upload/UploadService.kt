package upload

import auth.Authorization
import com.google.api.client.http.InputStreamContent
import com.google.api.client.util.DateTime
import com.google.api.services.youtube.model.*
import com.google.common.base.Stopwatch
import config.jsonMapper
import entity.UploadJob
import exec.logger
import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.Tab
import template.fill.PlaceholderUpdateService.replacePlaceholders
import ui.alert.SizedAlert
import ui.notification.Notification
import upload.resumable.RestorableUpload
import upload.resumable.UnfinishedUploadLoadService
import youtube.video.BinaryPrefix
import youtube.video.PrivacyStatus
import youtube.video.numBytes
import java.io.File
import java.io.FileNotFoundException
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

/**
 * Handles the actual upload of an [UploadJob] to YouTube by calling the API.
 */
object UploadService {

    var cancelUpload = false

    val UPLOAD_QUEUE_FILE = File("${UnfinishedUploadLoadService.UNFINISHED_UPLOAD_DIRECTORY}/queue.json")

    private val uploadQueue = LinkedList<UploadJob>()
    private var currentUpload: UploadJob? = null
    private val uploadBufferSize = numBytes(256, BinaryPrefix.MEBIBYTE)

    private var uploadNum = 0

    private fun beginUpload(uploadJob: UploadJob) {
        currentUpload = uploadJob
        // started in new Thread to prevent UI hang
        val uploadThread = Thread {
            val video = Video()

            video.status = VideoStatus()
            video.status.privacyStatus = uploadJob.privacyStatus.privacyStatus

            video.snippet = VideoSnippet()
            video.snippet.title = replacePlaceholders(uploadJob.inputData.titleTemplate, uploadJob.placeholders)
            video.snippet.description = replacePlaceholders(uploadJob.inputData.descriptionTemplate, uploadJob.placeholders)
            video.snippet.tags = uploadJob.inputData.tags.asList()

            if (uploadJob.privacyStatus == PrivacyStatus.SCHEDULED) {
                video.snippet.publishedAt = publishDateToGoogleDateTime(uploadJob.publishDate)
            }

            uploadJob.inputData.videoFile.inputStream().buffered(uploadBufferSize).use {
                try {
                    val mediaContent = InputStreamContent("video/*", it)
                    mediaContent.setRetrySupported(true)
                    val videoInsert = Authorization.connection.videos().insert("snippet,statistics,status", video, mediaContent)
                    val uploader = videoInsert.mediaHttpUploader
                    uploader.isDirectUploadEnabled = false
                    uploader.progressListener = UploadProgressListener(uploadJob, Stopwatch.createUnstarted())

                    val returnedVideo = videoInsert.execute()

                    // add thumbnailFile
                    addThumbnail(uploadJob, returnedVideo)

                    // add to playlist if selected
                    addToPlaylist(uploadJob, returnedVideo)
                } catch (fnfe: FileNotFoundException) {
                    SizedAlert(
                            Alert.AlertType.ERROR,
                            "One of the files needed to start the upload could not be loaded." +
                                    "\nFollowing files were looked for:" +
                                    "\n${uploadJob.inputData.thumbnailFile}" +
                                    "\n${uploadJob.inputData.videoFile}" +
                                    "\nBecause those files are necessary to continue, this upload can not start.",
                            ButtonType.OK
                    ).showAndWait()
                    logger.error("file referenced at upload start not found", fnfe)
                }
            }

            // start next queued upload
            Platform.runLater {
                uploadJob.uploadTab.tabPane.tabs.remove(uploadJob.uploadTab)
                Notification("upload of ${video.snippet.title} done").showAndFadeOut(10000, 5000)
            }
            currentUpload = null
            persistUploadQueue()
            tryToStartUpload()
        }
        uploadThread.name = "easyUp Upload Thread-${uploadNum++}"
        uploadThread.start()
    }

    private fun addThumbnail(uploadJob: UploadJob, returnedVideo: Video) {
        uploadJob.inputData.thumbnailFile?.inputStream().use {
            val thumbnailSet = Authorization.connection.thumbnails().set(returnedVideo.id, InputStreamContent("image/png", it))
            thumbnailSet.mediaHttpUploader.isDirectUploadEnabled = false
            thumbnailSet.execute()
        }
    }

    private fun addToPlaylist(uploadJob: UploadJob, returnedVideo: Video) {
        if (uploadJob.playlist != null) {
            val resourceId = ResourceId().setKind("youtube#video").setVideoId(returnedVideo.id)
            val snippet = PlaylistItemSnippet().setResourceId(resourceId).setPlaylistId(uploadJob.playlist.id)
            val playlistItem = PlaylistItem().setSnippet(snippet)
            Authorization.connection.playlistItems().insert("snippet", playlistItem).execute()
        }
    }

    fun publishDateToGoogleDateTime(publishDate: LocalDateTime): DateTime {
        val out = Date.from(publishDate.atZone(ZoneId.systemDefault()).toInstant())
        return DateTime(out)
    }

    fun scheduleUpload(job: UploadJob) {
        uploadQueue.push(job)
        persistUploadQueue()
        tryToStartUpload()
    }

    private fun persistUploadQueue() {
        val toPersist = when (currentUpload) {
            null -> uploadQueue
            else -> uploadQueue + currentUpload!!
        }
        jsonMapper.writeValue(UPLOAD_QUEUE_FILE, toPersist.map { RestorableUpload(it) })
    }

    private fun tryToStartUpload() {
        if (!uploading() && !uploadQueue.isEmpty()) {
            beginUpload(uploadQueue.pollLast())
        }
    }

    fun uploading() = currentUpload != null

    fun uploadingTab(tab: Tab) = currentUpload?.uploadTab == tab

    fun removeFromQueueWithTab(tab: Tab) = uploadQueue.removeIf { it.uploadTab == tab }
}
