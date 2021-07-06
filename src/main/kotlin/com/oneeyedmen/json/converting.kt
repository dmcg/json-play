package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.reflect.KProperty1

interface JsonConverter<D> {
    fun toJson(value: D, factory: ObjectMapper): JsonNode
    fun fromJson(node: JsonNode): D
}

interface JsonProperty<P, C> {
    val name: String
    fun toJson(value: P, factory: ObjectMapper): JsonNode
    fun fromJson(node: JsonNode): C
}

fun <D, P1, P2> converter(
    ctor: (P1, P2) -> D,
    p1: JsonProperty<D, P1>,
    p2: JsonProperty<D, P2>
) = object: JsonConverter<D> {
    override fun toJson(value: D, factory: ObjectMapper): JsonNode =
        factory.createObjectNode().apply {
            set<JsonNode>(p1.name, p1.toJson(value, factory))
            set<JsonNode>(p2.name, p2.toJson(value, factory))
        }

    override fun fromJson(node: JsonNode): D =
        ctor(
            p1.fromJson(node.get(p1.name)),
            p2.fromJson(node.get(p2.name)),
        )
}

