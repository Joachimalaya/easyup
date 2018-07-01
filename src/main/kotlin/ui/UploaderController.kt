package ui

import com.google.api.services.youtube.model.Playlist
import entity.Placeholder
import entity.UploadJob
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
import youtube.video.Playlists
import youtube.video.PrivacyStatus
import java.io.FileNotFoundException
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
    @FXML
    lateinit var addToPlaylist: CheckBox
    @FXML
    lateinit var playlist: ChoiceBox<Playlist>

    private var inputData = RestorableUpload()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        try {
            inputData = toRestore

            titlePreview.text = toRestore.title
            descriptionPreview.text = toRestore.description
            tagsPreview.text = toRestore.tags.joinToString(", ")
            placeholderTable.items.addAll(toRestore.placeholders)

            toRestore.thumbnailFile?.inputStream()?.use { thumbnailPreview.image = Image(it) }

            // set publish time
            val now = LocalDateTime.now()
            publishDate.value = now.toLocalDate()
            publishHour.valueFactory.value = now.hour
            publishMinute.valueFactory.value = now.minute

            tab.textProperty().bind(titlePreview.textProperty())

            addToPlaylist.isSelected = false
            addToPlaylist.selectedProperty().addListener { _, _, newValue -> playlist.isDisable = !newValue }

            playlist.items.addAll(Playlists.playlists)
            playlist.isDisable = true

            privacyStatus.valueProperty().addListener { _, _, newValue ->
                val scheduleDisabled = newValue != PrivacyStatus.SCHEDULED
                publishDate.isDisable = scheduleDisabled
                publishHour.isDisable = scheduleDisabled
                publishMinute.isDisable = scheduleDisabled
            }
            privacyStatus.value = toRestore.privacyStatus
            tab.textProperty().bind(titlePreview.textProperty())

            updatePlaceholders(toRestore.placeholders, titlePreview, descriptionPreview, inputData)
        } catch (fnfe: FileNotFoundException) {
            SizedAlert(
                    Alert.AlertType.ERROR,
                    "One of the files needed to restore the upload could not be loaded." +
                            "\nFollowing files were looked for:" +
                            "\n${toRestore.thumbnailFile}" +
                            "\n${toRestore.videoFile}" +
                            "\nBecause those files are necessary to continue, this restoration will be aborted.",
                    ButtonType.OK
            ).showAndWait()
            logger.error("file referenced at upload restoration not found", fnfe)
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
            scheduleUpload(UploadJob(
                    placeholderTable.items,
                    uploadProgress.progressProperty(),
                    progressText.textProperty(),
                    tab,
                    publishDateTime,
                    privacyStatus.value,
                    if (addToPlaylist.isSelected) playlist.value else null,
                    inputData
            ))
        } catch (e: Exception) {
            logger.error("An unhandled Exception occurred!", e)
        }
    }

    @FXML
    private fun handlePlaceholderUpdate(event: CellEditEvent<Placeholder, String>) {
        try {
            updatePlaceholders(event, titlePreview, descriptionPreview, inputData)
        } catch (e: Exception) {
            logger.error("An unhandled Exception occurred!", e)
        }
    }

    private fun lockUI() {
        placeholderTable.isDisable = true
        startButton.isDisable = true
    }

}