package com.wefox.kanekotic.centralizedPayments.processors

import com.github.kittinunf.result.Result
import com.github.kittinunf.result.failure
import com.wefox.kanekotic.centralizedPayments.clients.LogClient
import com.wefox.kanekotic.centralizedPayments.clients.LogResponseException
import com.wefox.kanekotic.centralizedPayments.models.GenericTypeMessage
import com.wefox.kanekotic.centralizedPayments.models.Payment
import org.apache.kafka.streams.kstream.KStream

fun KStream<String, GenericTypeMessage<Payment>>.errorHandlerProcessor(
    logClient: LogClient
): KStream<String, GenericTypeMessage<Payment>> {
    return this.peek { _, value ->
        Result.of<Unit, LogResponseException> {
            value.errors.forEach {
                error ->
                logClient.logError(value.value, error)
            }
        }.failure { e ->
            println(e)
        }
    }
}
