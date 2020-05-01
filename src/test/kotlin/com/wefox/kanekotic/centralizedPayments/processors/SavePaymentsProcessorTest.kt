package com.wefox.kanekotic.centralizedPayments.processors

import com.wefox.kanekotic.centralizedPayments.CentralizedPayments
import com.wefox.kanekotic.centralizedPayments.models.Payment
import com.wefox.kanekotic.centralizedPayments.serdes.PaymentSerde
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.TestInputTopic
import org.apache.kafka.streams.TestOutputTopic
import org.apache.kafka.streams.TopologyTestDriver
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SavePaymentsProcessorTest {
    private var testDriver: TopologyTestDriver? = null
    private var inputTopic: TestInputTopic<String, Payment>? = null
    private var outputTopic: TestOutputTopic<String, Payment>? = null

    @BeforeEach
    fun setup() {
        val builder = StreamsBuilder()
        val paymentSerde = PaymentSerde.get()

        val source =
            builder.stream<String, Payment>("test-input")

        SavePaymentProcessor(source).to("test-output")

        testDriver = TopologyTestDriver(builder.build(), CentralizedPayments.streamsConfig)
        inputTopic = testDriver?.createInputTopic(
            "test-input",
            StringSerializer(),
            paymentSerde.serializer
        )
        outputTopic = testDriver?.createOutputTopic(
            "test-output",
            StringDeserializer(),
            paymentSerde.deserializer
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
    fun testProcess() {
        inputTopic?.pipeInput(null,GetFakePayment())
    }

    fun GetFakePayment() : Payment {
        return Payment("", 1234, "offline", "", 100, 10)
    }
}