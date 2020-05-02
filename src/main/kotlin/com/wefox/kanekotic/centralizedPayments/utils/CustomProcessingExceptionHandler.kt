package com.wefox.kanekotic.centralizedPayments.utils

import com.wefox.kanekotic.centralizedPayments.clients.LogClient
import com.wefox.kanekotic.centralizedPayments.serdes.PaymentSerde
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.streams.errors.ProductionExceptionHandler

open class CustomProcessingExceptionHandler : ProductionExceptionHandler {
    protected var logHandler: LogClient = LogClient

    override fun handle(
        record: ProducerRecord<ByteArray, ByteArray>,
        exception: Exception
    ): ProductionExceptionHandler.ProductionExceptionHandlerResponse {
        val payment = PaymentSerde.get().deserializer.deserialize("", record.value())
        logHandler.logError(payment, exception)
        return ProductionExceptionHandler.ProductionExceptionHandlerResponse.CONTINUE
    }

    override fun configure(configs: MutableMap<String, *>?) { }
}