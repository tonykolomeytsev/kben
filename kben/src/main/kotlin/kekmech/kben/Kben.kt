package kekmech.kben

import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.StandardTypeAdapters
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement
import java.io.ByteArrayInputStream
import kotlin.reflect.KClass

class Kben(
    genericTypeAdapters: Map<KClass<out Any>, TypeAdapter<out Any>> = mapOf(),
) {

    private val typeAdapters = genericTypeAdapters + StandardTypeAdapters

    fun <T : Any> toBencode(obj: T): ByteArray =
        SerializationContext(typeAdapters).toBencodeByteArray(obj)

    fun <T : Any> fromBencode(byteArrayInputStream: ByteArrayInputStream, kClass: KClass<T>): T =
        DeserializationContext(typeAdapters).fromBencodeByteArray(byteArrayInputStream, kClass)

    internal companion object {

        val STRING_SEPARATOR = ":".toByteArray()
        val START_INTEGER = "i".toByteArray()
        val START_LIST = "l".toByteArray()
        val START_DICTIONARY = "d".toByteArray()
        val END = "e".toByteArray()
    }
}