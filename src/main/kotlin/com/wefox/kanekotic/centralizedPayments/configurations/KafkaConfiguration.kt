package com.wefox.kanekotic.centralizedPayments.configurations

import com.wefox.kanekotic.centralizedPayments.utils.CustomProcessingExceptionHandler
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.StreamsConfig
import java.util.*


object KafkaConfiguration {
    val OFFLINE_INPUT_TOPIC = FileConfig.config[FileConfig.kafkaOfflineTopic]
    val ONLINE_INPUT_TOPIC = FileConfig.config[FileConfig.kafkaOnlineTopic]


    val streamsConfig: Properties
        get() {
            val props = Properties()
            props[StreamsConfig.APPLICATION_ID_CONFIG] = FileConfig.config[FileConfig.kafkaApplicationId]
            props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = FileConfig.config[FileConfig.kafkaUrl]
            props[StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG] = FileConfig.config[FileConfig.cacheSize]
            props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = FileConfig.config[FileConfig.offsetReset]
            props[StreamsConfig.DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG] = CustomProcessingExceptionHandler::class.java
            return props
        }
}

