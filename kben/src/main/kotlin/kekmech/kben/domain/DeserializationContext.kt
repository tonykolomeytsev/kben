package kekmech.kben.domain

import kekmech.kben.domain.adapters.AnyTypeAdapter
import kekmech.kben.domain.adapters.IterableTypeAdapter
import kekmech.kben.domain.adapters.MapTypeAdapter
import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.domain.dto.BencodeElement.*
import kekmech.kben.io.BencodeReader
import java.io.ByteArrayInputStream
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class DeserializationContext(
    private val typeAdapters: Map<KClass<out Any>, TypeAdapter<out Any>>,
) {

    fun <T : Any> fromBencodeByteArray(byteArrayInputStream: ByteArrayInputStream, kClass: KClass<T>): T =
        byteArrayInputStream
            .use { decodeElement(it) ?: error("Broken Bencode schema") }
            .let { fromBencode(it, kClass) }

    @Suppress("UNCHECKED_CAST")
    internal fun <T : Any> fromBencode(bencodeElement: BencodeElement, kClass: KClass<T>): T {
        val typeAdapter = typeAdapters[kClass] as? TypeAdapter<T>
        if (typeAdapter != null) {
            return typeAdapter.fromBencode(bencodeElement, this)
        }
        if (kClass.isSubclassOf(Iterable::class)) {
            return IterableTypeAdapter(resolveType(bencodeElement)).fromBencode(bencodeElement, this) as T
        }
        if (kClass.isSubclassOf(Map::class)) {
            return MapTypeAdapter(resolveType(bencodeElement)).fromBencode(bencodeElement, this) as T
        }
        return AnyTypeAdapter(kClass).fromBencode(bencodeElement, this)
    }
        //typeAdapters[kClass ?: resolveType(bencodeElement)]?.fromBencode(bencodeElement, this) as T

    private fun resolveType(value: BencodeElement): KClass<*> =
        when (value) {
            is BencodeInteger -> Long::class
            is BencodeByteArray ->
                if (value.isValidUTF8String) String::class else ByteArray::class
            is BencodeList -> Iterable::class
            is BencodeDictionary -> Map::class
        }

    private fun decodeElement(stream: ByteArrayInputStream): BencodeElement? = BencodeReader(stream).read()
}