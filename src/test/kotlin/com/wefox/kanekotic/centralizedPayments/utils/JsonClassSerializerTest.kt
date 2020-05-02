package com.wefox.kanekotic.centralizedPayments.utils

import com.wefox.kanekotic.centralizedPayments.models.GenericTypeMessage
import org.apache.kafka.common.serialization.Serializer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JsonClassSerializerTest {

    data class Example(val text: String)

    private lateinit var subject: Serializer<GenericTypeMessage<Example>>


    @BeforeEach
    fun setup() {
        subject = JsonClassSerializer()
    }

    @Test
    fun shouldBeAbleToConvertObject() {
        val result = subject.serialize("", GenericTypeMessage(Example("pepe"), emptyArray()))
        Assertions.assertNotNull(result)
    }

}