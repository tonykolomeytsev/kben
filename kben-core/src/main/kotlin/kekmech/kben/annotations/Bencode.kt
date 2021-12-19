package kekmech.kben.annotations

/**
 * An annotation that indicates this member should be serialized to Bencode with
 * the provided name value as its field name.
 *
 * **Example with properties**:
 * ```kotlin
 * data class User(
 *     @Bencode("full name")
 *     val fullName: String,
 *     val tariff: String,
 * )
 *
 * // object to bencode
 * // output is "d9:full name5:Anton6:tariff5:Primee"
 * val bencode =
 *     kben.toBencode(User("Anton", "Prime"))
 *
 * // bencode to object
 * // output is User("Anton", "Prime")
 * val objectCopy =
 *     kben.fromBencode<User>(bencode)
 * ```
 *
 * **Example with enums**:
 * ```kotlin
 * enum class UserStatus {
 *     @Bencode("online") ONLINE,
 *     @Bencode("offline") OFFLINE
 * }
 * // ...
 * kben.toBencode(UserStatus.ONLINE) // '6:online'
 * ```
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class Bencode(val name: String)