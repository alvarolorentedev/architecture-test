package com.wefox.kanekotic.centralizedPayments.processors

import com.wefox.kanekotic.centralizedPayments.models.GenericTypeMessage
import com.wefox.kanekotic.centralizedPayments.models.Payment
import com.wefox.kanekotic.centralizedPayments.persistors.PaymentPersistor
import org.apache.kafka.streams.kstream.KStream

fun SavePaymentProcessor(source: KStream<String, GenericTypeMessage<Payment>>, paymentPersistor: PaymentPersistor): KStream<String, GenericTypeMessage<Payment>> {
    return source.peek { _, value -> paymentPersistor.save(value.value) }
}

