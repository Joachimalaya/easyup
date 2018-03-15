package entity

/**
 * A simple mapping container.
 */
data class Placeholder(var key: String, var value: String) {

    // empty constructor for JSON deserialization
    constructor() : this("", "")

}