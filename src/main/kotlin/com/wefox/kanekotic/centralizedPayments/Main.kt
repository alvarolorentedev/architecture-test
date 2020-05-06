package com.wefox.kanekotic.centralizedPayments

import com.github.kittinunf.result.Result
import com.wefox.kanekotic.centralizedPayments.clients.LogClient
import com.wefox.kanekotic.centralizedPayments.clients.PaymentsClient
import com.wefox.kanekotic.centralizedPayments.configurations.*
import com.wefox.kanekotic.centralizedPayments.configurations.KafkaConfiguration.streamsConfig
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
        fun onlineStream(): KafkaStreams {
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

            val streamsOffline = KafkaStreams(builderOffline.build(), streamsConfig)
            streamsOffline.setUncaughtExceptionHandler { _, e ->
                logClient.logError(Payment("00000", 0, "online", "00000", 0.0, 0), Error(ErrorType.other, e.message!!))
                System.exit(1)
            }
            return streamsOffline
        }

        fun offlineStream(): KafkaStreams {
            val builderOnline = StreamsBuilder()
            val paymentSerde = PaymentSerde.get()
            val logClient = LogClient(LogConfiguration)
            val paymentPersistor =
                PaymentPersistor(DriverManager.getConnection(PostgressConfiguration.getConnectionString()))
            if (ToggleConfiguration.online) {
                builderOnline.stream(
                    KafkaConfiguration.ONLINE_INPUT_TOPIC,
                    Consumed.with(Serdes.String(), paymentSerde.serde)
                )
                    .validatePaymentProcessor(PaymentsClient(PaymentServiceConfiguration))
                    .savePaymentProcessor(paymentPersistor)
                    .errorHandlerProcessor(logClient)
            }
            val streamsOnline = KafkaStreams(builderOnline.build(), streamsConfig)
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
                println("starting...")
                if (ToggleConfiguration.offline) {
                    streamsOffline.start()
                }
                if (ToggleConfiguration.online) {
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
