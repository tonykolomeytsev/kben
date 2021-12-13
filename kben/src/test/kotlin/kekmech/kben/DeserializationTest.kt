package kekmech.kben

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DeserializationTest {

    @Test
    fun `bencode string to string`() {
        val kben = Kben()
        val bencodeString = "6:Kotlin"
        assertEquals(
            kben.fromBencode(bencodeString),
            "Kotlin"
        )
    }
}