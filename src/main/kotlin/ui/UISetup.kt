package ui

import config.activeConfig
import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.stage.Stage
import ui.alert.SizedAlert
import upload.UploadService

class UISetup : Application() {

    private val layoutPath = "Uploader.fxml"
    private val cssConfig = "application.css"

    override fun start(primaryStage: Stage) {
        val root = FXMLLoader.load<Parent>(javaClass.getResource(layoutPath))
        val scene = Scene(root)
        scene.stylesheets.add(javaClass.getResource(cssConfig).toExternalForm())
        primaryStage.scene = scene
        primaryStage.minWidth = 720.0
        primaryStage.minHeight = 640.0
        primaryStage.isMaximized = true
        primaryStage.show()
        primaryStage.title = "easyUp"


        primaryStage.onCloseRequest = EventHandler {
            // TODO: check whether upload is running before showing this
            // security question for user
            val reallyClose = SizedAlert(Alert.AlertType.WARNING, "Closing the application while an upload is running means all progress will be lost.\nStill quit?", ButtonType.YES, ButtonType.NO).showAndWait()
            if (reallyClose.isPresent && reallyClose.get() == ButtonType.YES) {
                activeConfig.writeToDefault()

                // stop uploading thread
                // TODO: wrap cancel request in method
                UploadService.cancelUpload = true
            } else {
                it.consume()
            }
        }
    }

    fun launchApp(args: Array<String>) {
        launch(*args)
    }

}
