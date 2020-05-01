package com.wefox.kanekotic.centralizedPayments.processors

import com.wefox.kanekotic.centralizedPayments.models.Payment
import org.apache.kafka.streams.kstream.KStream

fun SavePaymentProcessor(source: KStream<String, Payment>): KStream<String, Payment> {
    throw NotImplementedError("Not Implemented")
}
