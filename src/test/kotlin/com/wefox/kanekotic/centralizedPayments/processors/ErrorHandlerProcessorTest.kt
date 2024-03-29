package com.wefox.kanekotic.centralizedPayments.processors

import com.wefox.kanekotic.centralizedPayments.Faker
import com.wefox.kanekotic.centralizedPayments.TestSerdes
import com.wefox.kanekotic.centralizedPayments.clients.LogClient
import com.wefox.kanekotic.centralizedPayments.clients.LogResponseException
import com.wefox.kanekotic.centralizedPayments.configurations.KafkaConfiguration
import com.wefox.kanekotic.centralizedPayments.models.GenericTypeMessage
import com.wefox.kanekotic.centralizedPayments.models.Payment
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.TestInputTopic
import org.apache.kafka.streams.TestOutputTopic
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.kstream.Consumed
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ErrorHandlerProcessorTest {
    private var testDriver: TopologyTestDriver? = null
    private var inputTopic: TestInputTopic<String, GenericTypeMessage<Payment>>? = null
    private var outputTopic: TestOutputTopic<String, GenericTypeMessage<Payment>>? = null

    @MockK
    private lateinit var logClient: LogClient

    @BeforeEach
    fun setup() {
        val builder = StreamsBuilder()
        val testSerdes = TestSerdes.get()

        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
        logClient = mockk(relaxed = true)

        builder.stream("test-input", Consumed.with(Serdes.String(), testSerdes.serde)).errorHandlerProcessor(logClient).to("test-output")

        testDriver = TopologyTestDriver(builder.build(), KafkaConfiguration.offlineStreamConfig)
        inputTopic = testDriver?.createInputTopic(
            "test-input",
            StringSerializer(),
            testSerdes.serializer
        )
        outputTopic = testDriver?.createOutputTopic(
            "test-output",
            StringDeserializer(),
            testSerdes.deserializer
        )
    }

    @AfterEach
    fun tearDown() {
        try {
            testDriver!!.close()
        } catch (e: RuntimeException) {
            println("Ignoring exception, test failing in Windows due this exception:" + e.localizedMessage)
        }
    }

    @Test
    fun shouldLogAllErrors() {
        val payment = Faker.payment()
        val error = Faker.error()
        inputTopic?.pipeInput(GenericTypeMessage(payment, arrayOf(error, error)))
        verify(exactly = 2) {
            logClient.logError(payment, any())
        }
    }

    @Test
    fun shouldlogAndErrorIfException() {
        val payment = Faker.payment()
        val exception = LogResponseException(Exception("kaboom"))
        val error = Faker.error()
        every { logClient.logError(any(), any()) } throws exception
        inputTopic?.pipeInput("pepe", GenericTypeMessage(payment, arrayOf(error)))
    }
}
