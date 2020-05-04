package com.wefox.kanekotic.centralizedPayments.processors

import com.wefox.kanekotic.centralizedPayments.clients.PaymentsClient
import com.wefox.kanekotic.centralizedPayments.clients.PaymentsResponseException
import com.wefox.kanekotic.centralizedPayments.models.Error
import com.wefox.kanekotic.centralizedPayments.models.GenericTypeMessage
import com.wefox.kanekotic.centralizedPayments.models.Payment
import org.apache.kafka.streams.kstream.KStream

fun KStream<String, GenericTypeMessage<Payment>>.validatePaymentProcessor(paymentClient: PaymentsClient): KStream<String, GenericTypeMessage<Payment>> {
    return this.mapValues { value ->
        try {
            paymentClient.validatePayment(value.value)
            value
        } catch (e: PaymentsResponseException) {
            value.copy(errors = value.errors.plus(Error("network", e.message!!)))
        }
    }
}
