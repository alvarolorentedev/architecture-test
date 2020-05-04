package com.wefox.kanekotic.centralizedPayments

import com.wefox.kanekotic.centralizedPayments.clients.LogClient
import com.wefox.kanekotic.centralizedPayments.clients.PaymentsClient
import com.wefox.kanekotic.centralizedPayments.configurations.KafkaConfiguration
import com.wefox.kanekotic.centralizedPayments.configurations.KafkaConfiguration.streamsConfig
import com.wefox.kanekotic.centralizedPayments.configurations.LogConfiguration
import com.wefox.kanekotic.centralizedPayments.configurations.PostgressConfiguration
import com.wefox.kanekotic.centralizedPayments.configurations.paymentServiceConfiguration
import com.wefox.kanekotic.centralizedPayments.persistors.PaymentPersistor
import com.wefox.kanekotic.centralizedPayments.processors.ErrorHandlerProcessor
import com.wefox.kanekotic.centralizedPayments.processors.SavePaymentProcessor
import com.wefox.kanekotic.centralizedPayments.processors.ValidatePaymentProcessor
import com.wefox.kanekotic.centralizedPayments.serdes.PaymentSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import java.sql.DriverManager
import java.util.concurrent.CountDownLatch


class Main {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val props = streamsConfig
            val builder = StreamsBuilder()
            val paymentSerde = PaymentSerde.get()
            val paymentPersistor =
                PaymentPersistor(DriverManager.getConnection(PostgressConfiguration.CONNECTION_STRING))

            if (ToggleConfiguration.offline) {
                val stream = builder.stream(
                    KafkaConfiguration.OFFLINE_INPUT_TOPIC,
                    Consumed.with(Serdes.String(), paymentSerde.serde)
                ).peek{ _, value -> println(value) }

                SavePaymentProcessor(stream, paymentPersistor)
                ErrorHandlerProcessor(stream, LogClient(LogConfiguration))
            }

            if (ToggleConfiguration.online) {
                val stream = builder.stream(
                    KafkaConfiguration.ONLINE_INPUT_TOPIC,
                    Consumed.with(Serdes.String(), paymentSerde.serde)
                )
                val validatePaymentProcessor = ValidatePaymentProcessor(stream, PaymentsClient(paymentServiceConfiguration))
                SavePaymentProcessor(validatePaymentProcessor, paymentPersistor)
            }
            val streams = KafkaStreams(builder.build(), props)
            val latch = CountDownLatch(1)

            Runtime.getRuntime().addShutdownHook(object : Thread("streams-centralized-payments-shutdown-hook") {
                override fun run() {
                    streams.close()
                    latch.countDown()
                }
            })
            try {
                println("starting...")
                streams.start()
                latch.await()
            } catch (e: Throwable) {
                System.exit(1)
            }

            println("closing...")
            System.exit(0)
        }
    }
}