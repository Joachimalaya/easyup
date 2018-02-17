package youtube.video

enum class ByteSize(val factor: Int) {

    KILOBYTE(1024),
    MEGABYTE(KILOBYTE.factor * KILOBYTE.factor),
    GIGABYTE(MEGABYTE.factor * MEGABYTE.factor)

}

fun numBytes(numUnit: Int, unit: ByteSize): Int = numUnit * unit.factor