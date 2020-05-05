package com.wefox.kanekotic.centralizedPayments.configurations

object PaymentServiceConfiguration {
    val maxRetries = FileConfig.config[payments.maxRetries]
    val url = FileConfig.config[payments.url]
}
