package kekmech.kben.domain

import kekmech.kben.TypeHolder
import kekmech.kben.domain.adapters.AnyTypeAdapter
import kekmech.kben.domain.adapters.EnumTypeAdapter
import kekmech.kben.domain.adapters.IterableTypeAdapter
import kekmech.kben.domain.adapters.MapTypeAdapter
import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.io.BencodeInputStream
import java.io.ByteArrayInputStream
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class DeserializationContext(
    standardTypeAdapters: Map<KClass<out Any>, TypeAdapter<out Any>>,
    customTypeAdapters: Map<KClass<out Any>, TypeAdapter<out Any>>,
) : AbstractContext(standardTypeAdapters, customTypeAdapters) {

    fun <T : Any> fromBencodeByteArray(byteArrayInputStream: ByteArrayInputStream, typeHolder: TypeHolder): T =
        byteArrayInputStream
            .use { decodeElement(it) }
            .let { fromBencode(it, typeHolder) }

    @Suppress("UNCHECKED_CAST")
    internal fun <T : Any> fromBencode(bencodeElement: BencodeElement, typeHolder: TypeHolder): T {
        val typeAdapter = findTypeAdapterFor<T>(typeHolder)
        val ret: Any = when {
            typeAdapter != null ->
                typeAdapter.fromBencode(bencodeElement, this, typeHolder)
            typeHolder.type.isSubclassOf(Iterable::class) ->
                IterableTypeAdapter<T>().fromBencode(bencodeElement, this, typeHolder)
            typeHolder.type.isSubclassOf(Map::class) ->
                MapTypeAdapter<T>().fromBencode(bencodeElement, this, typeHolder)
            typeHolder.type.isSubclassOf(Enum::class) ->
                EnumTypeAdapter().fromBencode(bencodeElement, this, typeHolder)
            else ->
                AnyTypeAdapter<T>().fromBencode(bencodeElement, this, typeHolder)
        }
        return ret as T
    }

    private fun decodeElement(stream: ByteArrayInputStream): BencodeElement =
        BencodeInputStream(stream).readBencodeElement()
}