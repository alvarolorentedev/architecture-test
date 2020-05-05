package com.wefox.kanekotic.centralizedPayments.processors

import com.github.kittinunf.result.Result
import com.wefox.kanekotic.centralizedPayments.models.Error
import com.wefox.kanekotic.centralizedPayments.models.ErrorType
import com.wefox.kanekotic.centralizedPayments.models.GenericTypeMessage
import com.wefox.kanekotic.centralizedPayments.models.Payment
import com.wefox.kanekotic.centralizedPayments.persistors.PaymentPersistor
import java.sql.SQLException
import org.apache.kafka.streams.kstream.KStream

fun KStream<String, GenericTypeMessage<Payment>>.savePaymentProcessor(paymentPersistor: PaymentPersistor): KStream<String, GenericTypeMessage<Payment>> {
    return this.mapValues { value ->
        Result.of<GenericTypeMessage<Payment>, SQLException> {
            if (value.errors.isEmpty()) {
                paymentPersistor.save(value.value)
            }
            value
        }.fold(
            {
                value
            },
            { e ->
                value.copy(errors = value.errors.plus(Error(ErrorType.database, e.message!!)))
            }
        )
    }
}
