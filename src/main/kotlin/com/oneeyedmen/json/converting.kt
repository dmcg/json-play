package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

class JsonContext(
    val factory: NodeFactory
) {
    fun <D> JsonConverter<D>.toJson(value: D) = toJson(value, factory)

    companion object // to hang static extensions off of
}

interface JsonConverter<D> {
    fun toJson(value: D, factory: NodeFactory): JsonNode
    fun fromJson(node: JsonNode): D
}

interface JsonProperty<P, C> {
    val name: String
    fun toJson(value: P, factory: NodeFactory): JsonNode
    fun fromJson(node: JsonNode): C
}

fun <D, P1, P2> jsonMapping(
    ctor: (P1, P2) -> D,
    p1: JsonProperty<D, P1>,
    p2: JsonProperty<D, P2>
) = object: JsonConverter<D> {
    override fun toJson(value: D, factory: NodeFactory): JsonNode =
        factory(listOf(
            p1.name to p1.toJson(value, factory),
            p2.name to p2.toJson(value, factory)
        ))

    override fun fromJson(node: JsonNode): D =
        ctor(
            p1.fromJson(node.get(p1.name)),
            p2.fromJson(node.get(p2.name)),
        )
}

typealias NodeFactory = (List<Pair<String, JsonNode>>) -> JsonNode
