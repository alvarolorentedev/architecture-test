package com.wefox.kanekotic.centralizedPayments.utils

import com.wefox.kanekotic.centralizedPayments.Faker
import com.wefox.kanekotic.centralizedPayments.clients.LogClient
import com.wefox.kanekotic.centralizedPayments.serdes.PaymentSerde
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.streams.errors.ProductionExceptionHandler
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class CustomProcessingExceptionHandlerTest {


    @Test
    fun shouldHandleExceptionAndLogError() {

        val exception = Exception("kaboom")
        val logClient = mockk<LogClient>(relaxed = true)
        val record = mockk<ProducerRecord<ByteArray, ByteArray>>(relaxed = true)
        val payment = Faker.payment()
        every { record.value() } returns PaymentSerde.get().serializer.serialize("", payment)
        val subject = CustomProcessingExceptionHandler()
        subject.configure(mutableMapOf("logHandler" to logClient))
        val result = subject.handle(record, exception)
        Assertions.assertEquals(ProductionExceptionHandler.ProductionExceptionHandlerResponse.CONTINUE, result)
        verify { logClient.logError(payment, exception) }

    }

}