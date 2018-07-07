package ui.tab

import javafx.scene.control.Button
import javafx.scene.control.Tab
import javafx.scene.control.Tooltip
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
        addButton.tooltip = Tooltip("add a new upload")

        graphic = addButton
        addButton.setOnMouseClicked {
            // load inputData
            val window = addButton.scene.window
            PrepareUploadService.handleLoadAction(window)

        }
    }
}