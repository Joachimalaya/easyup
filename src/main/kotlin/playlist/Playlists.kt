package playlist

import auth.Authorization
import com.google.api.services.youtube.model.Playlist
import exec.logger
import java.net.UnknownHostException

object Playlists {

    var playlists = listOf<Playlist>()
        private set

    /**
     * This method updates the [playlists] Lists with data from the YouTube servers.
     * Use sparingly, as this costs quota and network requests are slow.
     */
    fun read() {
        try {
            playlists = Authorization.connection.playlists().list("contentDetails,snippet,status").setMine(true).setMaxResults(50).execute().items.toList()
        } catch (uhe: UnknownHostException) {
            logger.error("Could not connect to google servers. List of playlists not available.", uhe)
        }
    }

}