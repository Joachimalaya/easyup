package upload.resumable

import config.jsonMapper
import entity.UploadData
import exec.appDirectory
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.io.File

val unfinishedUploadDirectory = File("$appDirectory/uploads")

object UnfinishedUploadLoadService {

    private fun findUnfinishedUpload(): File? =
            unfinishedUploadDirectory.listFiles({ _, name -> name.endsWith(".json") }).firstOrNull()

    fun loadUnfinishedUpload(titlePreview: TextField, descriptionPreview: TextArea, tags: TextField, thumbnailPreview: ImageView): UploadData {
        val unfinishedUpload = findUnfinishedUpload()

        if (unfinishedUpload != null) {
            val retryAlert = Alert(Alert.AlertType.CONFIRMATION, "There seems to be an unfinished upload. Do you want to retry it?")
            val wait = retryAlert.showAndWait()
            if (wait.isPresent && wait.get() == ButtonType.YES) {
                val unfinishedData = unfinishedUpload.inputStream().use { jsonMapper.readValue(it, UploadData::class.java) }
                titlePreview.text = unfinishedData.title
                descriptionPreview.text = unfinishedData.description
                tags.text = unfinishedData.tags.joinToString(", ")

                unfinishedData.thumbnailFile?.inputStream()?.use { thumbnailPreview.image = Image(it) }
                deleteUnfinishedUpload(unfinishedUpload)

                return unfinishedData
            }

            deleteUnfinishedUpload(unfinishedUpload)
        }

        return UploadData()
    }

    private fun deleteUnfinishedUpload(unfinishedUpload: File) {
        unfinishedUpload.delete()

        // TODO: delete partial upload from YouTube

    }


}