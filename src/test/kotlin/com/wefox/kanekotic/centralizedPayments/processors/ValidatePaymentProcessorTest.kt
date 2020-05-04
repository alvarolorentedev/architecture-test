package com.wefox.kanekotic.centralizedPayments.processors

import com.wefox.kanekotic.centralizedPayments.Faker
import com.wefox.kanekotic.centralizedPayments.TestSerdes
import com.wefox.kanekotic.centralizedPayments.clients.PaymentsClient
import com.wefox.kanekotic.centralizedPayments.configurations.KafkaConfiguration
import com.wefox.kanekotic.centralizedPayments.models.Error
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
import org.apache.kafka.streams.kstream.Produced
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ValidatePaymentProcessorTest {
    private var testDriver: TopologyTestDriver? = null
    private var inputTopic: TestInputTopic<String, GenericTypeMessage<Payment>>? = null
    private var outputTopic: TestOutputTopic<String, GenericTypeMessage<Payment>>? = null

    @MockK
    private lateinit var paymentsClient: PaymentsClient

    @BeforeEach
    fun setup() {
        val builder = StreamsBuilder()
        val testSerdes = TestSerdes.get()

        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)

        paymentsClient = mockk(relaxed = true)

        builder.stream("test-input", Consumed.with(Serdes.String(), testSerdes.serde))
            .ValidatePaymentProcessor(paymentsClient).to("test-output", Produced.with(Serdes.String(), testSerdes.serde))

        testDriver = TopologyTestDriver(builder.build(), KafkaConfiguration.streamsConfig)
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
    fun shouldCallSavePaymentAndReturnSamePaymentWithoutExceptions() {
        val payment = Faker.payment()
        inputTopic?.pipeInput("pepe", GenericTypeMessage(payment, emptyArray()))
        val result = outputTopic?.readValue()
        Assertions.assertArrayEquals(result?.errors, emptyArray())
        Assertions.assertEquals(result?.value, payment)
        verify { paymentsClient.validatePayment(payment) }
    }

    @Test
    fun shouldReturnSamePaymentAndErrorIfException() {
        val payment = Faker.payment()
        val exception = Exception("kaboom")
        every { paymentsClient.validatePayment(payment) } throws exception
        inputTopic?.pipeInput("pepe", GenericTypeMessage(payment, emptyArray()))
        val result = outputTopic?.readValue()
        Assertions.assertArrayEquals(result?.errors, arrayOf(Error("network", exception.message!!)))
        Assertions.assertEquals(result?.value, payment)
        verify { paymentsClient.validatePayment(payment) }
    }
}
