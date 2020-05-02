package com.wefox.kanekotic.centralizedPayments.utils

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.wefox.kanekotic.centralizedPayments.Faker
import com.wefox.kanekotic.centralizedPayments.serdes.PaymentSerde
import io.mockk.every
import io.mockk.mockk
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.streams.errors.ProductionExceptionHandler
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class CustomProcessingExceptionHandlerTest {
    private val port = 8997
    private val url  = "localhost"
    private val wiremock = WireMockServer(wireMockConfig().port(port))

    @BeforeEach
    fun beforeEach() {
        wiremock.start()
        wiremock.resetAll()
        configureFor(url, port)
    }
    @AfterEach
    fun afterEach() {
        wiremock.stop()
    }

    @Test
    fun shouldHandleExceptionAndCallLog() {
        val exception = Exception("kaboom")
        val record = mockk<ProducerRecord<ByteArray, ByteArray>>(relaxed = true)
        val payment = Faker.payment()
        every { record.value() } returns PaymentSerde.get().serializer.serialize("", payment)

        wiremock.stubFor(post("/log")
            .withHeader("Content-Type", equalTo("application/json"))
            .withRequestBody(equalTo("""{
                | 'payment_id': '${payment.payment_id}',
                | 'error_type': 'other',
                | 'error_description': '${exception.message}'
                |}""".trimMargin()))
            .inScenario("Scenario")
            .willReturn(ok()))
        val result = CustomProcessingExceptionHandler().handle(record, exception)
        Assertions.assertEquals(ProductionExceptionHandler.ProductionExceptionHandlerResponse.CONTINUE, result)
        wiremock.verify(
            1,
            postRequestedFor(urlEqualTo("/log"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalTo("""{
                | 'payment_id': '${payment.payment_id}',
                | 'error_type': 'other',
                | 'error_description': '${exception.message}'
                |}""".trimMargin())))
    }

}