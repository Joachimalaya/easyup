package ui.alert

import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.layout.Region

class SizedAlert(alertType: AlertType?, contentText: String?, vararg buttons: ButtonType?) : Alert(alertType, contentText, *buttons) {

    init {
        dialogPane.minHeight = Region.USE_PREF_SIZE
    }
}