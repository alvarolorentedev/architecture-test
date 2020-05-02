package com.wefox.kanekotic.centralizedPayments.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.wefox.kanekotic.centralizedPayments.models.GenericTypeMessage
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer

class JsonClassSerializer<T>: Serializer<GenericTypeMessage<T>> {
    private val objectMapper = jacksonObjectMapper()

    override fun serialize(topic: String, data: GenericTypeMessage<T>?): ByteArray {
        try {
            return objectMapper.writeValueAsBytes(data!!.value)
        } catch (e: Exception) {
            throw SerializationException("Error serializing JSON message", e)
        }
    }

    override fun close() {}
}


