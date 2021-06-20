package com.oneeyedmen.json

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Tests {

    @Test
    fun test() {
        val domain = Domain("fred", 42)
        assertEquals(JDomain("fred", 42), convert(domain))
    }

    private fun convert(domain: Domain): JDomain {
        val converter: (Domain) -> JDomain = createConverter(::JDomain, Domain::name, Domain::count)
        return converter(domain)
    }

    private fun <I, O, P1, P2> createConverter(
        constructor: (P1, P2) -> O,
        p1: (I) -> P1,
        p2: (I) -> P2
    ): (I) -> O = {
        constructor(p1(it), p2(it))
    }
}

data class Domain(
    val name: String,
    val count: Int
)

data class JDomain(
    val name: String,
    val count: Int
)