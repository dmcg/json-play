package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.reflect.KProperty1


fun <D> stringProp(property: KProperty1<D, String>) = stringProp(property.name, property::get)

fun <D> stringProp(name: String, extractor: (D) -> String) =
    prop(name, extractor, JsonString)

fun <D> intProp(property: KProperty1<D, Int>) = intProp(property.name, property::get)

fun <D> intProp(name: String, extractor: (D) -> Int) =
    prop(name, extractor, JsonInt)

fun <P, C> prop(
    property: KProperty1<P, C>,
    converter: JsonConverter<C>
) = prop(property.name, property::get, converter)

fun <P, C> prop(
    name: String,
    extractor: (P) -> C,
    converter: JsonConverter<C>
) = object: JsonProperty<P, C> {
    override val name get() = name
    override fun toJson(value: P, factory: NodeFactory) =
        converter.toJson(extractor(value), factory)
    override fun fromJson(node: JsonNode) = converter.fromJson(node)
    override fun schema(factory: NodeFactory) = converter.schema(factory)
}

@JvmName("propNameInt")
fun <D> prop(name: String, extractor: (D) -> Int) = intProp(name, extractor)

@JvmName("propInt")
fun <D> prop(property: KProperty1<D, Int>) = intProp(property)

@JvmName("propNameString")
fun <D> prop(name: String, extractor: (D) -> String) = stringProp(name, extractor)

@JvmName("propString")
fun <D> prop(property: KProperty1<D, String>) = stringProp(property)

