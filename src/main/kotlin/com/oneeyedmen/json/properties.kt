package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import kotlin.reflect.KProperty1


fun <P, C> prop(
    property: KProperty1<P, C>,
    converter: JsonConverter<C>
) = prop(property.name, property::get, converter)

fun <P, C> prop(
    name: String,
    extractor: (P) -> C,
    converter: JsonConverter<C>
) = object: JsonProperty<P, C> {

    override fun toJson(value: P, factory: NodeFactory) =
        name to converter.toJson(extractor(value), factory)

    override fun fromJson(node: JsonNode) =
        converter.fromJson(node[name])

    override fun schema(factory: NodeFactory) =
        name to converter.schema(factory)
}

