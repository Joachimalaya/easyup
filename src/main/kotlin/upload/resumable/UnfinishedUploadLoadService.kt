package upload.resumable

import auth.Authorization
import com.google.api.services.youtube.model.Video
import config.jsonMapper
import exec.appDirectory
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import ui.alert.SizedAlert
import upload.UploadService
import java.io.File

object UnfinishedUploadLoadService {

    val UNFINISHED_UPLOAD_DIRECTORY = File("$appDirectory/uploads")

    fun loadUnfinishedUploads(): List<RestorableUpload> = jsonMapper.readValue(UploadService.UPLOAD_QUEUE_FILE, Array<RestorableUpload>::class.java).asList()

    fun deleteUnfinishedUploads() {
        val failedUploads = findFailedUploads()
        if (failedUploads.isNotEmpty()) {
            val failedTitles = failedUploads.joinToString("\n", transform = { it.snippet.title })
            val response = SizedAlert(
                    Alert.AlertType.CONFIRMATION,
                    "There are some failed uploads for your account. Do you want to delete them?\n(Be aware, that this feature is still in development and deleted videos can not be recovered.)\n\nTitles of the failed uploads:\n$failedTitles",
                    ButtonType.YES,
                    ButtonType.NO
            ).showAndWait()

            if (response.isPresent && response.get() == ButtonType.YES) {
                failedUploads.forEach {
                    Authorization.connection.videos().delete(it.id).execute()
                }
            }
        }
    }

    // TODO: processingFailureReason is not filled correctly by YouTube; not yet implemented correctly?
    private fun findFailedUploads(videosToLookAt: Int = 5): List<Video> {
        val uploadListId = Authorization.connection.Channels()
                .list("contentDetails")
                .setMine(true)
                .execute()
                .items[0].contentDetails.relatedPlaylists.uploads

        val uploads = Authorization.connection.playlistItems()
                .list("contentDetails")
                .setPlaylistId(uploadListId)
                .setMaxResults(videosToLookAt.toLong())
                .execute()

        return Authorization.connection.videos()
                .list("snippet,processingDetails")
                .setId(uploads.items.joinToString(",", transform = { it.contentDetails.videoId }))
                .execute()
                .items
                .filter { it.processingDetails.processingFailureReason == "uploadFailed" }
    }
}