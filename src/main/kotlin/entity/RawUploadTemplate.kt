package entity

import java.util.*

data class RawUploadTemplate(var titleTemplate: String, var descriptionTemplate: String, var game: String, var tags: Array<String>) {

    // empty constructor needed for JSON deserialization
    constructor() : this("", "", "", arrayOf(""))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RawUploadTemplate

        if (titleTemplate != other.titleTemplate) return false
        if (descriptionTemplate != other.descriptionTemplate) return false
        if (game != other.game) return false
        if (!Arrays.equals(tags, other.tags)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = titleTemplate.hashCode()
        result = 31 * result + descriptionTemplate.hashCode()
        result = 31 * result + game.hashCode()
        result = 31 * result + Arrays.hashCode(tags)
        return result
    }
}