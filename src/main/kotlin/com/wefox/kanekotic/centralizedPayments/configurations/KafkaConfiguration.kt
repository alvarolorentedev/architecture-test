package com.wefox.kanekotic.centralizedPayments.configurations

import com.wefox.kanekotic.centralizedPayments.utils.CustomProcessingExceptionHandler
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.StreamsConfig
import java.util.*


object KafkaConfiguration {
    const val OFFLINE_INPUT_TOPIC = "offline"
    const val ONLINE_INPUT_TOPIC = "online"


    val streamsConfig: Properties
        get() {
            val props = Properties()
            props[StreamsConfig.APPLICATION_ID_CONFIG] = "streams-centralized-payments"
            props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "kafka-server:9092"
            props[StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG] = 0
            props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
            props[StreamsConfig.DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG] = CustomProcessingExceptionHandler::class.java
            return props
        }
}

