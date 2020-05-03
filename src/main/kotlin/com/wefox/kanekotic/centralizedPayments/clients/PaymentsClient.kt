package com.wefox.kanekotic.centralizedPayments.clients

import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.wefox.kanekotic.centralizedPayments.models.Payment

object PaymentsClient {
    fun validatePayment(payment: Payment) {
        val httpAsync = "http://localhost:8997/log"
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
                        val ex = result.getException()
                        println(ex)
                    }
                    is Result.Success -> {
                        val data = result.get()
                        println(data)
                    }
                }
            }

        httpAsync.join()
    }
}