package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import kotlin.reflect.KProperty1

fun <D> intProp(name: String, extractor: (D) -> Int) =
    prop(name, extractor, JsonInt)

fun <D> stringProp(name: String, extractor: (D) -> String) =
    prop(name, extractor, JsonString)

inline fun <D, reified E: Enum<E>> enumProp(name: String, noinline extractor: (D) -> E) =
    prop(name, extractor, JsonEnum(E::class))

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

@JvmName("propNameInt")
fun <D> prop(name: String, extractor: (D) -> Int) = intProp(name, extractor)

@JvmName("propInt")
fun <D> prop(property: KProperty1<D, Int>) = intProp(property.name, property)

@JvmName("propNameString")
fun <D> prop(name: String, extractor: (D) -> String) = stringProp(name, extractor)

@JvmName("propString")
fun <D> prop(property: KProperty1<D, String>) = stringProp(property.name, property)

@JvmName("propEnumString")
inline fun <D, reified E: Enum<E>> prop(name: String, noinline extractor: (D) -> E) = enumProp(name, extractor)

@JvmName("propEnum")
inline fun <D, reified E: Enum<E>> prop(property: KProperty1<D, E>) = enumProp(property.name, property)

