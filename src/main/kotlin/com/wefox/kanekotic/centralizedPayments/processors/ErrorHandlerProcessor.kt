package com.wefox.kanekotic.centralizedPayments.processors

import com.wefox.kanekotic.centralizedPayments.clients.LogClient
import com.wefox.kanekotic.centralizedPayments.models.GenericTypeMessage
import com.wefox.kanekotic.centralizedPayments.models.Payment
import org.apache.kafka.streams.kstream.KStream


fun ErrorHandlerProcessor(
    source: KStream<String, GenericTypeMessage<Payment>>,
    logClient: LogClient
): KStream<String, GenericTypeMessage<Payment>> {
    return source.peek { _, value ->
        value.errors.forEach { error -> logClient.logError(value.value, error) }
    }
}