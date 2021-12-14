package kekmech.kben.mocks

import kekmech.kben.domain.dto.BencodeElement.*

internal object Mocks {

    private val integers = listOf(1L, 42L, -999L, 0L, -2022L)
    private val strings = listOf("lorem", "ipsum", "dolor", "sit", "amet")

    object IntegerPrimitives {

        val IR = integers.map(::BencodeInteger)
        val RAW = integers.map { "i${it}e" }
    }

    object StringPrimitives {

        val IR = strings.map(::BencodeByteArray)
        val RAW = strings.map { "${it.length}:$it" }
    }

    object ListOfIntegers {

        val IR = BencodeList(integers.map(::BencodeInteger))
        val RAW = integers.joinToBencodeList { "i${it}e" }
    }

    object ListOfStrings {

        val IR = BencodeList(strings.map(::BencodeByteArray))
        val RAW = strings.joinToBencodeList { "${it.length}:$it" }
    }

    object DictionaryWithIntegers {

        private val dictionary = strings.zip(integers).toMap().toSortedMap()
        val IR = BencodeDictionary(dictionary.mapValues { BencodeInteger(it.value) }.toSortedMap())
        val RAW = dictionary.jointToBencodeDictionary { key, value -> "${key.toBencode()}${value.toBencode()}" }
    }

    object DictionaryWithStrings {

        private val  dictionary = strings.zip(integers.map { it.toString() }).toMap().toSortedMap()
        val IR = BencodeDictionary(dictionary.mapValues { BencodeByteArray(it.value) }.toSortedMap())
        val RAW = dictionary.jointToBencodeDictionary { key, value -> "${key.toBencode()}${value.toBencode()}" }
    }

    private fun String.compress() = filterNot { it.isWhitespace() || it == '\n' }

    private fun String.toBencode(): String = "${length}:$this"

    private fun Long.toBencode(): String = "i${this}e"

    private fun <T> List<T>.joinToBencodeList(transform: (T) -> String): String =
        joinToString(separator = "", prefix = "l", postfix = "e", transform = transform)

    private fun <T> Map<String, T>.jointToBencodeDictionary(transform: (String, T) -> String): String =
        toList().joinToString(separator = "", prefix = "d", postfix = "e") { transform(it.first, it.second) }
}