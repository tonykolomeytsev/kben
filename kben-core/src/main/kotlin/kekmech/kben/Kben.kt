package kekmech.kben

import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.StandardTypeAdaptersFactory
import kekmech.kben.domain.TypeAdapter
import java.io.InputStream
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

/**
 * This is the main class for using Kben.
 *
 * Example of how Kben is used for a simple KClass:
 * ```kotlin
 * val kben = Kben()
 * val target = MyTarget(1, 2, 3)
 * val bencode = kben.toBencode(target) // serializes target to Bencode
 * val target2 = kben.fromBencode<MyTarget>(bencode) // deserializes Bencode into target2
 * ```
 *
 * If the object that you are deserializing is a ParameterizedType
 * (i.e. contains at least one type parameter and may be an array) then you must use the
 * [Kben.fromBencode(Any, TypeHolder)] method.
 *
 * Example for serializing and deserializing a ParameterizedType:
 * ```kotlin
 * val kben = Kben()
 * val target = MyTarget<String>("hello")
 * val bencode = kben.toBencode(target) // serializes target to Bencode
 * val target2 = kben.fromBencode<MyTarget<String>>(
 *     bencode,
 *     Parameterized(MyTarget::class, listOf(Simple(String::class))),
 * ) // deserializes Bencode into target2
 * ```
 */
class Kben(
    typeAdapters: Map<KClass<out Any>, TypeAdapter<out Any>> = mapOf(),
) {

    private val standardTypeAdapters = StandardTypeAdaptersFactory.createTypeAdapters()
    private val customTypeAdapters = typeAdapters

    /**
     * This method serializes the specified object into its equivalent Bencode representation.
     */
    fun <T : Any> toBencode(obj: T): ByteArray =
        SerializationContext(standardTypeAdapters, customTypeAdapters).toBencodeByteArray(obj)

    /**
     * This method deserializes the specified Bencode into an object of the specified class. It is not
     * suitable to use if the specified class is a generic type since it will not have the generic
     * type information because of the Type Erasure feature of Java. Therefore, this method should not
     * be used if the desired type is a generic type. Note that this method works fine if the any of
     * the fields of the specified object are generics, just the object itself should not be a
     * generic type. For the cases when the object is of generic type, invoke
     * [Kben.fromBencode] with TypeHolder argument.
     */
    fun <T : Any> fromBencode(inputStream: InputStream, typeHolder: TypeHolder): T =
        DeserializationContext(standardTypeAdapters, customTypeAdapters).fromBencodeByteArray(inputStream, typeHolder)
}

/**
 * This method deserializes the specified Bencode into an object of the specified class.
 */
inline fun <reified T : Any> Kben.fromBencode(inputStream: InputStream): T =
    fromBencode(
        inputStream = inputStream,
        typeHolder = when {
            T::class.isSubclassOf(Iterable::class) ||
                T::class.isSubclassOf(Map::class) -> error("Unresolved Iterable generic type, use fromBencode with TypeHolder")
            else -> TypeHolder.Simple(T::class)
        }
    )

/**
 * This method deserializes the specified Bencode into an object of the specified class. It is not
 * suitable to use if the specified class is a generic type since it will not have the generic
 * type information because of the Type Erasure feature of Java. Therefore, this method should not
 * be used if the desired type is a generic type. Note that this method works fine if the any of
 * the fields of the specified object are generics, just the object itself should not be a
 * generic type. For the cases when the object is of generic type, invoke
 * [Kben.fromBencode] with TypeHolder argument.
 */
inline fun <reified T : Any> Kben.fromBencode(bencodeByteArray: ByteArray): T =
    fromBencode(bencodeByteArray.inputStream())

/**
 * This method deserializes the specified Bencode into an object of the specified class. It is not
 * suitable to use if the specified class is a generic type since it will not have the generic
 * type information because of the Type Erasure feature of Java. Therefore, this method should not
 * be used if the desired type is a generic type. Note that this method works fine if the any of
 * the fields of the specified object are generics, just the object itself should not be a
 * generic type. For the cases when the object is of generic type, invoke
 * [Kben.fromBencode] with TypeHolder argument.
 */
inline fun <reified T : Any> Kben.fromBencode(bencodeString: String): T =
    fromBencode(bencodeString.byteInputStream())

/**
 * This method deserializes the specified Bencode into an object of the specified class.
 */
fun <T : Any> Kben.fromBencode(bencodeByteArray: ByteArray, typeHolder: TypeHolder): T =
    fromBencode(bencodeByteArray.inputStream(), typeHolder)

/**
 * This method deserializes the specified Bencode into an object of the specified class.
 */
fun <T : Any> Kben.fromBencode(bencodeString: String, typeHolder: TypeHolder): T =
    fromBencode(bencodeString.byteInputStream(), typeHolder)