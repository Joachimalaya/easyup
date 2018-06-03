package ui

import config.activeConfig
import exec.logger
import javafx.application.Application
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.stage.Stage
import ui.alert.SizedAlert
import upload.UploadService

class MainWindow : Application() {

    companion object {
        // can't use a kotlin object, because javafx calls MainWindow()
        var INSTANCE: MainWindow? = null

        val TITLEPREFIX = "easyUp"
    }

    private val layoutPath = "MainWindow.fxml"

    private val cssFiles = listOf(
            javaClass.getResource("application.css").toExternalForm(),
            javaClass.getResource("addTab.css").toExternalForm())

    var windowStage: Stage? = null

    override fun start(primaryStage: Stage) {
        try {

            INSTANCE = this

            windowStage = primaryStage
            val root = FXMLLoader.load<Parent>(javaClass.getResource(layoutPath))
            val scene = Scene(root)

            cssFiles.forEach { scene.stylesheets.add(it) }

            primaryStage.scene = scene
            primaryStage.minWidth = 720.0
            primaryStage.minHeight = 640.0
            primaryStage.isMaximized = true
            primaryStage.show()
            primaryStage.title = TITLEPREFIX

            primaryStage.onCloseRequest = EventHandler {
                // write always; does no harm
                activeConfig.writeToDefault()

                // check whether upload is running before showing this
                if (UploadService.uploading()) {
                    // security question for user
                    val reallyClose = SizedAlert(Alert.AlertType.WARNING, "Closing the application while an upload is running means all progress will be lost.\nStill quit?", ButtonType.YES, ButtonType.NO).showAndWait()
                    if (reallyClose.isPresent && reallyClose.get() == ButtonType.YES) {
                        // stop uploading thread
                        // TODO: wrap cancel request in method
                        UploadService.cancelUpload = true
                    } else {
                        it.consume()
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("An unhandled exception occurred", e)
        }
    }

    /**
     * Add some text to the usual easyUp in the title of the application.
     *
     * This method is safe to call from non-event Threads.
     */
    fun changeTitle(titleSuffix: String) {
        Platform.runLater { windowStage?.title = "$TITLEPREFIX - $titleSuffix" }
    }

    /**
     * Reset the title of the application to the default.
     *
     * This method is safe to call from non-event Threads.
     */
    fun resetTitle() {
        Platform.runLater { windowStage?.title = TITLEPREFIX }
    }

    fun launchApp(args: Array<String>) {
        launch(*args)
    }

}
