package ui.tab

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.control.Tab

class AddTab : Tab() {

    init {
        isClosable = false
        text = "Home"

        content = FXMLLoader.load<Parent>(javaClass.getResource("../HomeTab.fxml"))
    }

}