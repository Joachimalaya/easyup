package ui.tab

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Tab
import ui.MainWindowController

class AddTab : Tab() {

    init {
        isClosable = false

        graphic = Button("+")
        graphic.setOnMouseClicked {
            val uploadTab = Tab("new upload")
            uploadTab.content = FXMLLoader.load<Parent>(javaClass.getResource("../Uploader.fxml"))
            MainWindowController.INSTANCE?.addTab(uploadTab)
        }

        content = FXMLLoader.load<Parent>(javaClass.getResource("../HomeTab.fxml"))
    }

}