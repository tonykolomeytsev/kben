package kekmech.kben

import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.io.BencodeReader
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BencodeReaderTest {

    @Test
    fun `test 1`() {
        assertEquals(
            BencodeElement.BencodeList(listOf(1, 2, 3).map { BencodeElement.BencodeInteger(it.toLong()) }),
            BencodeReader("li1ei2ei3ee".toByteArray().inputStream()).read(),
        )
    }

    @Test
    fun `test 2`() {
        assertEquals(
            BencodeElement.BencodeList(listOf("a", "ab", "abc").map { BencodeElement.BencodeByteArray(it.toByteArray()) }),
            BencodeReader("l1:a2:ab3:abce".toByteArray().inputStream()).read(),
        )
    }
}