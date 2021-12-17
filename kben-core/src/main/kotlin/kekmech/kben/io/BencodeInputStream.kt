package kekmech.kben.io

import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.domain.dto.BencodeElement.*
import java.io.*

class BencodeInputStream(
    inputStream: InputStream,
) : FilterInputStream(PushbackInputStream(inputStream)) {

    private fun peek(): Int {
        val byte = read()
        (`in` as PushbackInputStream).unread(byte)
        return byte
    }

    fun readBencodeElement(): BencodeElement {
        val byte = peek()
        return when {
            byte == Constants.START_INTEGER -> readBencodeInteger()
            byte == Constants.START_LIST -> readBencodeList()
            byte == Constants.START_DICTIONARY -> readBencodeDictionary()
            byte.toChar().isDigit() -> readBencodeByteString()
            byte == -1 -> throw EOFException("Unexpected EOF while parsing")
            else -> throw InvalidObjectException("Unexpected token while parsing bencode: " +
                "'${byte.toChar()}'.")
        }
    }

    fun readBencodeInteger(): BencodeInteger {
        var byte = `in`.read()
        checkEOF(byte)
        checkStartByte(expected = Constants.START_INTEGER, actual = byte)

        val stringBuilder = StringBuilder()
        byte = `in`.read()
        while (byte != Constants.END) {
            checkEOF(byte)
            stringBuilder.append(byte.toChar())
            byte = `in`.read()
        }
        return BencodeInteger(stringBuilder.toString().toLong())
    }

    fun readBencodeByteString(): BencodeByteString {
        var byte = `in`.read()
        checkEOF(byte)
        checkBencodeByteStringStartByte(byte)

        val stringBuilder = StringBuilder()
        stringBuilder.append(byte.toChar())
        byte = `in`.read()
        while (byte != Constants.STRING_SEPARATOR) {
            checkEOF(byte)
            checkBencodeByteStringStartByte(byte)
            stringBuilder.append(byte.toChar())
            byte = `in`.read()
        }

        val byteStringLength = stringBuilder.toString().toInt()
        return BencodeByteString(`in`.readNBytes(byteStringLength))
    }

    fun readBencodeList(): BencodeList {
        var byte = `in`.read()
        checkEOF(byte)
        checkStartByte(expected = Constants.START_LIST, actual = byte)

        val elements = mutableListOf<BencodeElement>()
        byte = `in`.read()
        while (byte != Constants.END) {
            checkEOF(byte)
            (`in` as PushbackInputStream).unread(byte)
            elements += readBencodeElement()
            byte = `in`.read()
        }

        return BencodeList(elements)
    }

    fun readBencodeDictionary(): BencodeDictionary {
        var byte = `in`.read()
        checkEOF(byte)
        checkStartByte(expected = Constants.START_DICTIONARY, actual = byte)

        val entries = sortedMapOf<String, BencodeElement>()
        byte = `in`.read()
        while (byte != Constants.END) {
            checkEOF(byte)
            (`in` as PushbackInputStream).unread(byte)
            entries[readBencodeByteString().asString] = readBencodeElement()
            byte = `in`.read()
        }

        return BencodeDictionary(entries)
    }

    private fun checkBencodeByteStringStartByte(byte: Int) {
        if (!byte.toChar().isDigit()) {
            throw InvalidObjectException(
                "Unexpected char at start of bencode byte string: '${byte.toChar()}'. Digit expected."
            )
        }
    }

    private fun checkStartByte(expected: Int, actual: Int) {
        if (actual != expected) {
            throw InvalidObjectException(
                "Unexpected token at start of bencode integer: '${actual.toChar()}'. " +
                    "Expected token: '${expected.toChar()}'"
            )
        }
    }

    private fun checkEOF(token: Int) {
        if (token == -1) {
            throw EOFException("Unexpected EOF while parsing")
        }
    }
}