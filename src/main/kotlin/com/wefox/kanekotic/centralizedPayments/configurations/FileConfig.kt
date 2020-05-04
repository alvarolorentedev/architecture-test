package com.wefox.kanekotic.centralizedPayments.configurations

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import com.uchuhimo.konf.Feature
import com.uchuhimo.konf.source.hocon

object postgress : ConfigSpec() {
    val connectionString by required<String>()
}

object kafka : ConfigSpec() {
    object topics : ConfigSpec() {
        val online by required<String>()
        val offline by required<String>()
    }

    val serverUrl by required<String>()
    val applicationId by required<String>()
    val offsetReset by required<String>()
    val cacheSize by required<Int>()
}

object logs : ConfigSpec() {
    val url by required<String>()
}

object payments : ConfigSpec() {
    val url by required<String>()
}

object toggles : ConfigSpec() {
    val online by required<Boolean>()
    val offline by required<Boolean>()
}

object FileConfig {
    val config = Config {
        addSpec(postgress)
        addSpec(kafka)
        addSpec(logs)
        addSpec(payments)
        addSpec(toggles)
    }
        .enable(Feature.OPTIONAL_SOURCE_BY_DEFAULT)
        .from.hocon.resource("application.conf")
        .from.hocon.resource("${System.getProperty("config.env")}.conf")
}