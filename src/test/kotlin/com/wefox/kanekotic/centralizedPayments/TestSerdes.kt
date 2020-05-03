package com.wefox.kanekotic.centralizedPayments

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.wefox.kanekotic.centralizedPayments.models.Account
import com.wefox.kanekotic.centralizedPayments.models.GenericTypeMessage
import com.wefox.kanekotic.centralizedPayments.models.Payment
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.Serializer
import java.util.*


class testDeserializer : Deserializer<GenericTypeMessage<Payment>> {
    private val objectMapper = jacksonObjectMapper()
    override fun configure(props: Map<String?, *>, isKey: Boolean) {
    }

    override fun deserialize(topic: String, bytes: ByteArray): GenericTypeMessage<Payment> {
        try {
            val intermediate = objectMapper.readValue(bytes, GenericTypeMessage(Faker.payment(), arrayOf(Faker.error()))::class.java)
            val payment = objectMapper.convertValue(
                intermediate.value,
                object : TypeReference<Payment>() {}
            )
            return GenericTypeMessage(payment, intermediate.errors)
        } catch (e: java.lang.Exception) {
            throw SerializationException(e)
        }
    }

    override fun close() {}
}

class testSerializer: Serializer<GenericTypeMessage<Payment>> {
    private val objectMapper = jacksonObjectMapper()

    override fun serialize(topic: String, data: GenericTypeMessage<Payment>?): ByteArray {
        try {
            return objectMapper.writeValueAsBytes(data)
        } catch (e: Exception) {
            throw SerializationException("Error serializing JSON message", e)
        }
    }

    override fun close() {}
}

class TestSerdes private constructor() {
    val serializer: Serializer<GenericTypeMessage<Payment>>
    val deserializer: Deserializer<GenericTypeMessage<Payment>>
    val serde: Serde<GenericTypeMessage<Payment>>

    init {
        val serdeProps: HashMap<String, Any> = HashMap()
        serializer = testSerializer()
        serdeProps.put("JsonClassClass", GenericTypeMessage(Faker.payment(), arrayOf())::class.java)
        serializer.configure(serdeProps, false)
        deserializer = testDeserializer()
        serdeProps.put("JsonClassClass", GenericTypeMessage(Faker.payment(), arrayOf())::class.java)
        deserializer.configure(serdeProps, false)
        serde = Serdes.serdeFrom(serializer, deserializer)
    }

    companion object{
        fun get(): TestSerdes {
            return TestSerdes()
        }
    }
}