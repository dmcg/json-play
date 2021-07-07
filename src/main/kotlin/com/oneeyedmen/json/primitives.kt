package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.TextNode
import kotlin.reflect.KClass

object JsonString : JsonConverter<String> {

    override fun toJson(value: String, factory: NodeFactory): JsonNode =
        TextNode.valueOf(value)

    override fun fromJson(node: JsonNode): String =
        (node as TextNode).textValue()

    override fun schema(factory: NodeFactory): JsonNode =
        factory.objectNode(
            "type" to TextNode.valueOf("string")
        )
}

object JsonInt : JsonConverter<Int> {

    override fun toJson(value: Int, factory: NodeFactory): JsonNode =
        IntNode.valueOf(value)

    override fun fromJson(node: JsonNode): Int =
        (node as IntNode).intValue()

    override fun schema(factory: NodeFactory): JsonNode =
        factory.objectNode(
            "type" to TextNode.valueOf("number")
        )
}

class JsonEnum<E: Enum<E>>(
    klass: KClass<E>
) : JsonConverter<E> {

    private val name = klass.qualifiedName
    private val values = klass.java.enumConstants

    override fun toJson(value: E, factory: NodeFactory): JsonNode =
        TextNode.valueOf(value.toString())

    override fun fromJson(node: JsonNode): E {
        val text = (node as TextNode).textValue()
        return values.find { it.toString() == text }
            ?: error("No enum value found for $text in $name")
    }

    override fun schema(factory: NodeFactory): JsonNode =
        factory.objectNode(
            "type" to TextNode.valueOf("string"),
            "emum" to factory.arrayNode(
                values.map {
                    TextNode.valueOf(it.toString())
                }
            )
        )
}

class JsonCollection<C: Collection<String>>(
    private val collectionClass: KClass<C>
) : JsonConverter<C> {

    override fun toJson(value: C, factory: NodeFactory): JsonNode =
        factory.arrayNode(
            value.map {
                TextNode.valueOf(it.toString())
            }
        )

    override fun fromJson(node: JsonNode): C =
        (node as ArrayNode).mapTo(mutableCollection()) { it.textValue() } as C

    private fun mutableCollection(): MutableCollection<String> = when {
        collectionClass == List::class -> mutableListOf<String>()
        collectionClass == Set::class -> mutableSetOf<String>()
        else -> TODO()
    }

    override fun schema(factory: NodeFactory): JsonNode {
        TODO("Not yet implemented")
    }
}
