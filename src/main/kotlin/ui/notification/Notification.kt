package ui.notification

import com.google.common.base.Stopwatch
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.layout.GridPane
import javafx.scene.text.Text
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.util.concurrent.TimeUnit

/**
 * Display an undecorated message for a short time in the upper left corner of the main display.
 */
class Notification(message: String) : Stage(StageStyle.UNDECORATED) {

    init {
        x = 0.0
        y = 0.0

        title = "easyUp notification"

        isResizable = false
        isAlwaysOnTop = true

        val rootPane = GridPane()
        rootPane.padding = Insets(50.0, 50.0, 50.0, 50.0)
        rootPane.add(Text(message), 0, 0)


        scene = Scene(rootPane)

    }

    /**
     * This method will fade out and destroy the Notification after the given time.
     */
    fun showAndFadeOut(showTime: Long, fadeoutTime: Long) {
        if (showTime < 0 || fadeoutTime < 0) {
            throw IllegalArgumentException("only positive durations are supported")
        }
        show()

        Thread {
            val elapsedShowTime = Stopwatch.createStarted()
            // show notification; handle spurious wakeups
            while (elapsedShowTime.elapsed(TimeUnit.MILLISECONDS) < showTime) {
                Thread.sleep(showTime)
            }

            val elapsedFadeoutTime = Stopwatch.createStarted()
            while (elapsedFadeoutTime.elapsed(TimeUnit.MILLISECONDS) < fadeoutTime) {
                Platform.runLater { opacity = 1.0 - elapsedFadeoutTime.elapsed(TimeUnit.MILLISECONDS).toDouble() / fadeoutTime.toDouble() }

                Thread.sleep(100)
            }

            Platform.runLater { close() }

        }.start()
    }

}