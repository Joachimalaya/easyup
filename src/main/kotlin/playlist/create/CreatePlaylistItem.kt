package playlist.create

import youtube.video.PrivacyStatus

data class CreatePlaylistItem(val title: String, val description: String, val tags: List<String>, val privacyStatus: PrivacyStatus)