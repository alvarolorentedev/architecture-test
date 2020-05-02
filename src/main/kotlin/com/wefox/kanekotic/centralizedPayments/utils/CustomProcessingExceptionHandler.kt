package com.wefox.kanekotic.centralizedPayments.utils

import com.github.kittinunf.fuel.httpPost
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.streams.errors.ProductionExceptionHandler
import com.github.kittinunf.result.Result
import com.wefox.kanekotic.centralizedPayments.serdes.PaymentSerde

class CustomProcessingExceptionHandler : ProductionExceptionHandler {
    override fun handle(
        record: ProducerRecord<ByteArray, ByteArray>,
        exception: Exception
    ): ProductionExceptionHandler.ProductionExceptionHandlerResponse {
        val payment = PaymentSerde.get().deserializer.deserialize("", record.value())
        val httpAsync = "http://localhost:8997/log"
            .httpPost()
            .header("Content-Type", "application/json")
            .body("""{
                | 'payment_id': '${payment.payment_id}',
                | 'error_type': 'other',
                | 'error_description': '${exception.message}'
                |}""".trimMargin())
            .responseString { _, _, result ->
                when (result) {
                    is Result.Failure -> {
                        val ex = result.getException()
                        println(ex)
                    }
                    is Result.Success -> {
                        val data = result.get()
                        println(data)
                    }
                }
            }

        httpAsync.join()
        return ProductionExceptionHandler.ProductionExceptionHandlerResponse.CONTINUE
    }

    override fun configure(configs: MutableMap<String, *>?) {
        //NOTHING TO CONFIGURE
    }
}