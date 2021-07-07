package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

operator fun JsonContext.Companion.invoke(objectMapper: ObjectMapper) =
    JsonContext(objectMapper.asNodeFactory())

fun ObjectMapper.asNodeFactory(): NodeFactory = object: NodeFactory {

    override fun objectNode(children: Iterable<Pair<String, JsonNode>>) =
        createObjectNode().apply {
            children.forEach { (name, node) ->
                set<JsonNode>(name, node)
            }
        }

    override fun arrayNode(children: Iterable<JsonNode>) =
        createArrayNode().apply {
            children.forEach {
                this.add(it)
            }
        }
}

