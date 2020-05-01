package com.wefox.kanekotic.centralizedPayments.serdes.utils

import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class JsonClassDeserializer<T> : Deserializer<T> {
    private val objectMapper = jacksonObjectMapper()
    private var tClass: Class<T>? = null
    override fun configure(props: Map<String?, *>, isKey: Boolean) {
        tClass = props["JsonClassClass"] as Class<T>?
    }

    override fun deserialize(topic: String, bytes: ByteArray): T {
        try {
            return objectMapper.readValue(bytes, tClass)
        } catch (e: java.lang.Exception) {
            throw SerializationException(e)
        }
    }

    override fun close() {}
}