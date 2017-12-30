package upload

import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener
import com.google.api.client.http.InputStreamContent
import com.google.api.services.youtube.model.*
import entity.Placeholder
import entity.UploadData
import exec.youTube
import template.fill.replacePlaceholders
import youtube.video.PrivacyStatus

/**
 * Handles the actual upload of data to YouTube by calling the API.
 */
class UploadService {

    fun beginUpload(uploadData: UploadData, placeholders: List<Placeholder>) {
        val video = Video()

        video.status = VideoStatus()
        video.status.privacyStatus = PrivacyStatus.PRIVATE.privacyStatus

        video.snippet = VideoSnippet()
        video.snippet.title = replacePlaceholders(uploadData.title, placeholders)
        video.snippet.description = replacePlaceholders(uploadData.description, placeholders)

        video.snippet.tags = uploadData.tags.asList()

        val mediaContent = InputStreamContent("video/*", uploadData.videoFile.inputStream())

        val videoInsert = youTube.videos().insert("snippet,statistics,status", video, mediaContent)

        val uploader = videoInsert.mediaHttpUploader
        uploader.isDirectUploadEnabled = false
        uploader.progressListener = MediaHttpUploaderProgressListener {
            // TODO: implement progress display
        }

        val returnedVideo = videoInsert.execute()

        // add thumbnailFile
        if(uploadData.thumbnailFile != null) {
            val thumbnailSet = youTube.thumbnails().set(returnedVideo.id, InputStreamContent("image/*", uploadData.thumbnailFile!!.inputStream()))
            thumbnailSet.mediaHttpUploader.isDirectUploadEnabled = false
            thumbnailSet.mediaHttpUploader.progressListener = MediaHttpUploaderProgressListener {
                // TODO: implement simple notification
            }

            thumbnailSet.execute()
        }
    }

}