package entity

import auth.Authorization
import com.google.api.client.http.InputStreamContent
import com.google.api.services.youtube.model.Playlist
import com.google.api.services.youtube.model.Video
import com.google.api.services.youtube.model.VideoSnippet
import com.google.api.services.youtube.model.VideoStatus
import com.google.common.base.Stopwatch
import exec.logger
import javafx.application.Platform
import javafx.beans.property.DoubleProperty
import javafx.beans.property.StringProperty
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.Tab
import template.fill.PlaceholderUpdateService
import ui.alert.SizedAlert
import ui.notification.Notification
import upload.UploadProgressListener
import upload.UploadService
import upload.UploadServiceState
import upload.resumable.RestorableUpload
import youtube.video.BinaryPrefix
import youtube.video.PrivacyStatus
import youtube.video.numBytes
import java.io.FileNotFoundException
import java.time.LocalDateTime

/**
 * Contains all the data needed to upload a video to YouTube.
 */
class UploadJob(
        val placeholders: List<Placeholder>,
        val progressBar: DoubleProperty,
        val progressText: StringProperty,
        val uploadTab: Tab,
        val publishDate: LocalDateTime,
        val privacyStatus: PrivacyStatus,
        val playlist: Playlist?,
        val inputData: RestorableUpload
) : Thread() {
    companion object {
        private val uploadBufferSize = numBytes(256, BinaryPrefix.MEBIBYTE)
    }

    override fun run() {
        val video = Video()

        video.status = VideoStatus()
        video.status.privacyStatus = privacyStatus.privacyStatus

        video.snippet = VideoSnippet()
        video.snippet.title = PlaceholderUpdateService.replacePlaceholders(inputData.titleTemplate, placeholders)
        video.snippet.description = PlaceholderUpdateService.replacePlaceholders(inputData.descriptionTemplate, placeholders)
        video.snippet.tags = inputData.tags.asList()

        if (privacyStatus == PrivacyStatus.SCHEDULED) {
            video.snippet.publishedAt = UploadService.publishDateToGoogleDateTime(publishDate)
        }

        inputData.videoFile.inputStream()
                .buffered(uploadBufferSize)
                .use {
                    try {
                        val mediaContent = InputStreamContent("video/*", it)
                        mediaContent.setRetrySupported(true)
                        val videoInsert = Authorization.connection.videos()
                                .insert("snippet,statistics,status", video, mediaContent)
                        val uploader = videoInsert.mediaHttpUploader
                        uploader.isDirectUploadEnabled = false
                        uploader.progressListener = UploadProgressListener(this, Stopwatch.createUnstarted())

                        val returnedVideo = videoInsert.execute()

                        // add thumbnailFile
                        UploadService.addThumbnail(this, returnedVideo)

                        // add to playlist if selected
                        UploadService.addToPlaylist(this, returnedVideo)
                    } catch (fnfe: FileNotFoundException) {
                        SizedAlert(
                                Alert.AlertType.ERROR,
                                "One of the files needed to start the upload could not be loaded." +
                                        "\nFollowing files were looked for:" +
                                        "\n${inputData.thumbnailFile}" +
                                        "\n${inputData.videoFile}" +
                                        "\nBecause those files are necessary to continue, this upload can not start.",
                                ButtonType.OK
                        ).showAndWait()
                        logger.error("file referenced at upload start not found", fnfe)
                    } catch (uce: UploadService.UploadCancelledException) {
                        if (UploadService.state == UploadServiceState.CANCEL_CURRENT) {
                            UploadService.state = UploadServiceState.ACTIVE
                        } else if (UploadService.state == UploadServiceState.TERMINATE) {
                            return
                        }
                    }
                }

        // start next queued upload
        Platform.runLater {
            uploadTab.tabPane.selectionModel.select(uploadTab.tabPane.selectionModel.selectedIndex + 1)
            uploadTab.tabPane.tabs.remove(uploadTab)
            Notification("upload of ${video.snippet.title} done").showAndFadeOut(10000, 5000)
        }
        UploadService.persistUploadQueue()
        UploadService.tryToStartUpload()
    }


}