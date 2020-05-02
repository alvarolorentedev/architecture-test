package com.wefox.kanekotic.centralizedPayments.utils

import io.mockk.mockk
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.streams.errors.ProductionExceptionHandler
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class CustomProcessingExceptionHandlerTest {
    @Test
    fun shouldFailIfEmpty() {
        val exception = mockk<Exception>(relaxed = true)
        val record = mockk<ProducerRecord<ByteArray, ByteArray>>(relaxed = true)
        val result = CustomProcessingExceptionHandler().handle(record, exception)
        Assertions.assertEquals(ProductionExceptionHandler.ProductionExceptionHandlerResponse.CONTINUE, result)
    }
}