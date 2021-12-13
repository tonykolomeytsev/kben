package kekmech.kben

import kekmech.kben.domain.*
import java.io.ByteArrayInputStream
import kotlin.reflect.KClass

class Kben(
    typeAdapters: Map<KClass<out Any>, TypeAdapter<out Any>> = mapOf(),
) {

    private val typeAdapters = StandardTypeAdaptersFactory.createTypeAdapters() + typeAdapters

    fun <T : Any> toBencode(obj: T): ByteArray =
        SerializationContext(typeAdapters).toBencodeByteArray(obj)

    fun <T : Any> fromBencode(inputStream: ByteArrayInputStream, kClass: KClass<T>): T =
        DeserializationContext(typeAdapters).fromBencodeByteArray(inputStream, kClass)

    internal companion object {

        val STRING_SEPARATOR = ":".toByteArray()
        val START_INTEGER = "i".toByteArray()
        val START_LIST = "l".toByteArray()
        val START_DICTIONARY = "d".toByteArray()
        val END = "e".toByteArray()
    }
}

inline fun <reified T : Any> Kben.fromBencode(inputStream: ByteArrayInputStream): T =
    fromBencode(inputStream, T::class)

inline fun <reified T : Any> Kben.fromBencode(bencodeByteArray: ByteArray): T =
    fromBencode(bencodeByteArray.inputStream(), T::class)

inline fun <reified T : Any> Kben.fromBencode(bencodeString: String): T =
    fromBencode(bencodeString.byteInputStream(), T::class)
