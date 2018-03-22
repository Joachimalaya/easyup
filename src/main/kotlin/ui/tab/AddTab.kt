package ui.tab

import javafx.scene.control.Button
import javafx.scene.control.Tab
import javafx.scene.paint.Color
import javafx.scene.paint.Paint

/**
 * A special tab that allows addition of other tabs with a button in this tab.
 */
class AddTab : Tab() {

    init {
        isClosable = false
        isDisable = true


        // TODO: style add button to not look disabled; just want the tab not selectable
        val addButton = Button("+")
        addButton.textFill = Paint.valueOf(Color.LIGHTGREEN.toString())

        graphic = addButton
        graphic.setOnMouseClicked {
            AddUploadTabService.addUploadTab()
        }
    }
}