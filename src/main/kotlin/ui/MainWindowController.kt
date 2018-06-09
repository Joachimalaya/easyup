package ui

import exec.logger
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.Pane
import javafx.stage.Modality
import javafx.stage.Stage
import ui.tab.AddUploadTabService
import upload.resumable.UnfinishedUploadLoadService
import java.net.URL
import java.util.*

class MainWindowController : Initializable {

    companion object {
        // can't use a kotlin object, because javafx will want to create a new instance
        var INSTANCE: MainWindowController? = null
    }

    @FXML
    lateinit var rootPane: Pane

    @FXML
    lateinit var tabPane: TabPane

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        try {
            INSTANCE = this

            AddUploadTabService.addUploadTabs(UnfinishedUploadLoadService.loadUnfinishedUploads())
        } catch (e: Exception) {
            logger.error("An unhandled Exception occurred!", e)
        }
    }

    @FXML
    private fun handleTemplateOpen(event: ActionEvent) {
        try {
            val templateEditor = Stage()
            val scene = Scene(FXMLLoader.load(javaClass.getResource("TemplateEditor.fxml")))
            scene.stylesheets.add(javaClass.getResource("application.css").toExternalForm())
            templateEditor.scene = scene
            templateEditor.title = "easyUp Template Editor"

            templateEditor.initOwner(rootPane.scene.window)
            templateEditor.initModality(Modality.APPLICATION_MODAL)
            templateEditor.showAndWait()
        } catch (e: Exception) {
            logger.error("An unhandled Exception occurred!", e)
        }
    }

    fun addTab(tab: Tab) {
        try {
            tabPane.tabs.add(tabPane.tabs.size - 1, tab)
            tabPane.selectionModel.select(tab)
        } catch (e: Exception) {
            logger.error("An unhandled Exception occurred!", e)
        }
    }

}