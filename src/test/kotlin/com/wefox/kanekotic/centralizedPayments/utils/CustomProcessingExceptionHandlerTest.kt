package com.wefox.kanekotic.centralizedPayments.utils

import io.mockk.mockk
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.streams.errors.ProductionExceptionHandler
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class CustomProcessingExceptionHandlerTest {


    @Test
    fun shouldHandleExceptionAndLogError() {
        val exception = Exception("kaboom")
        val record = mockk<ProducerRecord<ByteArray, ByteArray>>(relaxed = true)
        val result = CustomProcessingExceptionHandler().handle(record, exception)
        Assertions.assertEquals(ProductionExceptionHandler.ProductionExceptionHandlerResponse.CONTINUE, result)

    }

}