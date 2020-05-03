package com.wefox.kanekotic.centralizedPayments.clients

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.wefox.kanekotic.centralizedPayments.Faker
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.sql.SQLException
import com.wefox.kanekotic.centralizedPayments.models.Error

internal class PaymentsClientTest {
    private val port = 8997
    private val url  = "localhost"
    private val wiremock = WireMockServer(WireMockConfiguration.wireMockConfig().port(port))

    @BeforeEach
    fun beforeEach() {
        wiremock.start()
        wiremock.resetAll()
        WireMock.configureFor(url, port)
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

        PaymentsClient.validatePayment(payment)

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