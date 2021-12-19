package kekmech.kben.annotations

/**
 * Annotation pointing to the default enum value.
 *
 * Add this annotation to one of the enum values so that it is returned in case of an enum deserialization error.
 * Annotation affects only the result of deserialization and does not change the result of enum serialization in any way.
 *
 * Example:
 * ```kotlin
 * // enum with default value
 * enum class SomeOptions {
 *     OPTION_1,
 *     OPTION_2,
 *
 *     @DefaultValue UNKNOWN
 * }
 * // ...
 * kben.fromBencode<SomeOptions>("OPTION_3") // returns SomeOptions.UNKNOWN
 * ```
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class DefaultValue
