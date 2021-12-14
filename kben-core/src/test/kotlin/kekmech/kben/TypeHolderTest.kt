package kekmech.kben

import org.junit.jupiter.api.Test
import kotlin.reflect.full.primaryConstructor

class TypeHolderTest {

    data class TestClass<T>(
        val id: T,
        val a: List<List<String>>,
    )

    @Test
    fun test1() {
        TestClass::class.primaryConstructor!!.parameters.forEach {
            println(TypeHolder.from(it))
        }
    }
}