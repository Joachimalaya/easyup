package ui

import entity.Placeholder
import entity.UploadJob
import entity.UploadTemplate
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.control.TableColumn.CellEditEvent
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.text.Text
import template.fill.PlaceholderUpdateService.updatePlaceholders
import template.read.PrepareUploadService.handleLoadAction
import upload.UploadService.scheduleUpload
import upload.resumable.RestorableUpload
import java.net.URL
import java.time.LocalDateTime
import java.util.*


class UploaderController : Initializable {

    companion object {
        var toRestore: List<RestorableUpload> = emptyList()
    }

    @FXML
    lateinit var tab: Tab

    @FXML
    lateinit var prepareButton: Button
    @FXML
    lateinit var startButton: Button

    @FXML
    lateinit var titlePreview: TextField
    @FXML
    lateinit var descriptionPreview: TextArea
    @FXML
    lateinit var placeholderTable: TableView<Placeholder>
    @FXML
    lateinit var tagsPreview: TextField
    @FXML
    lateinit var uploadProgress: ProgressBar
    @FXML
    lateinit var progressText: Text
    @FXML
    lateinit var thumbnailPreview: ImageView
    @FXML
    lateinit var scheduledPublish: CheckBox
    @FXML
    lateinit var publishDate: DatePicker
    @FXML
    lateinit var publishHour: Spinner<Int>
    @FXML
    lateinit var publishMinute: Spinner<Int>

    private var uploadTemplate = UploadTemplate()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        if (toRestore.isNotEmpty()) {
            // restore data from pool
            val restorable = toRestore[0]
            toRestore -= restorable

            uploadTemplate = UploadTemplate(restorable)

            titlePreview.text = restorable.title
            descriptionPreview.text = restorable.description
            tagsPreview.text = restorable.tags.joinToString(", ")
            placeholderTable.items.addAll(restorable.placeholders)

            restorable.thumbnailFile?.inputStream()?.use { thumbnailPreview.image = Image(it) }
        } else {
            // set publish time
            val now = LocalDateTime.now()
            publishDate.value = now.toLocalDate()
            publishHour.valueFactory.value = now.hour
            publishMinute.valueFactory.value = now.minute

        }
    }

    @FXML
    private fun handlePrepareAction(event: ActionEvent) {
        val window = (event.source as Control).scene.window
        uploadTemplate = handleLoadAction(window, titlePreview, descriptionPreview, placeholderTable, tagsPreview, thumbnailPreview)
    }

    @FXML
    private fun handleUploadStartAction(event: ActionEvent) {
        lockUI()

        val publishDateTime = publishDate.value.atTime(publishHour.value, publishMinute.value)
        scheduleUpload(UploadJob(uploadTemplate, placeholderTable.items, uploadProgress.progressProperty(), progressText.textProperty(), tab, publishDateTime, scheduledPublish.isSelected))
    }

    @FXML
    private fun handlePlaceholderUpdate(event: CellEditEvent<Placeholder, String>) =
            updatePlaceholders(event, titlePreview, descriptionPreview, uploadTemplate)

    private fun lockUI() {
        placeholderTable.isDisable = true
        prepareButton.isDisable = true
        startButton.isDisable = true
    }

}