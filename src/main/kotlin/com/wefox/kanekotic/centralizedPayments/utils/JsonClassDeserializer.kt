package com.wefox.kanekotic.centralizedPayments.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.wefox.kanekotic.centralizedPayments.models.GenericTypeMessage
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer

class JsonClassDeserializer<T> : Deserializer<GenericTypeMessage<T>> {
    private val objectMapper = jacksonObjectMapper()
    private var tClass: Class<T>? = null
    override fun configure(props: Map<String?, *>, isKey: Boolean) {
        tClass = props["JsonClassClass"] as Class<T>?
    }

    override fun deserialize(topic: String, bytes: ByteArray): GenericTypeMessage<T> {
        try {
            return GenericTypeMessage(objectMapper.readValue(bytes, tClass), emptyArray())
        } catch (e: java.lang.Exception) {
            throw SerializationException(e)
        }
    }

    override fun close() {}
}
