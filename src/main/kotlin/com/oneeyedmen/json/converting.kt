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

typealias NodeFactory = (List<Pair<String, JsonNode>>) -> JsonNode

operator fun NodeFactory.invoke(vararg nodes: Pair<String, JsonNode>) =
    this(nodes.asList())