package ui

import entity.Placeholder
import entity.UploadJob
import entity.UploadTemplate
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.control.TableColumn.CellEditEvent
import javafx.scene.image.ImageView
import javafx.scene.text.Text
import template.fill.PlaceholderUpdateService.updatePlaceholders
import template.read.PrepareUploadService.handleLoadAction
import upload.UploadService.scheduleUpload
import java.net.URL
import java.time.LocalDate
import java.util.*


class UploaderController : Initializable {

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

    private var activeData = UploadTemplate()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        publishDate.value = LocalDate.now()
    }

    @FXML
    private fun handlePrepareAction(event: ActionEvent) {
        val window = (event.source as Control).scene.window
        activeData = handleLoadAction(window, titlePreview, descriptionPreview, placeholderTable, tagsPreview, thumbnailPreview)
    }

    @FXML
    private fun handleUploadStartAction(event: ActionEvent) {
        lockUI()
        activeData.publishDate = publishDate.value.atTime(publishHour.value, publishMinute.value)
        activeData.scheduledPublish = scheduledPublish.isSelected
        scheduleUpload(UploadJob(activeData, placeholderTable.items, uploadProgress.progressProperty(), progressText.textProperty()))
    }

    @FXML
    private fun handlePlaceholderUpdate(event: CellEditEvent<Placeholder, String>) =
            updatePlaceholders(event, titlePreview, descriptionPreview, activeData)

    private fun lockUI() {
        placeholderTable.isDisable = true
        prepareButton.isDisable = true
        startButton.isDisable = true
    }

}