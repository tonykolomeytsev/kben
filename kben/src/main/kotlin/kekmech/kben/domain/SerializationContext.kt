package kekmech.kben.domain

import kekmech.kben.Kben.Companion.END
import kekmech.kben.Kben.Companion.START_DICTIONARY
import kekmech.kben.Kben.Companion.START_INTEGER
import kekmech.kben.Kben.Companion.START_LIST
import kekmech.kben.Kben.Companion.STRING_SEPARATOR
import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.domain.dto.BencodeElement.*
import java.io.ByteArrayOutputStream

class SerializationContext(
    private val typeAdapters: Map<Class<*>, TypeAdapter<*>>,
) {

    fun <T : Any> toBencode(obj: T?): BencodeElement {
        return when (obj) {
            null -> BencodeString("4:null".toByteArray())
            is String -> BencodeString(obj)
            is Int -> BencodeInteger(obj.toLong())
            is Long -> BencodeInteger(obj)
            is ByteArray -> BencodeString(obj)
            is Iterable<*> -> BencodeList(obj.map(::toBencode))
            is Map<*, *> -> BencodeDictionary(
                obj
                    .mapKeys { (key, _) ->
                        key as? String ?: error("All keys in bencode dictionary must be a valid UTF-8 string: \"${obj}\"")
                    }
                    .mapValues { (_, value) -> toBencode(value) }
                    .toSortedMap()
            )
            else -> {
                @Suppress("UNCHECKED_CAST")
                (typeAdapters[obj::class.java] as? TypeAdapter<T>)
                    ?.toBencode(obj, obj::class.java.componentType, this)
                    ?: throw NotImplementedError("Serialization for class ${obj::class.java} not implemented")
            }
        }
    }

    internal fun <T : Any> toBencodeByteArray(obj: T?): ByteArray =
        ByteArrayOutputStream()
            .apply { toBencode(obj).writeTo(this) }
            .toByteArray()

    private fun BencodeElement.writeTo(buffer: ByteArrayOutputStream) {
        when (this) {
            is BencodeString -> buffer.apply {
                write(content.size.toString().toByteArray())
                write(STRING_SEPARATOR)
                write(content)
            }
            is BencodeInteger -> buffer.apply {
                write(START_INTEGER)
                write(integer.toString().toByteArray())
                write(END)
            }
            is BencodeList -> buffer.apply {
                write(START_LIST)
                elements.forEach { element ->
                    element.writeTo(buffer)
                }
                write(END)
            }
            is BencodeDictionary -> buffer.apply {
                write(START_DICTIONARY)
                entries.forEach { (key, value) ->
                    BencodeString(key.toByteArray()).writeTo(buffer)
                    value.writeTo(buffer)
                }
                write(END)
            }
        }
    }
}