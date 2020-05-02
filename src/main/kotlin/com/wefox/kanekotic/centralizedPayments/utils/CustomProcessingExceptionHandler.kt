package com.wefox.kanekotic.centralizedPayments.utils

import com.github.kittinunf.fuel.httpPost
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.streams.errors.ProductionExceptionHandler
import com.github.kittinunf.result.Result
import com.wefox.kanekotic.centralizedPayments.clients.LogClient
import com.wefox.kanekotic.centralizedPayments.serdes.PaymentSerde

class CustomProcessingExceptionHandler : ProductionExceptionHandler {
    private lateinit var logHandler: LogClient

    override fun handle(
        record: ProducerRecord<ByteArray, ByteArray>,
        exception: Exception
    ): ProductionExceptionHandler.ProductionExceptionHandlerResponse {
        val payment = PaymentSerde.get().deserializer.deserialize("", record.value())
        logHandler.logError(payment, exception)
        return ProductionExceptionHandler.ProductionExceptionHandlerResponse.CONTINUE
    }

    override fun configure(configs: MutableMap<String, *>?) {
        logHandler = configs?.get("logHandler") as LogClient
    }
}