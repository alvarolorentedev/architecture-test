package com.wefox.kanekotic.centralizedPayments.configurations

object PostgressConfiguration {
    val CONNECTION_STRING: String = FileConfig.config[postgress.connectionString]
}
