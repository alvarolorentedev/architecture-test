package com.wefox.kanekotic.centralizedPayments.clients

import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.wefox.kanekotic.centralizedPayments.configurations.LogConfiguration
import com.wefox.kanekotic.centralizedPayments.models.Error
import com.wefox.kanekotic.centralizedPayments.models.Payment

class LogResponseException(cause: Throwable?) : Exception(cause)

class LogClient(private val configuration: LogConfiguration) {
    fun logError(payment: Payment, error: Error) {
        val httpAsync = "${configuration.url}/log"
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
                        throw LogResponseException(result.getException())
                    }
                }
            }

        httpAsync.join()
    }
}
