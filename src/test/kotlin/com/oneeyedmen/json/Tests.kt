package com.oneeyedmen.json

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Tests {

    @Test
    fun `converter can convert`() {
        val domain = Domain("fred", 42)
        val converter: (Domain) -> JDomain = createConverter(::JDomain,
            Domain::name,
            Domain::count
        )
        assertEquals(JDomain("fred", 42), converter(domain))
    }

    data class Domain(
        val name: String,
        val count: Int
    )

    data class JDomain(
        val name: String,
        val count: Int
    )
}

fun <I, O, P1, P2> createConverter(
    constructor: (P1, P2) -> O,
    p1: (I) -> P1,
    p2: (I) -> P2
): (I) -> O = {
    constructor(p1(it), p2(it))
}


