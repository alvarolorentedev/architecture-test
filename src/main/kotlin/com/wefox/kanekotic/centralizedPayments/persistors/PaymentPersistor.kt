package com.wefox.kanekotic.centralizedPayments.persistors

import com.wefox.kanekotic.centralizedPayments.models.Payment
import java.sql.Connection
import java.sql.SQLException

class PaymentPersistor(private val connection: Connection) {
    @Throws(SQLException::class)
    fun save(payment: Payment) {
        val statement = connection.prepareStatement(insertPaymentStatement)
        statement.setString(1, payment.payment_id)
        statement.setInt(2, payment.account_id)
        statement.setString(3, payment.payment_type)
        statement.setString(4, payment.credit_card)
        statement.setDouble(5, payment.amount)
        statement.execute()
        val update = connection.createStatement()
        update.execute(updateAccountstatement)
    }

    companion object {
        private const val insertPaymentStatement =
            "INSERT INTO payments (payment_id, account_id, payment_type, credit_card, amount) VALUES (?, ?, ?, ?, ?)"
        private const val updateAccountstatement = "UPDATE accounts set last_payment_date = NOW()"
    }
}
