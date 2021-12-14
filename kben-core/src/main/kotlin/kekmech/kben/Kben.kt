package kekmech.kben

import kekmech.kben.domain.*
import java.io.ByteArrayInputStream
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class Kben(
    typeAdapters: Map<KClass<out Any>, TypeAdapter<out Any>> = mapOf(),
) {

    private val typeAdapters = StandardTypeAdaptersFactory.createTypeAdapters() + typeAdapters

    fun <T : Any> toBencode(obj: T): ByteArray =
        SerializationContext(typeAdapters).toBencodeByteArray(obj)

    fun <T : Any> fromBencode(inputStream: ByteArrayInputStream, typeHolder: TypeHolder): T =
        DeserializationContext(typeAdapters).fromBencodeByteArray(inputStream, typeHolder)
}

inline fun <reified T : Any> Kben.fromBencode(inputStream: ByteArrayInputStream): T =
    fromBencode(
        inputStream = inputStream,
        typeHolder = when {
            T::class.isSubclassOf(Iterable::class) ||
                T::class.isSubclassOf(Map::class) -> error("Unresolved Iterable generic type, use fromBencode with TypeHolder")
            else -> TypeHolder.Simple(T::class)
        }
    )

inline fun <reified T : Any> Kben.fromBencode(bencodeByteArray: ByteArray): T =
    fromBencode(bencodeByteArray.inputStream())

inline fun <reified T : Any> Kben.fromBencode(bencodeString: String): T =
    fromBencode(bencodeString.byteInputStream())

fun <T : Any> Kben.fromBencode(bencodeByteArray: ByteArray, typeHolder: TypeHolder): T =
    fromBencode(bencodeByteArray.inputStream(), typeHolder)

fun <T : Any> Kben.fromBencode(bencodeString: String, typeHolder: TypeHolder): T =
    fromBencode(bencodeString.byteInputStream(), typeHolder)