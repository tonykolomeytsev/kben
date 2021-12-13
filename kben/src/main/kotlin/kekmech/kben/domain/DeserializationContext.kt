package kekmech.kben.domain

import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.domain.dto.BencodeElement.*
import kekmech.kben.io.BencodeReader
import java.io.ByteArrayInputStream
import kotlin.reflect.KClass

class DeserializationContext(
    private val typeAdapters: Map<KClass<out Any>, TypeAdapter<out Any>>,
) {

    fun <T : Any> fromBencodeByteArray(byteArrayInputStream: ByteArrayInputStream, kClass: KClass<T>): T =
        byteArrayInputStream
            .use { decodeElement(it) ?: error("Broken Bencode schema") }
            .let { fromBencode(it, kClass) }

    internal fun <T : Any> fromBencode(bencodeElement: BencodeElement, kClass: KClass<T>? = null): T =
        typeAdapters[kClass ?: resolveType(bencodeElement)]?.fromBencode(bencodeElement, this) as T

    private fun resolveType(value: BencodeElement): KClass<*> =
        when (value) {
            is BencodeInteger -> Long::class
            is BencodeByteArray ->
                if (value.isValidUTF8String) String::class else ByteArray::class
            is BencodeList -> List::class
            is BencodeDictionary -> Map::class
        }

    private fun decodeElement(stream: ByteArrayInputStream): BencodeElement? = BencodeReader(stream).read()
}