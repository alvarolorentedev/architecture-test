package com.wefox.kanekotic.centralizedPayments.utils

import com.wefox.kanekotic.centralizedPayments.models.GenericTypeMessage
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class JsonClassDeserializerTest {

    data class Example(val text: String)

    private lateinit var subject: Deserializer<GenericTypeMessage<Example>>

    @BeforeEach
    fun setup() {
        subject = JsonClassDeserializer()
        val serdeProps: HashMap<String, Any> = HashMap()
        serdeProps.put("JsonClassClass", Example::class.java)
        subject.configure(serdeProps, false)
    }

    @Test
    fun shouldFailIfEmpty() {
        try {
            subject.deserialize("", "".toByteArray())
            fail("Should not be here")
        } catch (e: SerializationException) {
        }
    }

    @Test
    fun shouldFailIfOtherJson() {
        try {
            subject.deserialize("", "{ \"text2\": \"pepe\" }".toByteArray())
            fail("Should not be here")
        } catch (e: SerializationException) {
        }
    }

    @Test
    fun shouldReturnIfClassJson() {
        val result = subject.deserialize("", "{ \"text\": \"pepe\" }".toByteArray())
        Assertions.assertEquals(result.value, Example("pepe"))
        Assertions.assertArrayEquals(result.errors, emptyArray())
    }
}
