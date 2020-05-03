package com.wefox.kanekotic.centralizedPayments.clients

import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.wefox.kanekotic.centralizedPayments.models.Error
import com.wefox.kanekotic.centralizedPayments.models.Payment
import java.sql.SQLException

object LogClient {
    fun logError(payment: Payment, error: Error) {
        val httpAsync = "http://localhost:8997/log"
            .httpPost()
            .header("Content-Type", "application/json")
            .body(
                """{
                | 'payment_id': '${payment.payment_id}',
                | 'error_type': '${error.type}',
                | 'error_description': '${error.message}'
                |}""".trimMargin()
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