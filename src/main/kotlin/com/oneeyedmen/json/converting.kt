package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.TextNode

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
    val name: String
    fun toJson(value: P, factory: NodeFactory): JsonNode
    fun fromJson(node: JsonNode): C
    fun schema(factory: NodeFactory): JsonNode
}

fun <D, P1, P2> jsonMapping(
    ctor: (P1, P2) -> D,
    p1: JsonProperty<D, P1>,
    p2: JsonProperty<D, P2>
) = object: JsonConverter<D> {

    private val properties = listOf(p1, p2)

    override fun toJson(value: D, factory: NodeFactory): JsonNode =
        factory(
            properties.map { it.name to it.toJson(value, factory) }
        )

    override fun fromJson(node: JsonNode): D =
        ctor(
            p1.fromJson(node.get(p1.name)),
            p2.fromJson(node.get(p2.name)),
        )

    override fun schema(factory: NodeFactory) =
        factory(
            "type" to TextNode.valueOf("object"),
            "properties" to factory(
                properties.map { it.name to it.schema(factory) }
            )
        )
}

typealias NodeFactory = (List<Pair<String, JsonNode>>) -> JsonNode

operator fun NodeFactory.invoke(vararg nodes: Pair<String, JsonNode>) =
    this(nodes.asList())