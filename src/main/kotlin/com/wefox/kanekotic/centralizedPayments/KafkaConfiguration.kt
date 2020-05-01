package com.wefox.kanekotic.centralizedPayments

import com.wefox.kanekotic.centralizedPayments.serdes.PaymentSerde
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.StreamsConfig
import java.util.*


object KafkaConfiguration {
    const val OFFLINE_INPUT_TOPIC = "offline"
    const val ONLINE_INPUT_TOPIC = "online"


    val streamsConfig: Properties
        get() {
            val props = Properties()
            val paymentSerde = PaymentSerde.get()
            props[StreamsConfig.APPLICATION_ID_CONFIG] = "streams-centralized-payments"
            props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:29092"
            props[StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG] = 0
            props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
            return props
        }
}

