package ui.tab

import javafx.fxml.FXMLLoader
import ui.MainWindowController
import ui.UploaderController
import upload.resumable.RestorableUpload

object AddUploadTabService {

    private val uploaderPath = javaClass.getResource("Uploader.fxml")

    fun addUploadTab(uploadData: RestorableUpload) {
        UploaderController.toRestore = uploadData
        MainWindowController.INSTANCE?.addTab(FXMLLoader.load(uploaderPath))
    }

    fun addUploadTabs(uploads: List<RestorableUpload>) = uploads.forEach(this::addUploadTab)

}