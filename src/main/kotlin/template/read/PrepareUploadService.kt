package template.read

import com.google.common.collect.Lists
import config.activeConfig
import entity.Placeholder
import entity.UploadData
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

val formatPattern = Pattern.compile("\\{(\\w*)}")!!

/**
 * Asks the user for input files. These will then be parsed and prepared for upload.
 */
class PrepareUploadService {

    fun handleUploadAction(window: Window, titlePreview: TextInputControl, descriptionPreview: TextInputControl, placeholderTable: TableView<Placeholder>, tagsPreview: TextInputControl): UploadData {
        val videoFile = askForVideoFile(window)
        val template = askForTemplate(window)
        val thumbnailFile = askForThumbnail(window)

        if (videoFile == null || template == null) {
            Alert(Alert.AlertType.ERROR, "You need to provide both a video file and a template.", ButtonType.OK).showAndWait()
            return UploadData()
        }

        titlePreview.text = template.titleTemplate
        descriptionPreview.text = template.descriptionTemplate
        tagsPreview.text = template.tags.joinToString(", ")

        placeholderTable.items.addAll((findAllPlaceholders(template.titleTemplate) + findAllPlaceholders(template.descriptionTemplate)).distinct())

        titlePreview.isDisable = false
        descriptionPreview.isDisable = false
        placeholderTable.isDisable = false
        tagsPreview.isDisable = false

        return UploadData(template, videoFile, thumbnailFile)
    }

    private fun findAllPlaceholders(template: String): List<Placeholder> {
        val placeholderList = Lists.newArrayList<Placeholder>()
        val titleMatcher = formatPattern.matcher(template)
        while (titleMatcher.find()) {
            val key = titleMatcher.group(1)
            if (key.isNotBlank()) {
                placeholderList += Placeholder(key, "")
            }
        }
        return placeholderList
    }


    private fun askForVideoFile(window: Window): File? {
        val chooser = FileChooser()
        chooser.initialDirectory = activeConfig.lastVisitedDirectory
        chooser.title = "Select video to upload"

        val videoFile = chooser.showOpenDialog(window)
        activeConfig.updateLastVisitedDirectory(videoFile)
        return videoFile
    }

    private fun askForTemplate(window: Window): UploadDataTemplate? {
        val chooser = FileChooser()
        chooser.title = "Select a template file"
        chooser.initialDirectory = activeConfig.lastVisitedDirectory
        chooser.extensionFilters.addAll( //
                FileChooser.ExtensionFilter("template definitions (JSON)", "*.json"), //
                FileChooser.ExtensionFilter("all files", "*.*"))

        val templateFile = chooser.showOpenDialog(window)
        activeConfig.updateLastVisitedDirectory(templateFile)
        return if (templateFile != null) {
            val mapper = ObjectMapper(JsonFactory())
            mapper.readValue(templateFile, UploadDataTemplate::class.java)
        } else {
            null
        }
    }

    private fun askForThumbnail(window: Window): File? {
        val chooser = FileChooser()
        chooser.initialDirectory = activeConfig.lastVisitedDirectory
        chooser.title = "Select a thumbnail"
        chooser.extensionFilters.addAll(//
                FileChooser.ExtensionFilter("PNG", "*.png"), //
                FileChooser.ExtensionFilter("all files", "*.*"))

        val thumbnailFile = chooser.showOpenDialog(window)
        activeConfig.updateLastVisitedDirectory(thumbnailFile)
        return thumbnailFile
    }
}
