package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1


@JvmName("propEnumString")
inline fun <D, reified E: Enum<E>> prop(name: String, noinline extractor: (D) -> E) = enumProp(name, extractor)

@JvmName("propEnum")
inline fun <D, reified E: Enum<E>> prop(property: KProperty1<D, E>) = enumProp(property.name, property)

inline fun <D, reified E: Enum<E>> enumProp(name: String, noinline extractor: (D) -> E) =
    prop(name, extractor, JsonEnum(E::class))


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