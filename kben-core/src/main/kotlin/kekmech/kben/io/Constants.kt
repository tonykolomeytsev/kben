package kekmech.kben.io

internal object Constants {

    val STRING_SEPARATOR = ":".toByteArray()[0].toInt()
    val START_INTEGER = "i".toByteArray()[0].toInt()
    val START_LIST = "l".toByteArray()[0].toInt()
    val START_DICTIONARY = "d".toByteArray()[0].toInt()
    val END = "e".toByteArray()[0].toInt()
}