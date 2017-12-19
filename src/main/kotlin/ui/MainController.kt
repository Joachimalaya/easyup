package ui

import entity.Placeholder
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Control
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import template.read.PrepareUploadService
import java.net.URL
import java.util.*

class MainController : Initializable {

    @FXML
    var titlePreview: TextField = TextField()

    @FXML
    var descriptionPreview: TextArea = TextArea()

    @FXML
    var placeholderTable: TableView<Placeholder> = TableView()

    // TODO: rename
    private val prepareUploadService = PrepareUploadService()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        // NOP
    }

    @FXML
    private fun handleUploadAction(event: ActionEvent) {
        val window = (event.source as Control).scene.window
        prepareUploadService.handleUploadAction(window, titlePreview, descriptionPreview, placeholderTable)
    }
}