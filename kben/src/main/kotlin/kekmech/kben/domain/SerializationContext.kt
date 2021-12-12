package kekmech.kben.domain

import kekmech.kben.Kben.Companion.END
import kekmech.kben.Kben.Companion.START_DICTIONARY
import kekmech.kben.Kben.Companion.START_INTEGER
import kekmech.kben.Kben.Companion.START_LIST
import kekmech.kben.Kben.Companion.STRING_SEPARATOR
import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.domain.dto.BencodeElement.*
import java.io.ByteArrayOutputStream
import kotlin.reflect.KClass

class SerializationContext(
    private val typeAdapters: Map<KClass<out Any>, TypeAdapter<out Any>>,
) {

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> toBencode(obj: T): BencodeElement {
        val typeAdapter = (typeAdapters[obj::class] as? TypeAdapter<T>)
        if (typeAdapter != null) {
            return typeAdapter.toBencode(obj, this)
        }
        if (obj is Map<*, *>) {
            return MapTypeAdapter().toBencode(obj, this)
        }
        if (obj is Iterable<*>) {
            return IterableTypeAdapter().toBencode(obj, this)
        }
        throw IllegalStateException("TypeAdapter for type ${obj::class} not registered")
    }

    internal fun <T : Any> toBencodeByteArray(obj: T): ByteArray =
        ByteArrayOutputStream()
            .apply { toBencode(obj).writeTo(this) }
            .toByteArray()

    private fun BencodeElement.writeTo(buffer: ByteArrayOutputStream) {
        when (this) {
            is BencodeByteArray -> buffer.apply {
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
                    BencodeByteArray(key.toByteArray()).writeTo(buffer)
                    value.writeTo(buffer)
                }
                write(END)
            }
        }
    }
}