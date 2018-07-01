package youtube.video

import com.google.api.services.youtube.model.Playlist

class LabeledPlaylist(val playlist: Playlist) {

    override fun toString(): String = playlist.snippet.title

}