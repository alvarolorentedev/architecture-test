package com.wefox.kanekotic.centralizedPayments.processors

import com.wefox.kanekotic.centralizedPayments.models.Error
import com.wefox.kanekotic.centralizedPayments.models.GenericTypeMessage
import com.wefox.kanekotic.centralizedPayments.models.Payment
import com.wefox.kanekotic.centralizedPayments.persistors.PaymentPersistor
import java.sql.SQLException
import org.apache.kafka.streams.kstream.KStream

fun KStream<String, GenericTypeMessage<Payment>>.savePaymentProcessor(paymentPersistor: PaymentPersistor): KStream<String, GenericTypeMessage<Payment>> {
    return this.mapValues { value ->
        try {
            if (value.errors.isEmpty()) {
                paymentPersistor.save(value.value)
            }
            value
        } catch (e: SQLException) {
            value.copy(errors = value.errors.plus(Error("database", e.message!!)))
        }
    }
}
