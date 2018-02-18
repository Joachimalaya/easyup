package exec

import ui.MainWindow
import java.io.File


val appDirectory = File(System.getProperty("user.home") + "/.easyUp")

fun main(args: Array<String>) {
    // ensure that application directory exists
    appDirectory.createNewFile()

    MainWindow().launchApp(args)
}