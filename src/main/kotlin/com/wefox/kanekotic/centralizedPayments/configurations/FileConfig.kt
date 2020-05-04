package com.wefox.kanekotic.centralizedPayments.configurations

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
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

object toggles : ConfigSpec() {
    val online by required<Boolean>()
    val offline by required<Boolean>()
}



object FileConfig {
    val config = Config {
            addSpec(postgress)
            addSpec(kafka)
            addSpec(toggles)
    }.from.hocon.resource("${System.getProperty("config.env")}.conf")
}
