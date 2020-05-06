package com.wefox.kanekotic.centralizedPayments.configurations

import org.jasypt.util.text.BasicTextEncryptor

object PostgressConfiguration {

    fun getConnectionString(): String {
        val textEncryptor = BasicTextEncryptor()
        textEncryptor.setPassword(FileConfig.config[jasypt.password])
        return textEncryptor.decrypt(FileConfig.config[postgress.connectionString])
    }
}
