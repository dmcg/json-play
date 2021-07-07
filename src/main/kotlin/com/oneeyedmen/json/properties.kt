package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import kotlin.reflect.KProperty1


fun <P, C> prop(
    property: KProperty1<P, C>,
    converter: JsonConverter<C>,
    nullable: Boolean = false,
) = prop(property.name, property::get, converter, nullable)

fun <P, C> prop(
    name: String,
    extractor: (P) -> C,
    converter: JsonConverter<C>,
    nullable: Boolean = false,
) = object: JsonProperty<P, C> {

    override fun toJson(value: P, factory: NodeFactory) =
        name to converter.toJson(extractor(value), factory)

    override fun fromJson(node: JsonNode): C {
        val value: C? = node[name]?.let {
            converter.fromJson(it)
        }
        if (value == null && !nullable)
            error("Missing required property $name")
        return value as C // TODO obviously bogus but works
    }

    override fun schema(factory: NodeFactory) =
        name to converter.schema(factory)
}