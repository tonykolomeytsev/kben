package kekmech.kben.domain.reflect

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class Bencode(val name: String)