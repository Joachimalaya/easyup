package ui

import config.activeConfig
import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

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
            activeConfig.writeToDefault()
        }
    }

    fun launchApp(args: Array<String>) {
        launch(*args)
    }

}
