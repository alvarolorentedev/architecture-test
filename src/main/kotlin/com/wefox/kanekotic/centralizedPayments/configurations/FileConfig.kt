package com.wefox.kanekotic.centralizedPayments.configurations

import com.natpryce.konfig.*

object FileConfig {
    val postgressConnectionString = Key(
        "postgress.connection-string",
        stringType
    )
    val kafkaUrl = Key(
        "kafka.server-url",
        stringType
    )
    val kafkaOnlineTopic = Key(
        "kafka.input-topics.online",
        stringType
    )
    val kafkaOfflineTopic = Key(
        "kafka.input-topics.offline",
        stringType
    )
    val kafkaApplicationId = Key(
        "kafka.application-id",
        stringType
    )
    val cacheSize =
        Key("kafka.cache-size", intType)
    val offsetReset = Key(
        "kafka.offset-reset",
        stringType
    )
    val offlineToggle = Key(
        "toggles.offline",
        booleanType
    )
    val onlineToggle = Key(
        "toggles.online",
        booleanType
    )

    val config = EnvironmentVariables() overriding
            ConfigurationProperties.fromResource("application.conf")


}