package com.wefox.kanekotic.centralizedPayments.processors

import com.wefox.kanekotic.centralizedPayments.KafkaConfiguration
import com.wefox.kanekotic.centralizedPayments.models.Payment
import com.wefox.kanekotic.centralizedPayments.persistors.PaymentPersistor
import com.wefox.kanekotic.centralizedPayments.serdes.PaymentSerde
import io.mockk.MockKAnnotations
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
import org.apache.kafka.streams.kstream.Consumed;
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SavePaymentsProcessorTest {
    private var testDriver: TopologyTestDriver? = null
    private var inputTopic: TestInputTopic<String, Payment>? = null
    private var outputTopic: TestOutputTopic<String, Payment>? = null

    @MockK
    private lateinit var paymentPersistor: PaymentPersistor


    @BeforeEach
    fun setup() {
        val builder = StreamsBuilder()
        val paymentSerde = PaymentSerde.get()

        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)

        val source =
            builder.stream<String, Payment>("test-input", Consumed.with(Serdes.String(), paymentSerde.serde))

        paymentPersistor = mockk(relaxed = true)
        SavePaymentProcessor(source, paymentPersistor).to("test-output")

        testDriver = TopologyTestDriver(builder.build(), KafkaConfiguration.streamsConfig)
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
    fun shouldCallSavePayment() {
        val payment = GetFakePayment()
        inputTopic?.pipeInput("pepe",payment)
        verify { paymentPersistor.save(payment) }
    }

    fun GetFakePayment() : Payment {
        return Payment("", 1234, "offline", "", 100, 10)
    }
}