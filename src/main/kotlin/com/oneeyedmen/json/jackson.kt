package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

operator fun JsonContext.Companion.invoke(objectMapper: ObjectMapper) =
    JsonContext(objectMapper.asNodeFactory())

fun ObjectMapper.asNodeFactory(): NodeFactory = { pairs ->
    this.createObjectNode().apply {
        pairs.forEach { (name, node) ->
            set<JsonNode>(name, node)
        }
    }
}