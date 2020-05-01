package com.wefox.kanekotic.centralizedPayments.processors

import com.wefox.kanekotic.centralizedPayments.models.Payment
import com.wefox.kanekotic.centralizedPayments.persistors.PaymentPersistor
import org.apache.kafka.streams.kstream.KStream

fun SavePaymentProcessor(source: KStream<String, Payment>, paymentPersistor: PaymentPersistor): KStream<String, Payment> {
    return source.peek { _, value -> paymentPersistor.save(value)

    }
}
