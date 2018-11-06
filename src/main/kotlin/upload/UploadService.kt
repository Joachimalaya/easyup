package upload

import auth.Authorization
import com.google.api.client.http.InputStreamContent
import com.google.api.client.util.DateTime
import com.google.api.services.youtube.model.PlaylistItem
import com.google.api.services.youtube.model.PlaylistItemSnippet
import com.google.api.services.youtube.model.ResourceId
import com.google.api.services.youtube.model.Video
import config.jsonMapper
import entity.UploadJob
import exec.logger
import javafx.scene.control.Tab
import upload.resumable.RestorableUpload
import upload.resumable.UnfinishedUploadLoadService
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

/**
 * Handles the actual upload of an [UploadJob] to YouTube by calling the API.
 */
object UploadService {

    var state = UploadServiceState.ACTIVE

    val UPLOAD_QUEUE_FILE = File("${UnfinishedUploadLoadService.UNFINISHED_UPLOAD_DIRECTORY}/queue.json")

    private val uploadQueue = LinkedList<UploadJob>()
    private var currentUpload: UploadJob? = null

    private var uploadNum = 0

    private fun beginUpload(uploadJob: UploadJob) {
        currentUpload = uploadJob
        // started in new Thread to prevent UI hang
        uploadJob.name = "easyUp Upload Thread-${uploadNum++}"
        uploadJob.start()
    }

    fun addThumbnail(uploadJob: UploadJob, returnedVideo: Video) {
        uploadJob.inputData.thumbnailFile?.inputStream()
                .use {
                    val thumbnailSet = Authorization.connection.thumbnails()
                            .set(returnedVideo.id, InputStreamContent("image/png", it))
                    thumbnailSet.mediaHttpUploader.isDirectUploadEnabled = false
                    thumbnailSet.execute()
                }
    }

    fun addToPlaylist(uploadJob: UploadJob, returnedVideo: Video) {
        if (uploadJob.playlist != null) {
            val resourceId = ResourceId().setKind("youtube#video")
                    .setVideoId(returnedVideo.id)
            val snippet = PlaylistItemSnippet().setResourceId(resourceId)
                    .setPlaylistId(uploadJob.playlist.id)
            val playlistItem = PlaylistItem().setSnippet(snippet)
            Authorization.connection.playlistItems()
                    .insert("snippet", playlistItem)
                    .execute()
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

    fun persistUploadQueue() {
        val toPersist = if (uploading()) {
            uploadQueue + currentUpload!!
        } else {
            uploadQueue
        }
        val restorables = toPersist.map { RestorableUpload(it) }
        jsonMapper.writeValue(UPLOAD_QUEUE_FILE, restorables)
        logger.debug("persisted list of restorable uploads:\n${jsonMapper.writeValueAsString(restorables)}")
    }

    fun tryToStartUpload() {
        if (!uploading() && !uploadQueue.isEmpty()) {
            beginUpload(uploadQueue.pollLast())
        }
    }

    fun uploading() = currentUpload != null && !currentUpload!!.isAlive

    fun uploadingTab(tab: Tab) = currentUpload?.uploadTab == tab

    fun removeFromQueueWithTab(tab: Tab) = uploadQueue.removeIf { it.uploadTab == tab }

    class UploadCancelledException : Exception("the running upload was cancelled")
}
