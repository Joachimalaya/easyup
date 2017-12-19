package template.read

import com.google.common.base.Strings
import com.google.common.collect.Lists
import entity.Placeholder
import entity.UploadDataTemplate
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.TableView
import javafx.scene.control.TextInputControl
import javafx.stage.FileChooser
import javafx.stage.Window
import org.codehaus.jackson.JsonFactory
import org.codehaus.jackson.map.ObjectMapper
import java.io.File
import java.util.regex.Pattern

val formatPattern = Pattern.compile("\\{(\\w*)\\}")

// TODO: rename
class PrepareUploadService {

    fun handleUploadAction(window: Window, titlePreview: TextInputControl, descriptionPreview: TextInputControl, placeholderTable: TableView<Placeholder>) {
        val videoFile = askForVideoFile(window)
        val template = askForTemplate(window)

        if (videoFile == null || template == null) {
            Alert(Alert.AlertType.ERROR, "You need to provide both a video file and a template.", ButtonType.OK).showAndWait()
            return
        }

        titlePreview.text = template.titleTemplate
        descriptionPreview.text = template.descriptionTemplate

        placeholderTable.items.addAll((findAllPlaceholders(template.titleTemplate) + findAllPlaceholders(template.titleTemplate)).distinct())

        titlePreview.isDisable = false
        descriptionPreview.isDisable = false
        placeholderTable.isDisable = false

    }

    private fun findAllPlaceholders(template: String): List<Placeholder> {
        val placeholderList = Lists.newArrayList<Placeholder>()
        val titleMatcher = formatPattern.matcher(template)
        while (titleMatcher.find()) {
            val key = titleMatcher.group(1)
            if(!Strings.isNullOrEmpty(key)) {
                placeholderList += Placeholder(key, "")
            }
        }

        return placeholderList
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
            val mapper = ObjectMapper(JsonFactory())
            mapper.readValue(templateFile, UploadDataTemplate::class.java)
        } else {
            null
        }
    }

}

