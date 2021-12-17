package kekmech.kben.io

import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.domain.dto.BencodeElement.*
import java.io.FilterOutputStream
import java.io.OutputStream

/**
 * OutputStream for writing bencoded data
 */
class BencodeOutputStream(
    outputStream: OutputStream,
) : FilterOutputStream(outputStream) {

    /**
     * Writes the passed [element] to the stream.
     */
    fun write(element: BencodeElement) {
        when (element) {
            is BencodeByteString -> {
                write(element.bytes.size.toString().toByteArray())
                write(Constants.STRING_SEPARATOR)
                write(element.bytes)
            }
            is BencodeInteger -> {
                write(Constants.START_INTEGER)
                write(element.integer.toString().toByteArray())
                write(Constants.END)
            }
            is BencodeList -> {
                write(Constants.START_LIST)
                element.elements.forEach(::write)
                write(Constants.END)
            }
            is BencodeDictionary -> {
                write(Constants.START_DICTIONARY)
                element.entries.forEach { (key, value) ->
                    write(BencodeByteString(key.toByteArray(Charsets.UTF_8)))
                    write(value)
                }
                write(Constants.END)
            }
        }
    }
}