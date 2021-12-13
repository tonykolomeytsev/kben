package kekmech.kben.io

import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.io.Constants.END
import kekmech.kben.io.Constants.START_DICTIONARY
import kekmech.kben.io.Constants.START_INTEGER
import kekmech.kben.io.Constants.START_LIST
import kekmech.kben.io.Constants.STRING_SEPARATOR
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class BencodeReader(
    private val stream: ByteArrayInputStream,
) {

    private var last: Int = -1

    fun read(): BencodeElement? {
        last = stream.read()
        return decodeElement(stream)
    }

    private fun decodeElement(stream: ByteArrayInputStream) =
        when (last) {
            START_INTEGER[0].toInt() -> decodeInteger(stream)
            START_LIST[0].toInt() -> decodeList(stream)
            START_DICTIONARY[0].toInt() -> decodeDictionary(stream)
            END[0].toInt(), -1 -> null
            else -> decodeString(stream)
        }

    private fun decodeInteger(stream: ByteArrayInputStream): BencodeElement.BencodeInteger {
        val byteBuffer = ByteArrayOutputStream()
        last = stream.read()
        while (last != END[0].toInt()) {
            byteBuffer.write(last)
            last = stream.read()
        }
        val integer = String(byteBuffer.toByteArray()).toLong()
        return BencodeElement.BencodeInteger(integer)
    }

    private fun decodeList(stream: ByteArrayInputStream): BencodeElement.BencodeList {
        val elements = mutableListOf<BencodeElement>()
        last = stream.read()
        var element = decodeElement(stream)
        while (element != null) {
            elements += element
            last = stream.read()
            element = decodeElement(stream)
        }
        return BencodeElement.BencodeList(elements)
    }

    private fun decodeDictionary(stream: ByteArrayInputStream): BencodeElement.BencodeDictionary {
        val sortedMap = sortedMapOf<String, BencodeElement>()
        last = stream.read()
        var key = (decodeElement(stream) as? BencodeElement.BencodeByteArray)
        while (key != null) {
            val value = decodeElement(stream)!!
            sortedMap += key.asString to value
            key = (decodeElement(stream) as? BencodeElement.BencodeByteArray)
        }
        return BencodeElement.BencodeDictionary(sortedMap)
    }

    private fun decodeString(stream: ByteArrayInputStream): BencodeElement.BencodeByteArray {
        val lengthByteBuffer = ByteArrayOutputStream(3)
        while (last != STRING_SEPARATOR[0].toInt()) {
            lengthByteBuffer.write(last)
            last = stream.read()
        }
        val stringLength = String(lengthByteBuffer.toByteArray()).toInt()
        return BencodeElement.BencodeByteArray(stream.readNBytes(stringLength))
    }
}