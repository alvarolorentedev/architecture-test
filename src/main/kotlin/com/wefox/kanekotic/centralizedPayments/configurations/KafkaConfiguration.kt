package com.wefox.kanekotic.centralizedPayments.configurations

import com.wefox.kanekotic.centralizedPayments.utils.CustomProcessingExceptionHandler
import java.util.Properties
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.StreamsConfig

object KafkaConfiguration {
    val OFFLINE_INPUT_TOPIC = FileConfig.config[kafka.topics.offline]
    val ONLINE_INPUT_TOPIC = FileConfig.config[kafka.topics.online]

    val offlineStreamConfig: Properties
        get() {
            val props = Properties()
            props[StreamsConfig.APPLICATION_ID_CONFIG] = "${FileConfig.config[kafka.applicationId]}-offline"
            props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = FileConfig.config[kafka.serverUrl]
            props[StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG] = FileConfig.config[kafka.cacheSize]
            props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = FileConfig.config[kafka.offsetReset]
            props[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = true
            props[StreamsConfig.MAX_TASK_IDLE_MS_CONFIG ] = Long.MAX_VALUE
            props[StreamsConfig.DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG] = CustomProcessingExceptionHandler::class.java
            return props
        }

    val onlineStreamConfig: Properties
        get() {
            val props = Properties()
            props[StreamsConfig.APPLICATION_ID_CONFIG] = "${FileConfig.config[kafka.applicationId]}-online"
            props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = FileConfig.config[kafka.serverUrl]
            props[StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG] = FileConfig.config[kafka.cacheSize]
            props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = FileConfig.config[kafka.offsetReset]
            props[StreamsConfig.DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG] = CustomProcessingExceptionHandler::class.java
            props[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = true
            props[StreamsConfig.MAX_TASK_IDLE_MS_CONFIG ] = Long.MAX_VALUE
            return props
        }
}
