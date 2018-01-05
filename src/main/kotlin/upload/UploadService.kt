package upload

import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener
import com.google.api.client.http.InputStreamContent
import com.google.api.services.youtube.model.Video
import com.google.api.services.youtube.model.VideoSnippet
import com.google.api.services.youtube.model.VideoStatus
import entity.Placeholder
import entity.UploadData
import exec.youTube
import javafx.scene.control.ProgressBar
import template.fill.replacePlaceholders
import youtube.video.PrivacyStatus

/**
 * Handles the actual upload of data to YouTube by calling the API.
 */
class UploadService {

    // TODO: start in new Thread to prevent UI hang?
    fun beginUpload(uploadData: UploadData, placeholders: List<Placeholder>, progressBar: ProgressBar) {
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
            uploader.isDirectUploadEnabled = true
            uploader.progressListener = MediaHttpUploaderProgressListener {
                // TODO: fix
//            when(it.uploadState) {
//                MediaHttpUploader.UploadState.MEDIA_IN_PROGRESS -> progressBar.progress = it.progress
//                else -> {}
//            }
            }

            val returnedVideo = videoInsert.execute()
            // add thumbnailFile
            // TODO: thumbnail-upload okay? leads to hangup
//            if (uploadData.thumbnailFile != null) {
//                uploadData.thumbnailFile!!.inputStream().use {
//                    val thumbnailSet = youTube.thumbnails().set(returnedVideo.id, InputStreamContent("image/png", it))
//                    thumbnailSet.mediaHttpUploader.isDirectUploadEnabled = false
//                    thumbnailSet.mediaHttpUploader.progressListener = MediaHttpUploaderProgressListener {
//                        // TODO: implement simple notification
//                    }
//
//                    thumbnailSet.execute()
//                }
//            }
        }

    }
}