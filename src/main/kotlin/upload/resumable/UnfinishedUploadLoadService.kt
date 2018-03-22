package upload.resumable

import config.jsonMapper
import entity.Placeholder
import entity.UploadTemplate
import exec.appDirectory
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import template.fill.PlaceholderUpdateService
import ui.alert.SizedAlert
import upload.UploadService
import java.io.File

// TODO: currently unused; need to open tabs for every unfinished upload
object UnfinishedUploadLoadService {

    val UNFINISHED_UPLOAD_DIRECTORY = File("$appDirectory/uploads")

    private fun findUnfinishedUpload(): File? =
            UNFINISHED_UPLOAD_DIRECTORY.listFiles({ _, name -> name.endsWith(".json") }).firstOrNull()

    fun loadUnfinishedUploads(): List<RestorableUpload> = jsonMapper.readValue(UploadService.UPLOAD_QUEUE_FILE, Array<RestorableUpload>::class.java).asList()

    fun loadUnfinishedUpload(titlePreview: TextField, descriptionPreview: TextArea, tags: TextField, thumbnailPreview: ImageView, placeholderTable: TableView<Placeholder>): UploadTemplate {
        val unfinishedUpload = findUnfinishedUpload()

        if (unfinishedUpload != null) {
            val retryAlert = SizedAlert(Alert.AlertType.CONFIRMATION, "There seems to be an unfinished upload. Do you want to retry it?", ButtonType.YES, ButtonType.NO)
            val wait = retryAlert.showAndWait()
            if (wait.isPresent && wait.get() == ButtonType.YES) {
                val unfinishedData = unfinishedUpload.inputStream().use { jsonMapper.readValue(it, RestorableUpload::class.java) }
                titlePreview.text = PlaceholderUpdateService.replacePlaceholders(unfinishedData.title, unfinishedData.placeholders)
                descriptionPreview.text = PlaceholderUpdateService.replacePlaceholders(unfinishedData.description, unfinishedData.placeholders)
                tags.text = unfinishedData.tags.joinToString(", ")

                unfinishedData.thumbnailFile?.inputStream()?.use { thumbnailPreview.image = Image(it) }

                placeholderTable.items.addAll(unfinishedData.placeholders)

                deleteUnfinishedUpload(unfinishedUpload)

                titlePreview.isDisable = false
                descriptionPreview.isDisable = false
                placeholderTable.isDisable = false
                tags.isDisable = false

                return unfinishedData
            }

            deleteUnfinishedUpload(unfinishedUpload)
        }

        return UploadTemplate()
    }

    private fun deleteUnfinishedUpload(unfinishedUpload: File) {
        unfinishedUpload.delete()

        // TODO: delete partial upload from YouTube

    }


}