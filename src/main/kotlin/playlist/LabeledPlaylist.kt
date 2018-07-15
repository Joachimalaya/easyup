package playlist

import com.google.api.services.youtube.model.Playlist

data class LabeledPlaylist(val playlist: Playlist) {

    override fun toString(): String = playlist.snippet.title

}