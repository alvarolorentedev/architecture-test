package com.wefox.kanekotic.centralizedPayments.processors

import com.wefox.kanekotic.centralizedPayments.Faker
import com.wefox.kanekotic.centralizedPayments.TestSerdes
import com.wefox.kanekotic.centralizedPayments.configurations.KafkaConfiguration
import com.wefox.kanekotic.centralizedPayments.models.Error
import com.wefox.kanekotic.centralizedPayments.models.ErrorType
import com.wefox.kanekotic.centralizedPayments.models.GenericTypeMessage
import com.wefox.kanekotic.centralizedPayments.models.Payment
import com.wefox.kanekotic.centralizedPayments.persistors.PaymentPersistor
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import java.sql.SQLException
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

class SavePaymentsProcessorTest {
    private var testDriver: TopologyTestDriver? = null
    private var inputTopic: TestInputTopic<String, GenericTypeMessage<Payment>>? = null
    private var outputTopic: TestOutputTopic<String, GenericTypeMessage<Payment>>? = null

    @MockK
    private lateinit var paymentPersistor: PaymentPersistor

    @BeforeEach
    fun setup() {
        val builder = StreamsBuilder()
        val testSerdes = TestSerdes.get()

        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
        paymentPersistor = mockk(relaxed = true)

        builder.stream("test-input", Consumed.with(Serdes.String(), testSerdes.serde))
            .savePaymentProcessor(paymentPersistor).to("test-output", Produced.with(Serdes.String(), testSerdes.serde))

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
    fun shouldCallSavePaymentAndReturnSamePaymentWithoutExceptions() {
        val payment = Faker.payment()
        inputTopic?.pipeInput("pepe", GenericTypeMessage(payment, emptyArray()))
        val result = outputTopic?.readValue()
        Assertions.assertArrayEquals(result?.errors, emptyArray())
        Assertions.assertEquals(result?.value, payment)
        verify { paymentPersistor.save(payment) }
    }

    @Test
    fun shouldNotCallSaveInDatabaseIfExisitngError() {
        val payment = Faker.payment()
        inputTopic?.pipeInput("pepe", GenericTypeMessage(payment, arrayOf(Error(ErrorType.network, "kaboom"))))
        val result = outputTopic?.readValue()
        Assertions.assertArrayEquals(result?.errors, arrayOf(Error(ErrorType.network, "kaboom")))
        Assertions.assertEquals(result?.value, payment)
        verify(exactly = 0) {
            paymentPersistor.save(payment)
        }
    }

    @Test
    fun shouldReturnSamePaymentAndErrorIfSQLException() {
        val payment = Faker.payment()
        val exception = SQLException("kaboom")
        every { paymentPersistor.save(any()) } throws exception
        inputTopic?.pipeInput("pepe", GenericTypeMessage(payment, emptyArray()))
        val result = outputTopic?.readValue()
        Assertions.assertArrayEquals(result?.errors, arrayOf(Error(ErrorType.database, exception.message!!)))
        Assertions.assertEquals(result?.value, payment)
        verify { paymentPersistor.save(payment) }
    }
}
