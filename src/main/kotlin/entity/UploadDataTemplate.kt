package entity

data class UploadDataTemplate (var titleTemplate: String, var descriptionTemplate: String, var game: String) {

    constructor() : this("", "", "")
}