package ui

import entity.Placeholder
import entity.UploadData
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.control.TableColumn.CellEditEvent
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import template.fill.PlaceholderUpdateService
import template.read.PrepareUploadService
import upload.UploadService
import java.net.URL
import java.util.*

class MainController : Initializable {

    @FXML
    var titlePreview: TextField = TextField()
    @FXML
    var descriptionPreview: TextArea = TextArea()
    @FXML
    var placeholderTable: TableView<Placeholder> = TableView()
    @FXML
    var tagsPreview: TextField = TextField()
    @FXML
    var thumbnailPreview: Pane = Pane()
    @FXML
    var uploadProgress: ProgressBar = ProgressBar()
    @FXML
    var progressText: Text = Text()

    private val prepareUploadService = PrepareUploadService()
    private val placeholderUpdateService = PlaceholderUpdateService()
    private val uploadService = UploadService()
    private var activeData = UploadData()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        // NOP
    }

    @FXML
    private fun handlePrepareAction(event: ActionEvent) {
        val window = (event.source as Control).scene.window
        activeData = prepareUploadService.handleUploadAction(window, titlePreview, descriptionPreview, placeholderTable, tagsPreview)
    }

    // TODO: lock UI on upload
    @FXML
    private fun handleUploadStartAction(event: ActionEvent) =
            uploadService.beginUpload(activeData, placeholderTable.items, uploadProgress, progressText)

    @FXML
    private fun handlePlaceholderUpdate(event: CellEditEvent<Placeholder, String>) =
            placeholderUpdateService.updatePlaceholders(event, titlePreview, descriptionPreview, activeData)
}