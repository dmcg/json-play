package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode

class JsonContext(
    val factory: NodeFactory
) {
    fun <D> JsonConverter<D>.toJson(value: D) = toJson(value, factory)

    companion object // to hang static extensions off of
}

interface JsonConverter<D> {
    fun toJson(value: D, factory: NodeFactory): JsonNode
    fun fromJson(node: JsonNode): D
    fun schema(factory: NodeFactory): JsonNode
}

interface JsonProperty<P, C> {
    fun toJson(value: P, factory: NodeFactory): Pair<String, JsonNode>
    fun fromJson(node: JsonNode): C
    fun schema(factory: NodeFactory): Pair<String, JsonNode>
}

interface NodeFactory {
    fun objectNode(children: Iterable<Pair<String, JsonNode>>): JsonNode
    fun objectNode(vararg children: Pair<String, JsonNode>) = objectNode(children.asList())
    fun arrayNode(children: Iterable<JsonNode>): JsonNode
    fun arrayNode(vararg children: JsonNode) = arrayNode(children.asList())
}