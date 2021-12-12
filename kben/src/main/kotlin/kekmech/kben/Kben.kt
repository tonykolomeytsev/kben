package kekmech.kben

import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement
import java.io.ByteArrayInputStream

class Kben(
    private val typeAdapters: Map<Class<*>, TypeAdapter<*>> = mapOf(),
) {

    fun <T : Any> toBencode(obj: T): ByteArray =
        SerializationContext(typeAdapters).toBencodeByteArray(obj)

    fun fromBencode(byteArrayInputStream: ByteArrayInputStream): BencodeElement =
        DeserializationContext(typeAdapters).fromBencodeByteArray(byteArrayInputStream)

    fun fromBencode(byteArray: ByteArray): BencodeElement = fromBencode(byteArray.inputStream())

    internal companion object {

        val STRING_SEPARATOR = ":".toByteArray()
        val START_INTEGER = "i".toByteArray()
        val START_LIST = "l".toByteArray()
        val START_DICTIONARY = "d".toByteArray()
        val END = "e".toByteArray()
    }
}