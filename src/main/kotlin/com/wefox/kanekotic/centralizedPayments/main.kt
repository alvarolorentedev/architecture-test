package com.wefox.kanekotic.centralizedPayments

import com.wefox.kanekotic.centralizedPayments.CentralizedPayments.streamsConfig
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import java.util.concurrent.CountDownLatch


fun main() {
    val props = streamsConfig
    val builder = StreamsBuilder()
    builder.stream<String, String>(CentralizedPayments.OFFLINE_INPUT_TOPIC).mapValues { value -> println(value) }
    builder.stream<String, String>(CentralizedPayments.ONLINE_INPUT_TOPIC).mapValues { value -> println(value) }
    val streams = KafkaStreams(builder.build(), props)
    val latch = CountDownLatch(1)

    Runtime.getRuntime().addShutdownHook(object : Thread("streams-centralized-payments-shutdown-hook") {
        override fun run() {
            streams.close()
            latch.countDown()
        }
    })
    try {
        streams.start()
        latch.await()
    } catch (e: Throwable) {
        System.exit(1)
    }
    System.exit(0)
}