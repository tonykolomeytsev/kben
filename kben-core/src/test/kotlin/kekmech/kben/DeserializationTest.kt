package kekmech.kben

import kekmech.kben.mocks.Purchase
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DeserializationTest {

    @Test
    fun `bencode string to string`() {
        val kben = Kben()
        val bencodeString = "6:Kotlin"
        assertEquals(
            "Kotlin",
            kben.fromBencode(bencodeString)
        )
    }

    @Test
    fun `bencode integer to long`() {
        val kben = Kben()
        val bencodeInteger = "i42e"
        assertEquals(
            42L,
            kben.fromBencode(bencodeInteger)
        )
    }

    @Test
    fun `bencode integer to int`() {
        val kben = Kben()
        val bencodeInteger = "i42e"
        assertEquals(
            42,
            kben.fromBencode(bencodeInteger)
        )
    }

    @Test
    fun `bencode integer to bytearray`() {
        val kben = Kben()
        val bencodeInteger = "11:hello world"
        assertArrayEquals(
            "hello world".toByteArray(),
            kben.fromBencode(bencodeInteger),
        )
    }

    @Test
    fun `bencode list to typed list`() {
        val kben = Kben()
        val testDataClass = TestClass(listOf(Purchase(id = 0, url = "")))
        val bencodeList = "li1ei2ei3ee"
        assertEquals(
            listOf(1L, 2L, 3L),
            kben.fromBencode(bencodeList)
        )
    }
}

private data class TestClass(val purchases: List<Purchase>)