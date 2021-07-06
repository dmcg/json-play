package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode

fun <D, P1> jsonMapping(
    ctor: (P1) -> D,
    p1: JsonProperty<D, P1>,
): JsonConverter<D> = object: JsonConverterBase<D>(listOf(
    p1,
)) {
    override fun fromJson(node: JsonNode): D =
        ctor(
            p1.fromJson(node),
        )
}

fun <D, P1, P2> jsonMapping(
    ctor: (P1, P2) -> D,
    p1: JsonProperty<D, P1>,
    p2: JsonProperty<D, P2>
): JsonConverter<D> = object: JsonConverterBase<D>(listOf(
    p1,
    p2,
)) {
    override fun fromJson(node: JsonNode): D =
        ctor(
            p1.fromJson(node),
            p2.fromJson(node),
        )
}

fun <D, P1, P2, P3> jsonMapping(
    ctor: (P1, P2, P3) -> D,
    p1: JsonProperty<D, P1>,
    p2: JsonProperty<D, P2>,
    p3: JsonProperty<D, P3>
): JsonConverter<D> = object: JsonConverterBase<D>(listOf(
    p1,
    p2,
    p3,
)) {
    override fun fromJson(node: JsonNode): D =
        ctor(
            p1.fromJson(node),
            p2.fromJson(node),
            p3.fromJson(node),
        )
}