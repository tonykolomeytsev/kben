package kekmech.kben.domain

import kekmech.kben.domain.adapters.AnyTypeAdapter
import kekmech.kben.domain.adapters.IterableTypeAdapter
import kekmech.kben.domain.adapters.MapTypeAdapter
import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.io.BencodeWriter
import kotlin.reflect.KClass

class SerializationContext(
    private val typeAdapters: Map<KClass<out Any>, TypeAdapter<out Any>>,
) {

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> toBencode(obj: T): BencodeElement {
        val typeAdapter = typeAdapters[obj::class] as? TypeAdapter<T>
        if (typeAdapter != null) {
            return typeAdapter.toBencode(obj, this)
        }
        if (obj is Iterable<*>) {
            return IterableTypeAdapter<T>().toBencode(obj as Iterable<T>, this)
        }
        if (obj is Map<*, *>) {
            return MapTypeAdapter<T>().toBencode(obj as Map<String, T>, this)
        }
        return AnyTypeAdapter<T>().toBencode(obj, this)
    }

    internal fun <T : Any> toBencodeByteArray(obj: T): ByteArray =
        BencodeWriter().write(toBencode(obj))
}