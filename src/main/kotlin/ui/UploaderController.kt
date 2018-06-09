package ui

import entity.Placeholder
import entity.UploadJob
import entity.UploadTemplate
import exec.logger
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.control.TableColumn.CellEditEvent
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.text.Text
import template.fill.PlaceholderUpdateService.updatePlaceholders
import ui.alert.SizedAlert
import upload.UploadService
import upload.UploadService.removeFromQueueWithTab
import upload.UploadService.scheduleUpload
import upload.resumable.RestorableUpload
import youtube.video.PrivacyStatus
import java.net.URL
import java.time.LocalDateTime
import java.util.*


class UploaderController : Initializable {

    companion object {
        var toRestore = RestorableUpload()
    }

    @FXML
    lateinit var tab: Tab

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
    lateinit var publishDate: DatePicker
    @FXML
    lateinit var publishHour: Spinner<Int>
    @FXML
    lateinit var publishMinute: Spinner<Int>
    @FXML
    lateinit var privacyStatus: ChoiceBox<PrivacyStatus>

    private var uploadTemplate = UploadTemplate()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        try {
            // restore data from pool
            val restorable = toRestore

            uploadTemplate = UploadTemplate(restorable)

            titlePreview.text = restorable.title
            descriptionPreview.text = restorable.description
            tagsPreview.text = restorable.tags.joinToString(", ")
            placeholderTable.items.addAll(restorable.placeholders)

            restorable.thumbnailFile?.inputStream()?.use { thumbnailPreview.image = Image(it) }

            // set publish time
            val now = LocalDateTime.now()
            publishDate.value = now.toLocalDate()
            publishHour.valueFactory.value = now.hour
            publishMinute.valueFactory.value = now.minute

            tab.textProperty().bind(titlePreview.textProperty())

            privacyStatus.valueProperty().addListener { _, _, newValue ->
                val scheduleDisabled = newValue != PrivacyStatus.SCHEDULED
                publishDate.isDisable = scheduleDisabled
                publishHour.isDisable = scheduleDisabled
                publishMinute.isDisable = scheduleDisabled
            }
            privacyStatus.value = restorable.privacyStatus
            tab.textProperty().bind(titlePreview.textProperty())
        } catch (e: Exception) {
            logger.error("An unhandled Exception occurred!", e)
        }
    }

    @FXML
    private fun handleCloseRequest(event: Event) {
        try {
            if (UploadService.uploadingTab(tab)) {
                // TODO: implement
                SizedAlert(Alert.AlertType.ERROR, "Removing the currently running upload is not supported.", ButtonType.OK)
            } else {
                // TODO: implement
                removeFromQueueWithTab(tab)
            }
        } catch (e: Exception) {
            logger.error("An unhandled Exception occurred!", e)
        }
    }

    @FXML
    private fun handleUploadStartAction(event: ActionEvent) {
        try {
            lockUI()

            val publishDateTime = publishDate.value.atTime(publishHour.value, publishMinute.value)
            scheduleUpload(UploadJob(uploadTemplate, placeholderTable.items, uploadProgress.progressProperty(), progressText.textProperty(), tab, publishDateTime, scheduledPublish.isSelected))
        } catch (e: Exception) {
            logger.error("An unhandled Exception occurred!", e)
        }
    }

    @FXML
    private fun handlePlaceholderUpdate(event: CellEditEvent<Placeholder, String>) {
        try {
            updatePlaceholders(event, titlePreview, descriptionPreview, uploadTemplate)
        } catch (e: Exception) {
            logger.error("An unhandled Exception occurred!", e)
        }
    }

    private fun lockUI() {
        placeholderTable.isDisable = true
        startButton.isDisable = true
    }

}