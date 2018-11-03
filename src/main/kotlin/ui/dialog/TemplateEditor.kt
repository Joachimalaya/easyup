package ui.dialog

import exec.logger
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.Window
import ui.ResourceConstants

object TemplateEditor {

    private var backingStage: Stage? = null
    val stage: Stage
        get() = backingStage!!

    fun initialize(parentWindow: Window) {
        if (backingStage == null) {
            try {
                val templateEditor = Stage()
                val scene = Scene(FXMLLoader.load<Pane?>(ResourceConstants.PLAYLIST_CREATOR))
                scene.stylesheets.add(ResourceConstants.APPLICATION_CSS.toExternalForm())
                templateEditor.scene = scene
                templateEditor.title = "easyUp Template Editor"

                templateEditor.initOwner(parentWindow)
                templateEditor.initModality(Modality.APPLICATION_MODAL)
                templateEditor.minWidth = 640.0
                templateEditor.minHeight = 240.0

                backingStage = templateEditor
                logger.info("template editor initialized")
            } catch (e: Exception) {
                logger.error("An unhandled Exception occurred!", e)
            }
        }
    }
}