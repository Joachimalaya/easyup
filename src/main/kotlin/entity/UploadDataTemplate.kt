package entity

data class UploadDataTemplate (var titleTemplate: String, var descriptionTemplate: String, var game: String) {

    // empty constructor needed for JSON deserialization
    constructor() : this("", "", "")
}