package ui

import entity.Placeholder
import entity.UploadData
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.TableColumn.CellEditEvent
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.stage.Modality
import javafx.stage.Stage
import template.fill.PlaceholderUpdateService
import template.read.PrepareUploadService
import upload.UploadService
import java.net.URL
import java.util.*


class UploaderController : Initializable {

    @FXML
    lateinit var prepareButton: Button
    @FXML
    lateinit var startButton: Button

    @FXML
    lateinit var rootPane: Pane

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

    private val prepareUploadService = PrepareUploadService()
    private val placeholderUpdateService = PlaceholderUpdateService()
    private val uploadService = UploadService()
    private var activeData = UploadData()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        // TODO: load data of incomplete upload if existing
    }

    @FXML
    private fun handlePrepareAction(event: ActionEvent) {
        val window = rootPane.scene.window
        activeData = prepareUploadService.handleUploadAction(window, titlePreview, descriptionPreview, placeholderTable, tagsPreview, thumbnailPreview)
    }

    @FXML
    private fun handleUploadStartAction(event: ActionEvent) {
        lockUI()
        uploadService.beginUpload(activeData, placeholderTable.items, uploadProgress, progressText)
    }

    @FXML
    private fun handlePlaceholderUpdate(event: CellEditEvent<Placeholder, String>) =
            placeholderUpdateService.updatePlaceholders(event, titlePreview, descriptionPreview, activeData)

    private fun lockUI() {
        placeholderTable.isDisable = true
        prepareButton.isDisable = true
        startButton.isDisable = true
    }

    @FXML
    private fun handleTemplateOpen(event: ActionEvent) {
        val templateEditor = Stage()
        val scene = Scene(FXMLLoader.load(javaClass.getResource("TemplateEditor.fxml")))
        scene.stylesheets.add(javaClass.getResource("application.css").toExternalForm())
        templateEditor.scene = scene
        templateEditor.title = "easyUp Template Editor"

        templateEditor.initOwner(rootPane.scene.window)
        templateEditor.initModality(Modality.APPLICATION_MODAL)
        templateEditor.showAndWait()
    }

}