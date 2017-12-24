package exec

import ui.UISetup
import java.io.File


val appDirectory = File(System.getProperty("user.home") + "/.easyUp")

fun main(args: Array<String>) {
    // ensure that application directory exists
    appDirectory.createNewFile()

//     val youTube = Authorization().createAuthorizedYouTube()

    UISetup().launchApp(args)
}