package kekmech.kben.domain.dto

import kekmech.kben.domain.dto.BencodeElement.*
import java.math.BigInteger
import java.util.*

/**
 * Intermediate bencode representation.
 *
 * This is sealed class for [BencodeByteString], [BencodeInteger],
 * [BencodeList], [BencodeDictionary]
 */
sealed class BencodeElement {

    /**
     * Intermediate representation for bencode **byte string** type.
     *
     * Not all **byte strings** can be represented as valid UTF-8 string.
     * Some can only be stored as a byte array.
     */
    data class BencodeByteString(val bytes: ByteArray) : BencodeElement() {

        constructor(string: String) : this(string.toByteArray())

        /**
         * Returns string representation of **byte string** content.
         */
        val asString: String get() = String(bytes)

        /**
         * Returns some string representation of **byte string** content.
         * Can be used to display the result of a hash function.
         */
        val asHexString: String get() = String.format("%064x", BigInteger(1, bytes))

        /**
         * True if **byte string** content is a valid UTF-8 string.
         */
        val isValidUTF8String: Boolean get() =
            bytes.all { it >= 0 && Char(it.toInt()).isDefined() }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as BencodeByteString
            return bytes.contentEquals(other.bytes)
        }

        override fun hashCode(): Int {
            return bytes.contentHashCode()
        }
    }

    /**
     * Intermediate representation for bencode **integer** type.
     */
    data class BencodeInteger(val integer: Long) : BencodeElement()

    /**
     * Intermediate representation for bencode **list** type.
     */
    data class BencodeList(val elements: Iterable<BencodeElement>) : BencodeElement()

    /**
     * Intermediate representation for bencode **dictionary** type.
     */
    data class BencodeDictionary(val entries: SortedMap<String, BencodeElement>) : BencodeElement()
}