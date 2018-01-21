package ui

import config.jsonMapper
import entity.UploadDataTemplate
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import javafx.stage.FileChooser
import java.net.URL
import java.util.*

class TemplateEditorController : Initializable {

    @FXML
    lateinit var pane: Pane

    @FXML
    lateinit var titleInput: TextField
    @FXML
    lateinit var descriptionInput: TextArea
    @FXML
    lateinit var tagsInput: TextField

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        // NOP
    }

    fun handleLoad(actionEvent: ActionEvent) {
        val answer = Alert(Alert.AlertType.INFORMATION, "Loading a template will discard all unsaved changes.\nAre you sure you want to load a template file?", ButtonType.YES, ButtonType.NO).showAndWait()
        if (answer.isPresent && answer.get() == ButtonType.YES) {
            val chooser = FileChooser()
            val targetFile = chooser.showOpenDialog(pane.scene.window)

            targetFile.inputStream().use {
                val template = jsonMapper.readValue(it, UploadDataTemplate::class.java)
                titleInput.text = template.titleTemplate
                descriptionInput.text = template.descriptionTemplate
                tagsInput.text = template.tags.joinToString(", ")
            }
        }
    }

    fun handleSave(actionEvent: ActionEvent) {
        val chooser = FileChooser()

        val targetFile = chooser.showSaveDialog(pane.scene.window)
        if (targetFile != null) {
            val tags = tagsInput.text.split(",").map { it.trim() }.toTypedArray()
            val template = UploadDataTemplate(titleInput.text, descriptionInput.text, "", tags)
            targetFile.outputStream().use { jsonMapper.writeValue(it, template) }
        }

    }
}