package kekmech.kben.domain.dto

import java.math.BigInteger
import java.util.*

sealed class BencodeElement {

    data class BencodeByteArray(val content: ByteArray) : BencodeElement() {

        constructor(string: String) : this(string.toByteArray())

        val asString: String get() = String(content)

        val asHexString: String get() = String.format("%064x", BigInteger(1, content))

        val isValidUTF8String: Boolean get() =
            content.all { Char(it.toInt()).isDefined() }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as BencodeByteArray
            return content.contentEquals(other.content)
        }

        override fun hashCode(): Int {
            return content.contentHashCode()
        }
    }

    data class BencodeInteger(val integer: Long) : BencodeElement()

    data class BencodeList(val elements: Iterable<BencodeElement>) : BencodeElement()

    data class BencodeDictionary(val entries: SortedMap<String, BencodeElement>) : BencodeElement()
}