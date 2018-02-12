package ui.alert

import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.layout.Region


/**
 * Extension for Alert, that fixes the small text pane.
 * @see <a href="https://stackoverflow.com/questions/28937392/javafx-alerts-and-their-size">stackoverflow on Alerts and their size</a>
 */
class SizedAlert(alertType: AlertType?, contentText: String?, vararg buttons: ButtonType?) : Alert(alertType, contentText, *buttons) {

    init {
        dialogPane.minHeight = Region.USE_PREF_SIZE
    }
}