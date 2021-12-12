package kekmech.kben

import kekmech.kben.mocks.UserCredentials
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SerializationTest {

    @Test
    fun `strings to bencode`() {
        val kben = Kben()
        println("8:announce".toByteArray().map { it.toChar() })
        println(kben.toBencode("announce").map { it.toChar() })
        assertArrayEquals(
            "8:announce".toByteArray(),
            kben.toBencode("announce"),
        )
    }

    @Test
    fun `integers to bencode`() {
        val kben = Kben()
        assertArrayEquals(
            "i42e".toByteArray(),
            kben.toBencode(42),
        )
        assertArrayEquals(
            "i${Long.MAX_VALUE / 2L}e".toByteArray(),
            kben.toBencode(Long.MAX_VALUE / 2L),
        )
    }

    @Test
    fun `byte arrays to bencode`() {
        val kben = Kben()
        assertArrayEquals(
            byteArrayOf(1, 2, 3, 4, 5),
            kben.toBencode(byteArrayOf(1, 2, 3, 4, 5)),
        )
    }

    @Test
    fun `lists to bencode`() {
        val kben = Kben()
        assertArrayEquals(
            "l8:announcei42ee".toByteArray(),
            kben.toBencode(listOf("announce", 42))
        )
    }

    @Test
    fun `dictionaries to bencode`() {
        val kben = Kben()
        assertArrayEquals(
            "d4:name5:Anton4:lang6:Kotlin3:agei24ee".toByteArray(),
            kben.toBencode(mapOf("name" to "Anton", "lang" to "Kotlin", "age" to 24))
        )
    }

    @Test
    fun `objects to bencode without type adapter`() {
        val kben = Kben()
        val credentials =
            UserCredentials(
                username = "hello",
                password = "world",
            )

        println(listOf("hello world")::class.java)
        assertThrows<NotImplementedError> {
            kben.toBencode(credentials)
        }
    }
}