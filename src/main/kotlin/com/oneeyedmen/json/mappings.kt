@file:Suppress("unused")
package com.oneeyedmen.json


fun <D, P1> jsonMapping(
    ctor: (P1) -> D,
    p1: JsonProperty<D, P1>,
): JsonConverter<D> =
    JsonConverterBase(
        {
            ctor(
                p1.fromJson(it),
            )
        },
        p1,
    )

fun <D, P1, P2> jsonMapping(
    ctor: (P1, P2) -> D,
    p1: JsonProperty<D, P1>,
    p2: JsonProperty<D, P2>,
): JsonConverter<D> =
    JsonConverterBase(
        {
            ctor(
                p1.fromJson(it),
                p2.fromJson(it),
            )
        },
        p1, p2,
    )

fun <D, P1, P2, P3> jsonMapping(
    ctor: (P1, P2, P3) -> D,
    p1: JsonProperty<D, P1>,
    p2: JsonProperty<D, P2>,
    p3: JsonProperty<D, P3>,
): JsonConverter<D> =
    JsonConverterBase(
        {
            ctor(
                p1.fromJson(it),
                p2.fromJson(it),
                p3.fromJson(it),
            )
        },
        p1, p2, p3,
    )
