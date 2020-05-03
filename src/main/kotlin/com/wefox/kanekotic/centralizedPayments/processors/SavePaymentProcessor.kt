package com.wefox.kanekotic.centralizedPayments.processors

import com.wefox.kanekotic.centralizedPayments.models.GenericTypeMessage
import com.wefox.kanekotic.centralizedPayments.models.Payment
import com.wefox.kanekotic.centralizedPayments.persistors.PaymentPersistor
import org.apache.kafka.streams.kstream.KStream
import java.sql.SQLException
import com.wefox.kanekotic.centralizedPayments.models.Error

fun SavePaymentProcessor(source: KStream<String, GenericTypeMessage<Payment>>, paymentPersistor: PaymentPersistor): KStream<String, GenericTypeMessage<Payment>> {
    return source.mapValues { value ->
        try {
            if(value.errors.isEmpty())
                paymentPersistor.save(value.value)
            value
        } catch (e: SQLException) {
            value.copy(errors = value.errors.plus(Error("database", e.message!!)))
        }
    }
}

