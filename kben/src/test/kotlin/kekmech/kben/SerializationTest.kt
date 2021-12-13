package kekmech.kben

import kekmech.kben.mocks.BuyResponseMock
import kekmech.kben.mocks.UserCredentials
import kekmech.kben.mocks.UserCredentialsTypeAdapter
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

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
            "d3:agei24e4:lang6:Kotlin4:name5:Antone".toByteArray(),
            kben.toBencode(mapOf("name" to "Anton", "lang" to "Kotlin", "age" to 24)),
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

        assertEquals(
            "d8:password5:world8:username5:helloe",
            kben.toBencode(credentials).let(::String)
        )
    }

    @Test
    fun `objects to bencode with type adapter`() {
        val kben = Kben(typeAdapters = mapOf(UserCredentials::class to UserCredentialsTypeAdapter()))
        val credentials =
            UserCredentials(
                username = "hello",
                password = "world",
            )

        assertArrayEquals(
            "d8:password5:world8:username5:helloe".toByteArray(),
            kben.toBencode(credentials),
        )
    }

    @Test
    fun `deep data structure to bencode`() {
        val kben = Kben()
        assertArrayEquals(
            """
                d
                    8:password  5:world 
                    7:private   d
                        3:age   i50e
                        4:list  l i1e i2e i3e e
                    e
                    8:username  5:hello
                e
                """.trimIndent()
                .filterNot { it.isWhitespace() || it == '\n' }.toByteArray(),
            kben.toBencode(
                mapOf(
                    "password" to "world",
                    "private" to mapOf(
                        "age" to 50,
                        "list" to listOf(1, 2, 3),
                    ),
                    "username" to "hello",
                )
            )
        )
    }

    @Test
    fun `structures with objects to bencode`() {
        val kben = Kben()
        val buyResponse = BuyResponseMock
        val buySerialized = """
            d
                6:amount    i10e
                4:coin      d
                    7:logoUrl   23:http://1.2.3.4/logo.url
                    4:name      10:EnergoCoin
                    6:ticker    3:MEC
                e
                9:purchases l 
                    d 
                        2:id i1e 
                        3:url 3:abc
                    e
                    d
                        2:id i2e
                        3:url 3:def
                    e
                e
            e
        """.trimIndent().filterNot { it.isWhitespace() || it == '\n' }
        assertEquals(
            buySerialized,
            kben.toBencode(buyResponse).let(::String)
        )
    }
}