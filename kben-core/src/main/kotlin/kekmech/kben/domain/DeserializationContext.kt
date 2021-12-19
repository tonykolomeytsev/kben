package kekmech.kben.domain

import kekmech.kben.Kben
import kekmech.kben.TypeHolder
import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.io.BencodeInputStream
import java.io.ByteArrayInputStream
import kotlin.reflect.full.isSubclassOf

class DeserializationContext(kben: Kben) : AbstractContext(kben.standardTypeAdapters, kben.customTypeAdapters) {

    private val iterableTypeAdapter = kben.iterableTypeAdapter
    private val mapTypeAdapter = kben.mapTypeAdapter
    private val enumTypeAdapter = kben.enumTypeAdapter
    private val anyTypeAdapter = kben.anyTypeAdapter

    @Suppress("UNCHECKED_CAST")
    internal fun <T : Any> fromBencode(bencodeElement: BencodeElement, typeHolder: TypeHolder): T {
        val typeAdapter = findTypeAdapterFor<T>(typeHolder)
        val ret: Any = when {
            typeAdapter != null ->
                typeAdapter.fromBencode(bencodeElement, this, typeHolder)
            typeHolder.type.isSubclassOf(Iterable::class) ->
                iterableTypeAdapter.fromBencode<T>(bencodeElement, this, typeHolder)
            typeHolder.type.isSubclassOf(Map::class) ->
                mapTypeAdapter.fromBencode<T>(bencodeElement, this, typeHolder)
            typeHolder.type.isSubclassOf(Enum::class) ->
                enumTypeAdapter.fromBencode(bencodeElement, this, typeHolder)
            else ->
                anyTypeAdapter.fromBencode<T>(bencodeElement, this, typeHolder)
        }
        return ret as T
    }

    private fun decodeElement(stream: ByteArrayInputStream): BencodeElement =
        BencodeInputStream(stream).readBencodeElement()

    fun <T : Any> fromBencodeByteArray(byteArrayInputStream: ByteArrayInputStream, typeHolder: TypeHolder): T =
        byteArrayInputStream
            .use { decodeElement(it) }
            .let { fromBencode(it, typeHolder) }
}