package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode

abstract class JsonConverterBase<D>(
    private val properties: List<JsonProperty<D, *>>
) : JsonConverter<D> {

    override fun toJson(value: D, factory: NodeFactory): JsonNode =
        factory(
            properties.map { it.toJson(value, factory) }
        )

    override fun schema(factory: NodeFactory) =
        factory(
            "type" to TextNode.valueOf("object"),
            "properties" to factory(
                properties.map { it.schema(factory) }
            )
        )
}