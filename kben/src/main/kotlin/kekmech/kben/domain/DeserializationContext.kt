package kekmech.kben.domain

import kekmech.kben.Kben.Companion.END
import kekmech.kben.Kben.Companion.START_DICTIONARY
import kekmech.kben.Kben.Companion.START_INTEGER
import kekmech.kben.Kben.Companion.START_LIST
import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.domain.dto.BencodeElement.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class DeserializationContext(
    private val typeAdapters: Map<Class<*>, TypeAdapter<*>>,
) {

    fun fromBencode(byteArrayInputStream: ByteArrayInputStream): BencodeElement =
        byteArrayInputStream.use { decodeElement(it) ?: error("Broken Bencode schema") }

    private fun decodeElement(stream: ByteArrayInputStream): BencodeElement? =
        when (stream.read()) {
            START_INTEGER[0].toInt() -> decodeInteger(stream)
            START_LIST[0].toInt() -> decodeList(stream)
            START_DICTIONARY[0].toInt() -> decodeDictionary(stream)
            END[0].toInt(), -1 -> null
            else -> decodeString(stream)
        }

    private fun decodeInteger(stream: ByteArrayInputStream): BencodeInteger {
        val byteBuffer = ByteArrayOutputStream()
        var byte = stream.read()
        while (byte != END[0].toInt()) {
            byteBuffer.write(byte)
            byte = stream.read()
        }
        val integer = String(byteBuffer.toByteArray()).toLong()
        return BencodeInteger(integer)
    }

    private fun decodeList(stream: ByteArrayInputStream): BencodeList {
        val elements = mutableListOf<BencodeElement>()
        var element = decodeElement(stream)
        while (element != null) {
            elements += element
            element = decodeElement(stream)
        }
        return  BencodeList(elements)
    }

    private fun decodeDictionary(stream: ByteArrayInputStream): BencodeDictionary {
        val sortedMap = sortedMapOf<String, BencodeElement>()
        var key = (decodeElement(stream) as? BencodeString)
        while (key != null) {
            val value = decodeElement(stream)!!
            sortedMap += key.string to value
            key = (decodeElement(stream) as? BencodeString)
        }
        return BencodeDictionary(sortedMap)
    }

    private fun decodeString(stream: ByteArrayInputStream): BencodeString {
        val lengthByteBuffer = ByteArrayOutputStream()
        var byte = stream.read()
        while (byte != END[0].toInt()) {
            lengthByteBuffer.write(byte)
            byte = stream.read()
        }
        val stringLength = String(lengthByteBuffer.toByteArray()).toInt()
        return BencodeString(stream.readNBytes(stringLength))
    }

    internal fun fromBencodeByteArray(byteArrayInputStream: ByteArrayInputStream): BencodeElement =
        fromBencode(byteArrayInputStream)
}