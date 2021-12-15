package kekmech.kben.domain

import kekmech.kben.TypeHolder
import kekmech.kben.domain.adapters.AnyTypeAdapter
import kekmech.kben.domain.adapters.IterableTypeAdapter
import kekmech.kben.domain.adapters.MapTypeAdapter
import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.io.BencodeReader
import java.io.ByteArrayInputStream
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class DeserializationContext(
    standardTypeAdapters: Map<KClass<out Any>, TypeAdapter<out Any>>,
    customTypeAdapters: Map<KClass<out Any>, TypeAdapter<out Any>>,
) : AbstractContext(standardTypeAdapters, customTypeAdapters) {

    fun <T : Any> fromBencodeByteArray(byteArrayInputStream: ByteArrayInputStream, typeHolder: TypeHolder): T =
        byteArrayInputStream
            .use { decodeElement(it) ?: error("Broken bencode") }
            .let { fromBencode(it, typeHolder) }

    @Suppress("UNCHECKED_CAST")
    internal fun <T : Any> fromBencode(bencodeElement: BencodeElement, typeHolder: TypeHolder): T {
        val ret: Any = when (typeHolder) {
            is TypeHolder.Simple -> {
                findTypeAdapterFor<T>(typeHolder)
                    ?.fromBencode(bencodeElement, this, typeHolder)
                    ?: AnyTypeAdapter<T>().fromBencode(bencodeElement, this, typeHolder)
            }
            is TypeHolder.Parameterized -> {
                val typeAdapter = findTypeAdapterFor<T>(typeHolder)
                when {
                    typeAdapter != null ->
                        typeAdapter.fromBencode(bencodeElement, this, typeHolder)
                    typeHolder.type.isSubclassOf(Iterable::class) ->
                        IterableTypeAdapter<T>().fromBencode(bencodeElement, this, typeHolder)
                    typeHolder.type.isSubclassOf(Map::class) ->
                        MapTypeAdapter<T>().fromBencode(bencodeElement, this, typeHolder)
                    else ->
                        AnyTypeAdapter<T>().fromBencode(bencodeElement, this, typeHolder)
                }
            }
        }
        return ret as T
    }

    private fun decodeElement(stream: ByteArrayInputStream): BencodeElement? = BencodeReader(stream).read()
}