package ui

import exec.logger
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.Pane
import ui.dialog.PlaylistCreator
import ui.dialog.TemplateEditor
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
            UnfinishedUploadLoadService.deleteUnfinishedUploads()
        } catch (e: Exception) {
            logger.error("An unhandled Exception occurred!", e)
        }
    }

    @FXML
    private fun handleTemplateOpen() {
        TemplateEditor.stage.show()
    }

    @FXML
    private fun handlePlaylistCreateOpen() {
        PlaylistCreator.stage.show()
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