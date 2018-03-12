package template.read

import com.google.common.collect.Lists
import config.activeConfig
import config.jsonMapper
import entity.Placeholder
import entity.RawUploadTemplate
import entity.UploadTemplate
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.TableView
import javafx.scene.control.TextInputControl
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.FileChooser
import javafx.stage.Window
import java.io.File
import java.util.regex.Pattern

val formatPattern = Pattern.compile("\\{(\\w*)}")!!

/**
 * Asks the user for input files. These will then be parsed and prepared for upload.
 */
object PrepareUploadService {

    fun handleLoadAction(window: Window, titlePreview: TextInputControl, descriptionPreview: TextInputControl, placeholderTable: TableView<Placeholder>, tagsPreview: TextInputControl, thumbnailPreview: ImageView): UploadTemplate {
        val videoFile = askForVideoFile(window)
        val template = askForTemplate(window)
        val thumbnailFile = askForThumbnail(window)

        if (videoFile == null || template == null) {
            Alert(Alert.AlertType.ERROR, "You need to provide both a video file and a template.", ButtonType.OK).showAndWait()
            return UploadTemplate()
        }

        thumbnailFile?.inputStream()?.use { thumbnailPreview.image = Image(it) }

        titlePreview.text = template.titleTemplate
        descriptionPreview.text = template.descriptionTemplate
        tagsPreview.text = template.tags.joinToString(", ")

        placeholderTable.items.addAll((findAllPlaceholders(template.titleTemplate) + findAllPlaceholders(template.descriptionTemplate)).distinct())

        titlePreview.isDisable = false
        descriptionPreview.isDisable = false
        placeholderTable.isDisable = false
        tagsPreview.isDisable = false

        return UploadTemplate(template, videoFile, thumbnailFile)
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

        chooser.extensionFilters.addAll(supportedVideoFormats + allFilesFilter)
        chooser.selectedExtensionFilter = chooser.extensionFilters[activeConfig.preferredVideoFormatIndex]

        val videoFile = chooser.showOpenDialog(window)
        activeConfig.updateLastVisitedDirectory(videoFile)
        activeConfig.preferredVideoFormatIndex = chooser.extensionFilters.indexOf(chooser.selectedExtensionFilter)

        return videoFile
    }

    private fun askForTemplate(window: Window): RawUploadTemplate? {
        val chooser = FileChooser()
        chooser.title = "Select a template file"
        chooser.initialDirectory = activeConfig.lastVisitedDirectory
        chooser.extensionFilters.addAll( //
                FileChooser.ExtensionFilter("template definitions (JSON)", "*.json"), //
                FileChooser.ExtensionFilter("all files", "*.*"))

        val templateFile = chooser.showOpenDialog(window)
        activeConfig.updateLastVisitedDirectory(templateFile)
        return if (templateFile != null) {
            jsonMapper.readValue(templateFile, RawUploadTemplate::class.java)
        } else {
            null
        }
    }

    private fun askForThumbnail(window: Window): File? {
        val chooser = FileChooser()
        chooser.initialDirectory = activeConfig.lastVisitedDirectory
        chooser.title = "Select a thumbnail"
        chooser.extensionFilters.addAll(supportedThumbnailFormats + allFilesFilter)

        val thumbnailFile = chooser.showOpenDialog(window)
        activeConfig.updateLastVisitedDirectory(thumbnailFile)
        return thumbnailFile
    }

    val supportedVideoFormats = listOf(FileChooser.ExtensionFilter("QuickTime Movie", "*.mov"),
            FileChooser.ExtensionFilter("MP4", "*.mp4", "*.mpeg4"),
            FileChooser.ExtensionFilter("Audio Video Interleaved", "*.avi"),
            FileChooser.ExtensionFilter("Windows Media Video", "*.wmv"),
            FileChooser.ExtensionFilter("Flash Video", "*.flv"))

    private val supportedThumbnailFormats = listOf(FileChooser.ExtensionFilter("PNG", "*.png"))

    private val allFilesFilter = FileChooser.ExtensionFilter("all files", "*.*")


}
