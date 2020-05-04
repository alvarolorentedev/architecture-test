package com.wefox.kanekotic.centralizedPayments.clients

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.wefox.kanekotic.centralizedPayments.Faker
import com.wefox.kanekotic.centralizedPayments.configurations.LogConfiguration
import com.wefox.kanekotic.centralizedPayments.configurations.paymentServiceConfiguration
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.sql.SQLException
import com.wefox.kanekotic.centralizedPayments.models.Error
import io.mockk.every
import io.mockk.mockk

internal class PaymentsClientTest {
    private val port = 8997
    private val url  = "localhost"
    private val wiremock = WireMockServer(WireMockConfiguration.wireMockConfig().port(port))
    private lateinit var paymentsClient: PaymentsClient

    @BeforeEach
    fun beforeEach() {
        wiremock.start()
        wiremock.resetAll()
        WireMock.configureFor(url, port)
        val config: paymentServiceConfiguration = mockk(relaxed = true)
        every { config.url } returns "http://localhost:8997"
        paymentsClient = PaymentsClient(config)
    }
    @AfterEach
    fun afterEach() {
        wiremock.stop()
    }

    @Test
    fun shouldSendRequestToValidatePayment() {
        val payment = Faker.payment()

        wiremock.stubFor(
            WireMock.post("/log")
            .withHeader("Content-Type", WireMock.equalTo("application/json"))
            .withRequestBody(
                WireMock.equalTo(
                    """"{
                        |'payment_id': ${payment.payment_id}, 
                        |'account_id': ${payment.account_id}, 
                        |'payment_type': 'online',
                        |'credit_card': ${payment.credit_card}',
                        |'amount':${payment.amount}
                        |}"""".trimMargin()
                )
            )
            .inScenario("Scenario")
            .willReturn(WireMock.ok()))

        paymentsClient.validatePayment(payment)

        wiremock.verify(
            1,
            WireMock.postRequestedFor(WireMock.urlEqualTo("/log"))
                .withHeader("Content-Type", WireMock.equalTo("application/json"))
                .withRequestBody(
                    WireMock.equalTo(
                        """"{
                        |'payment_id': ${payment.payment_id}, 
                        |'account_id': ${payment.account_id}, 
                        |'payment_type': 'online',
                        |'credit_card': ${payment.credit_card}',
                        |'amount':${payment.amount}
                        |}"""".trimMargin()
                    )
                ))
    }
}