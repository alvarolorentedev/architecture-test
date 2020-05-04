package com.wefox.kanekotic.centralizedPayments.clients

import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.wefox.kanekotic.centralizedPayments.configurations.PaymentServiceConfiguration
import com.wefox.kanekotic.centralizedPayments.models.Payment

class PaymentsResponseException(cause: Throwable?) : Exception(cause)

class PaymentsClient(private val configuration: PaymentServiceConfiguration) {
    fun validatePayment(payment: Payment) {
        val httpAsync = "${configuration.url}/log"
            .httpPost()
            .header("Content-Type", "application/json")
            .body(
                """"{
                    |'payment_id': ${payment.payment_id}, 
                    |'account_id': ${payment.account_id}, 
                    |'payment_type': 'online',
                    |'credit_card': ${payment.credit_card}',
                    |'amount':${payment.amount}
                    |}"""".trimMargin()
            )
            .responseString { _, _, result ->
                when (result) {
                    is Result.Failure -> {
                        throw PaymentsResponseException(result.getException())
                    }
                }
            }

        httpAsync.join()
    }
}
