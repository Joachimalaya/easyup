package ui.dialog

import exec.logger
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.Window
import ui.ResourceConstants

object PlaylistCreator {

    private var backingStage: Stage? = null
    val stage: Stage
        get() = backingStage!!

    fun initialize(parentWindow: Window) {
        if (backingStage == null) {
            try {
                val playlistCreator = Stage()
                val scene = Scene(FXMLLoader.load<Pane?>(ResourceConstants.PLAYLIST_CREATOR))
                scene.stylesheets.add(ResourceConstants.APPLICATION_CSS.toExternalForm())
                playlistCreator.scene = scene
                playlistCreator.title = "easyUp Playlist Creator"

                playlistCreator.initOwner(parentWindow)
                playlistCreator.initModality(Modality.APPLICATION_MODAL)
                playlistCreator.minWidth = 640.0
                playlistCreator.minHeight = 240.0

                backingStage = playlistCreator
                logger.info("playlist creator initialized")
            } catch (e: Exception) {
                logger.error("An unhandled Exception occurred!", e)
            }
        }
    }
}