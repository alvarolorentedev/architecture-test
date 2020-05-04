package com.wefox.kanekotic.centralizedPayments.configurations

object PostgressConfiguration {
     val CONNECTION_STRING = FileConfig.config[FileConfig.postgressConnectionString]
}