package youtube.video

/**
 * A list of the standardized binary prefixes used to denote memory size.
 */
enum class BinaryPrefix(val factor: Int) {

    KIBIBYTE(1024),
    MEBIBYTE(Math.pow(1024.0, 2.0).toInt()),
    GIBIBYTE(Math.pow(1024.0, 3.0).toInt())

}

/**
 * Gives the number of bytes for a given amount of binary prefix.
 */
fun numBytes(numUnit: Int, unit: BinaryPrefix): Int = numUnit * unit.factor