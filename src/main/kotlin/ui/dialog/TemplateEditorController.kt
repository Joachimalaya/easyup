package ui.dialog

import config.activeConfig
import config.jsonMapper
import entity.RawUploadTemplate
import exec.logger
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import javafx.stage.FileChooser
import javafx.stage.Stage
import ui.alert.SizedAlert
import java.io.File
import java.net.URL
import java.util.*

class TemplateEditorController : Initializable {

    @FXML
    lateinit var rootPane: Pane

    @FXML
    lateinit var titleInput: TextField
    @FXML
    lateinit var descriptionInput: TextArea
    @FXML
    lateinit var tagsInput: TextField

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        // NOP
    }

    @FXML
    private fun handleLoad() {
        try {
            val answer = SizedAlert(Alert.AlertType.INFORMATION, "Loading a inputData will discard all unsaved changes.\nAre you sure you want to load a inputData file?", ButtonType.YES, ButtonType.NO).showAndWait()
            if (answer.isPresent && answer.get() == ButtonType.YES) {
                val chooser = FileChooser()
                val targetFile: File? = chooser.showOpenDialog(rootPane.scene.window)

                if (targetFile != null) {
                    targetFile.inputStream()
                            .use {
                                val template = jsonMapper.readValue(it, RawUploadTemplate::class.java)
                                titleInput.text = template.titleTemplate
                                descriptionInput.text = template.descriptionTemplate
                                tagsInput.text = template.tags.joinToString(", ")
                            }
                    activeConfig.lastVisitedTemplateDirectory = targetFile.parentFile
                }
            }
        } catch (e: Exception) {
            logger.error("An unhandled Exception occurred!", e)
        }
    }

    @FXML
    private fun handleSave() {
        try {
            val chooser = FileChooser()

            val targetFile = chooser.showSaveDialog(rootPane.scene.window)
            if (targetFile != null) {
                val tags = tagsInput.text.split(",").map { it.trim() }.toTypedArray()
                val template = RawUploadTemplate(titleInput.text, descriptionInput.text, "", tags)
                targetFile.outputStream().use { jsonMapper.writeValue(it, template) }
                activeConfig.lastVisitedTemplateDirectory = targetFile.parentFile
            }
        } catch (e: Exception) {
            logger.error("An unhandled Exception occurred!", e)
        }
    }

    @FXML
    private fun handleCancel() = (rootPane.scene.window as Stage).close()
}