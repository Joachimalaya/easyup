package ui.tab

import javafx.scene.control.Button
import javafx.scene.control.Tab
import template.read.PrepareUploadService

/**
 * A special tab that allows addition of other tabs with a button in this tab.
 */
class AddTab : Tab() {

    init {
        isClosable = false
        isDisable = true

        styleClass.add("add-tab")

        val addButton = Button("+")

        graphic = addButton
        addButton.setOnMouseClicked {
            // load template
            val window = addButton.scene.window
            PrepareUploadService.handleLoadAction(window)

        }
    }
}