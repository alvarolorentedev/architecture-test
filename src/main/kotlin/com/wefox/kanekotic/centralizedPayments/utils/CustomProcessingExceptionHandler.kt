package com.wefox.kanekotic.centralizedPayments.utils

import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.streams.errors.ProductionExceptionHandler

class CustomProcessingExceptionHandler : ProductionExceptionHandler {
    override fun handle(
        record: ProducerRecord<ByteArray, ByteArray>,
        exception: Exception
    ): ProductionExceptionHandler.ProductionExceptionHandlerResponse {
        println("PASSING")
        return ProductionExceptionHandler.ProductionExceptionHandlerResponse.CONTINUE
    }

    override fun configure(configs: MutableMap<String, *>?) {
        //NOTHING TO CONFIGURE
    }
}