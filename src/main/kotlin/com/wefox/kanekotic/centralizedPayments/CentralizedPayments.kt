package com.wefox.kanekotic.centralizedPayments

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig
import java.util.*


object CentralizedPayments {
    const val OFFLINE_INPUT_TOPIC = "offline"
    const val ONLINE_INPUT_TOPIC = "online"


    val streamsConfig: Properties
        get() {
            val props = Properties()
            props[StreamsConfig.APPLICATION_ID_CONFIG] = "streams-centralized-payments"
            props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:29092"
            props[StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG] = 0
            props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
            props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
            props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
            return props
        }
}

