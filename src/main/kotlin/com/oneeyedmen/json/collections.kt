package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.TextNode
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1


@JvmName("propCollectionString")
inline fun <D, E, reified C: Collection<E>> prop(
    name: String,
    noinline extractor: (D) -> C,
    elementConverter: JsonConverter<E>,
) = collectionProp(name, extractor, elementConverter)

@JvmName("propCollection")
inline fun <D, E, reified C: Collection<E>> prop(
    property: KProperty1<D, C>,
    elementConverter: JsonConverter<E>,
) = collectionProp(property.name, property, elementConverter)

inline fun <D, E, reified C: Collection<E>> collectionProp(
    name: String,
    noinline extractor: (D) -> C,
    elementConverter: JsonConverter<E>
) = prop(name, extractor, JsonCollection(C::class, elementConverter))


class JsonCollection<E, C: Collection<E>>(
    private val collectionClass: KClass<C>,
    private val elementConverter: JsonConverter<E>
) : JsonConverter<C> {

    override fun toJson(value: C, factory: NodeFactory): JsonNode =
        factory.arrayNode(
            value.map {
                elementConverter.toJson(it, factory)
            }
        )

    override fun fromJson(node: JsonNode): C =
        (node as ArrayNode).mapTo(mutableCollection()) {
            elementConverter.fromJson(it)
        } as C

    private fun mutableCollection(): MutableCollection<E> = when {
        collectionClass == List::class -> mutableListOf<E>()
        collectionClass == Set::class -> mutableSetOf<E>()
        else -> TODO()
    }

    override fun schema(factory: NodeFactory): JsonNode {
        TODO("Not yet implemented")
    }
}
