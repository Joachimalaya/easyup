package playlist

import auth.Authorization
import com.google.api.services.youtube.model.Playlist

object Playlists {

    var playlists = listOf<Playlist>()
        private set

    /**
     * This method updates the [playlists] Lists with data from the YouTube servers.
     * Use sparingly, as this costs quota and network requests are slow.
     */
    fun read() {
        playlists = Authorization.connection.playlists().list("contentDetails,snippet,status").setMine(true).setMaxResults(50).execute().items.toList()
    }

}