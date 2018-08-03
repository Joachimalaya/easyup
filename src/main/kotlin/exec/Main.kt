package exec

import org.slf4j.log4j12.Log4jLoggerFactory
import playlist.Playlists
import ui.MainWindow
import java.io.File

val logger = Log4jLoggerFactory().getLogger("easyup")
val appDirectory = File("${System.getProperty("user.home")}/.easyUp")

fun main(args: Array<String>) {
    // ensure that application directory exists
    appDirectory.mkdirs()

    // load playlists eagerly
    Playlists.read()

    MainWindow().launchApp(args)
}