package com.wefox.kanekotic.centralizedPayments.configurations

object LogConfiguration {
    val maxRetries = FileConfig.config[logs.maxRetries]
    val url = FileConfig.config[logs.url]
}
