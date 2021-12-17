package kekmech.kben.mocks

import kekmech.kben.domain.dto.BencodeElement.*

internal object Mocks {

    private val integers = listOf(1L, 42L, -999L, 0L, -2022L)
    private val strings = listOf("lorem", "ipsum", "dolor", "sit", "amet")

    object IntegerPrimitives {

        val LIST = integers
        val IR = integers.map(::BencodeInteger)
        val RAW = integers.map { "i${it}e" }
    }

    object StringPrimitives {

        val LIST = strings
        val IR = strings.map(::BencodeByteString)
        val RAW = strings.map { "${it.length}:$it" }
    }

    object ListOfIntegers {

        val IR = BencodeList(integers.map(::BencodeInteger))
        val RAW = integers.joinToBencodeList { "i${it}e" }
    }

    object ListOfStrings {

        val IR = BencodeList(strings.map(::BencodeByteString))
        val RAW = strings.joinToBencodeList { "${it.length}:$it" }
    }

    object DictionaryWithIntegers {

        val DICTIONARY = strings.zip(integers).toMap().toSortedMap()
        val IR = BencodeDictionary(DICTIONARY.mapValues { BencodeInteger(it.value) }.toSortedMap())
        val RAW = DICTIONARY.jointToBencodeDictionary { key, value -> "${key.toBencode()}${value.toBencode()}" }
    }

    object DictionaryWithStrings {

        val DICTIONARY = strings.zip(integers.map { it.toString() }).toMap().toSortedMap()
        val IR = BencodeDictionary(DICTIONARY.mapValues { BencodeByteString(it.value) }.toSortedMap())
        val RAW = DICTIONARY.jointToBencodeDictionary { key, value -> "${key.toBencode()}${value.toBencode()}" }
    }

    object SetOfIntegers {

        val SET = integers.toSet()
        val IR = ListOfIntegers.IR
        val RAW = ListOfIntegers.RAW
    }

    object SetOfStrings {

        val SET = strings.toSet()
        val IR = ListOfStrings.IR
        val RAW = ListOfStrings.RAW
    }

    private fun String.compress() = filterNot { it.isWhitespace() || it == '\n' }

    private fun String.toBencode(): String = "${length}:$this"

    private fun Long.toBencode(): String = "i${this}e"

    private fun <T> List<T>.joinToBencodeList(transform: (T) -> String): String =
        joinToString(separator = "", prefix = "l", postfix = "e", transform = transform)

    private fun <T> Map<String, T>.jointToBencodeDictionary(transform: (String, T) -> String): String =
        toList().joinToString(separator = "", prefix = "d", postfix = "e") { transform(it.first, it.second) }
}