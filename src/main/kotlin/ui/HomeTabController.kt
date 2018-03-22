package ui

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.control.Tab
import javafx.scene.layout.Pane
import java.net.URL
import java.util.*

class HomeTabController : Initializable {

    @FXML
    lateinit var rootPane: Pane

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        // TODO: load template of incomplete upload if existing
        // activeData = UnfinishedUploadLoadService.loadUnfinishedUpload(titlePreview, descriptionPreview, tagsPreview, thumbnailPreview, placeholderTable)
    }

    fun addUploadAction(event: ActionEvent) {
        val uploadTab = FXMLLoader.load<Tab>(javaClass.getResource("Uploader.fxml"))

        MainWindowController.INSTANCE?.addTab(uploadTab)
    }


}