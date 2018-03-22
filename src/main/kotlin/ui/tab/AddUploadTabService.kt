package ui.tab

import javafx.fxml.FXMLLoader
import javafx.scene.control.Tab
import ui.MainWindowController
import ui.UploaderController
import upload.resumable.RestorableUpload

object AddUploadTabService {

    private val uploaderPath = javaClass.getResource("../Uploader.fxml")

    fun addUploadTab() {
        val uploadTab = FXMLLoader.load<Tab>(uploaderPath)
        MainWindowController.INSTANCE?.addTab(uploadTab)
    }

    fun addUploadTab(uploads: List<RestorableUpload>) {
        UploaderController.toRestore = uploads

        uploads.forEach {
            val uploadTab = FXMLLoader.load<Tab>(uploaderPath)
            MainWindowController.INSTANCE?.addTab(uploadTab)
        }


    }


}