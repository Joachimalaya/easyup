package upload

import com.google.api.client.googleapis.media.MediaHttpUploader
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener
import com.google.api.client.http.InputStreamContent
import com.google.api.services.youtube.model.Video
import com.google.api.services.youtube.model.VideoSnippet
import com.google.api.services.youtube.model.VideoStatus
import com.google.common.collect.Lists
import entity.UploadData
import exec.youTube
import youtube.video.PrivacyStatus

class UploadService {

    fun beginUpload(uploadData: UploadData) {
        val video = Video()

        video.status = VideoStatus()
        video.status.privacyStatus = PrivacyStatus.PRIVATE.privacyStatus

        video.snippet = VideoSnippet()
        video.snippet.title = uploadData.title
        video.snippet.description = uploadData.description

        video.snippet.tags = Lists.newArrayList("bla", "test")

        val mediaContent = InputStreamContent("video/*", uploadData.videoFile.inputStream())

        val videoInsert = youTube.videos().insert("snippet,statistics,status", video, mediaContent)

        val uploader = videoInsert.mediaHttpUploader
        uploader.isDirectUploadEnabled = false

        val progressListener = MediaHttpUploaderProgressListener() {
            fun progressChanged(uploader: MediaHttpUploader) {
                // TODO: implement progress display
            }
        }

        uploader.progressListener = progressListener

        val returnedVideo = videoInsert.execute()
        // TODO: does calling execute() block program flow?
    }

}