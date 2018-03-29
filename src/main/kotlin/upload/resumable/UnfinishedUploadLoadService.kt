package upload.resumable

import config.jsonMapper
import exec.appDirectory
import upload.UploadService
import java.io.File

object UnfinishedUploadLoadService {

    val UNFINISHED_UPLOAD_DIRECTORY = File("$appDirectory/uploads")

    fun loadUnfinishedUploads(): List<RestorableUpload> = jsonMapper.readValue(UploadService.UPLOAD_QUEUE_FILE, Array<RestorableUpload>::class.java).asList()

    private fun deleteUnfinishedUpload() {
        if (!UploadService.uploading) {
            // TODO: delete partial upload from YouTube

        }
    }
}