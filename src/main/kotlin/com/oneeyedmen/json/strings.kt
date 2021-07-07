package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.TextNode
import kotlin.reflect.KProperty1


@JvmName("propNameString")
inline fun <D, reified S: String?> prop(
    name: String,
    noinline extractor: (D) -> S
): JsonProperty<D, S> = stringProp(name, extractor)

@JvmName("propString")
inline fun <D, reified S: String?> prop(
    property: KProperty1<D, S>
): JsonProperty<D, S> = stringProp(property.name, property)

@JvmName("propStringCollectionString")
inline fun <D, reified C: Collection<String>> prop(
    name: String,
    noinline extractor: (D) -> C,
): JsonProperty<D, C> = prop(name, extractor, JsonString)

@JvmName("propStringCollection")
inline fun <D, reified C: Collection<String>> prop(
    property: KProperty1<D, C>,
): JsonProperty<D, C> = prop(property.name, property)

inline fun <D, reified S: String?> stringProp(
    name: String,
    noinline extractor: (D) -> S
): JsonProperty<D, S> =
    prop(
        name,
        extractor,
        JsonString,
        null is S
    ) as JsonProperty<D, S>


object JsonString : JsonConverter<String?> {

    override fun toJson(value: String?, factory: NodeFactory): JsonNode =
        if (value == null) NullNode.instance else
        TextNode.valueOf(value)

    override fun fromJson(node: JsonNode): String? = when (node) {
        is TextNode -> node.asText()
        is NullNode -> null
        else -> error("$node is not text")
    }

    override fun schema(factory: NodeFactory): JsonNode =
        factory.objectNode(
            "type" to TextNode.valueOf("string")
        )
}