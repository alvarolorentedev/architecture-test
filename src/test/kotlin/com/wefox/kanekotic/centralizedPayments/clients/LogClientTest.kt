package com.wefox.kanekotic.centralizedPayments.clients

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.wefox.kanekotic.centralizedPayments.Faker
import com.wefox.kanekotic.centralizedPayments.configurations.LogConfiguration
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.sql.SQLException
import com.wefox.kanekotic.centralizedPayments.models.Error
import io.mockk.every
import io.mockk.mockk

internal class LogClientTest {
    private val port = 8997
    private val url  = "localhost"
    private val wiremock = WireMockServer(WireMockConfiguration.wireMockConfig().port(port))
    private lateinit var LogClient: LogClient

    @BeforeEach
    fun beforeEach() {
        wiremock.start()
        wiremock.resetAll()
        WireMock.configureFor(url, port)
        val logConfiguration: LogConfiguration = mockk(relaxed = true)
        every { logConfiguration.url } returns "http://localhost:8997"
        LogClient = LogClient(logConfiguration)
    }
    @AfterEach
    fun afterEach() {
        wiremock.stop()
    }

    @Test
    fun shouldSendUnkownTypeOfExceptionAsOther() {
        val message = "kaboom"
        val payment = Faker.payment()

        wiremock.stubFor(
            WireMock.post("/log")
            .withHeader("Content-Type", WireMock.equalTo("application/json"))
            .withRequestBody(
                WireMock.equalTo(
                    """{
                | 'payment_id': '${payment.payment_id}',
                | 'error_type': 'database',
                | 'error_description': '${message}'
                |}""".trimMargin()
                )
            )
            .inScenario("Scenario")
            .willReturn(WireMock.ok()))

        LogClient.logError(payment, Error("database",message))

        wiremock.verify(
            1,
            WireMock.postRequestedFor(WireMock.urlEqualTo("/log"))
                .withHeader("Content-Type", WireMock.equalTo("application/json"))
                .withRequestBody(
                    WireMock.equalTo(
                        """{
                | 'payment_id': '${payment.payment_id}',
                | 'error_type': 'database',
                | 'error_description': '${message}'
                |}""".trimMargin()
                    )
                ))
    }
}