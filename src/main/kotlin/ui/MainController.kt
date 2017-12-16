package ui

import auth.jsonFactory
import entity.Placeholder
import entity.UploadDataTemplate
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.stage.FileChooser
import javafx.stage.Window
import java.io.File
import java.net.URL
import java.util.*

class MainController : Initializable {

    @FXML
    var titlePreview: TextField = TextField()

    @FXML
    var descriptionPreview: TextArea = TextArea()

    @FXML
    var keyTable: TableView<Placeholder> = TableView()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        // NOP
    }

    @FXML
    private fun handleUploadAction(event: ActionEvent) {
        val window = (event.source as Control).scene.window
        val videoFile = askForVideoFile(window)
        val template = askForTemplate(window)

        if (videoFile == null || template == null) {
            Alert(Alert.AlertType.ERROR, "You need to provide both a video file and a template.", ButtonType.OK).showAndWait()
            return
        }

        titlePreview.text = template.titleTemplate
        descriptionPreview.text = template.descriptionTemplate

    }

    private fun askForVideoFile(window: Window): File? {
        val chooser = FileChooser()
        chooser.title = "Select video to upload"

        return chooser.showOpenDialog(window)
    }

    private fun askForTemplate(window: Window): UploadDataTemplate? {
        val chooser = FileChooser()
        chooser.title = "Select a template file"

        chooser.extensionFilters.add(FileChooser.ExtensionFilter("template definitions (JSON)", "*.json"))

        val templateFile = chooser.showOpenDialog(window)
        return if (templateFile != null) {
            jsonFactory.fromInputStream(templateFile.inputStream(), UploadDataTemplate::class.java)
        } else {
            null
        }

    }

}