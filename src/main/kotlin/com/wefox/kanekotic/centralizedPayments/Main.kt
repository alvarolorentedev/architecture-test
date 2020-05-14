package com.wefox.kanekotic.centralizedPayments

import com.github.kittinunf.result.Result
import com.wefox.kanekotic.centralizedPayments.clients.LogClient
import com.wefox.kanekotic.centralizedPayments.clients.PaymentsClient
import com.wefox.kanekotic.centralizedPayments.configurations.KafkaConfiguration
import com.wefox.kanekotic.centralizedPayments.configurations.KafkaConfiguration.offlineStreamConfig
import com.wefox.kanekotic.centralizedPayments.configurations.KafkaConfiguration.onlineStreamConfig
import com.wefox.kanekotic.centralizedPayments.configurations.LogConfiguration
import com.wefox.kanekotic.centralizedPayments.configurations.PaymentServiceConfiguration
import com.wefox.kanekotic.centralizedPayments.configurations.PostgressConfiguration
import com.wefox.kanekotic.centralizedPayments.models.Error
import com.wefox.kanekotic.centralizedPayments.models.ErrorType
import com.wefox.kanekotic.centralizedPayments.models.Payment
import com.wefox.kanekotic.centralizedPayments.persistors.PaymentPersistor
import com.wefox.kanekotic.centralizedPayments.processors.errorHandlerProcessor
import com.wefox.kanekotic.centralizedPayments.processors.savePaymentProcessor
import com.wefox.kanekotic.centralizedPayments.processors.validatePaymentProcessor
import com.wefox.kanekotic.centralizedPayments.serdes.PaymentSerde
import java.sql.DriverManager
import java.util.concurrent.CountDownLatch
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed

class Main {
    companion object {
        fun offlineStream(): KafkaStreams {
            val builderOffline = StreamsBuilder()
            val paymentSerde = PaymentSerde.get()
            val logClient = LogClient(LogConfiguration)
            val paymentPersistor =
                PaymentPersistor(DriverManager.getConnection(PostgressConfiguration.getConnectionString()))
            if (ToggleConfiguration.offline) {
                builderOffline.stream(
                    KafkaConfiguration.OFFLINE_INPUT_TOPIC,
                    Consumed.with(Serdes.String(), paymentSerde.serde)
                )
                    .savePaymentProcessor(paymentPersistor)
                    .errorHandlerProcessor(logClient)
            }

            val streamsOffline = KafkaStreams(builderOffline.build(), offlineStreamConfig)
            streamsOffline.setUncaughtExceptionHandler { _, e ->
                logClient.logError(Payment("00000", 0, "offline", "00000", 0.0, 0), Error(ErrorType.other, e.message!!))
                System.exit(1)
            }
            return streamsOffline
        }

        fun onlineStream(): KafkaStreams {
            val builderOnline = StreamsBuilder()
            val paymentSerde = PaymentSerde.get()
            val logClient = LogClient(LogConfiguration)
            val paymentPersistor =
                PaymentPersistor(DriverManager.getConnection(PostgressConfiguration.getConnectionString()))
            builderOnline.stream(
                KafkaConfiguration.ONLINE_INPUT_TOPIC,
                Consumed.with(Serdes.String(), paymentSerde.serde)
            )
                .validatePaymentProcessor(PaymentsClient(PaymentServiceConfiguration))
                .savePaymentProcessor(paymentPersistor)
                .errorHandlerProcessor(logClient)
            val streamsOnline = KafkaStreams(builderOnline.build(), onlineStreamConfig)
            streamsOnline.setUncaughtExceptionHandler { _, e ->
                logClient.logError(Payment("00000", 0, "online", "00000", 0.0, 0), Error(ErrorType.other, e.message!!))
                System.exit(1)
            }
            return streamsOnline
        }

        @JvmStatic fun main(args: Array<String>) {
            val streamsOnline = onlineStream()
            val streamsOffline = offlineStream()
            val latch = CountDownLatch(1)

            Runtime.getRuntime().addShutdownHook(object : Thread("streams-centralized-payments-shutdown-hook") {
                override fun run() {
                    streamsOffline.close()
                    streamsOnline.close()
                    latch.countDown()
                }
            })

            Result.of<Unit, Exception> {
                if (ToggleConfiguration.offline) {
                    println("starting offline...")
                    streamsOffline.start()
                }
                if (ToggleConfiguration.online) {
                    println("starting online...")
                    streamsOnline.start()
                }
                latch.await()
            }.fold(
                {
                    println("closing...")
                    System.exit(0)
                },
                {
                    System.exit(1)
                }
            )
        }
    }
}
