package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode

class JsonConverterBase<D>(
    private val ctor: (JsonNode) -> D,
    private val properties: List<JsonProperty<D, *>>,
) : JsonConverter<D> {

    constructor(
        ctor: (JsonNode) -> D,
        vararg properties: JsonProperty<D, *>
    ) : this(ctor, properties.asList())

    override fun toJson(value: D, factory: NodeFactory): JsonNode =
        factory(
            properties.map { it.toJson(value, factory) }
        )

    override fun fromJson(node: JsonNode): D = ctor(node)

    override fun schema(factory: NodeFactory) =
        factory(
            "type" to TextNode.valueOf("object"),
            "properties" to factory(
                properties.map { it.schema(factory) }
            )
        )
}