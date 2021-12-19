package kekmech.kben

import com.dampcake.bencode.Bencode
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class BenchmarkTest {

    private val bencode = Bencode()
    private val kben = Kben()
    private val data = mapOf(
        "string" to "value",
        "number" to 123456L,
        "list" to listOf(
            "list-item-1",
            "list-item-2",
        ),
        "dict" to sortedMapOf(
            "123" to "test",
            "456" to "thing",
        )
    )

    @Test
    fun performanceTest() {
        assertArrayEquals(bencode.encode(data), kben.toBencode(data))
        val dampcakeTime = ArrayList<Long>(20)
        val kbenTime = ArrayList<Long>(20)
        repeat(10) {
            repeat(3) {
                kbenTime += measureTimeMillis {
                    repeat(100_000) {
                        kben.toBencode(data)
                    }
                }
            }
            repeat(3) {
                dampcakeTime += measureTimeMillis {
                    repeat(100_000) {
                        bencode.encode(data)
                    }
                }
            }
        }
        println("dampcakeTime = $dampcakeTime")
        println("kbenTime = $kbenTime")

        val medianDc = dampcakeTime.sorted()[dampcakeTime.lastIndex / 2]
        val medianKb = kbenTime.sorted()[kbenTime.lastIndex / 2]

        println("Median dampcake: $medianDc")
        println("Median kben: $medianKb")
        println("Average dampcake: ${dampcakeTime.average()}")
        println("Average kben: ${kbenTime.average()}")
    }
}