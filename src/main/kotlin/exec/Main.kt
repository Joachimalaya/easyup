package exec

import auth.Authorization
import ui.UISetup
import java.io.File


val appDirectory = File(System.getProperty("user.home") + "/.easyUp")
val youTube = Authorization.createAuthorizedYouTube()

fun main(args: Array<String>) {
    // ensure that application directory exists
    appDirectory.createNewFile()

    UISetup().launchApp(args)
}