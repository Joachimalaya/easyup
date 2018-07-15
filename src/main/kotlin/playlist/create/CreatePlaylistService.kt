package playlist.create

import auth.Authorization
import com.google.api.services.youtube.model.Playlist
import com.google.api.services.youtube.model.PlaylistSnippet
import com.google.api.services.youtube.model.PlaylistStatus
import playlist.Playlists

object CreatePlaylistService {

    fun createPlaylist(toCreate: CreatePlaylistItem) {
        val playlist = Playlist()

        playlist.snippet = PlaylistSnippet()
        playlist.snippet.title = toCreate.title
        playlist.snippet.description = toCreate.description
        playlist.snippet.tags = toCreate.tags

        playlist.status = PlaylistStatus()
        playlist.status.privacyStatus = toCreate.privacyStatus.privacyStatus

        Authorization.connection.playlists().insert("snippet,status", playlist).execute()

        Playlists.read()
    }
}