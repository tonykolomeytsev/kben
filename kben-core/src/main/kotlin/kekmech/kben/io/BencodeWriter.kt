package kekmech.kben.io

import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.io.Constants.END
import kekmech.kben.io.Constants.START_DICTIONARY
import kekmech.kben.io.Constants.START_INTEGER
import kekmech.kben.io.Constants.START_LIST
import kekmech.kben.io.Constants.STRING_SEPARATOR
import java.io.ByteArrayOutputStream

class BencodeWriter {

    private val buffer = ByteArrayOutputStream()

    fun write(element: BencodeElement) {
        when (element) {
            is BencodeElement.BencodeByteArray -> buffer.apply {
                write(element.content.size.toString().toByteArray())
                write(STRING_SEPARATOR)
                write(element.content)
            }
            is BencodeElement.BencodeInteger -> buffer.apply {
                write(START_INTEGER)
                write(element.integer.toString().toByteArray())
                write(END)
            }
            is BencodeElement.BencodeList -> buffer.apply {
                write(START_LIST)
                element.elements.forEach { element ->
                    write(element)
                }
                write(END)
            }
            is BencodeElement.BencodeDictionary -> buffer.apply {
                write(START_DICTIONARY)
                element.entries.forEach { (key, value) ->
                    write(BencodeElement.BencodeByteArray(key.toByteArray()))
                    write(value)
                }
                write(END)
            }
        }
    }

    fun toByteArray(): ByteArray = buffer.toByteArray()
}