package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.reflect.KProperty1


fun <D> jsonString(property: KProperty1<D, String>) = jsonString(property.name, property::get)

fun <D> jsonString(name: String, extractor: (D) -> String) =
    jsonObject(name, extractor, JsonString)

fun <D> jsonInt(property: KProperty1<D, Int>) = jsonInt(property.name, property::get)

fun <D> jsonInt(name: String, extractor: (D) -> Int) =
    jsonObject(name, extractor, JsonInt)

fun <P, C> jsonObject(
    property: KProperty1<P, C>,
    converter: JsonConverter<C>
) = jsonObject(property.name, property::get, converter)

fun <P, C> jsonObject(
    name: String,
    extractor: (P) -> C,
    converter: JsonConverter<C>
) = object: JsonProperty<P, C> {
    override val name get() = name
    override fun toJson(value: P, factory: ObjectMapper): JsonNode =
        converter.toJson(extractor(value), factory)
    override fun fromJson(node: JsonNode): C = converter.fromJson(node)
}
