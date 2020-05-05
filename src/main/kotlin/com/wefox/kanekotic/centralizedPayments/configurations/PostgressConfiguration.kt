package com.wefox.kanekotic.centralizedPayments.configurations

import org.jasypt.util.text.StrongTextEncryptor

object PostgressConfiguration {
    val textEncryptor: StrongTextEncryptor = StrongTextEncryptor()

    init {
        textEncryptor.setPassword(FileConfig.config[jasypt.password])
    }

    fun getConnectionString(): String {
        return textEncryptor.decrypt(FileConfig.config[postgress.connectionString])
    }
}
