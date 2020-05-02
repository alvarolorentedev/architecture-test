package com.wefox.kanekotic.centralizedPayments.persistors

import com.wefox.kanekotic.centralizedPayments.models.Payment
import java.sql.Connection
import java.sql.SQLException

class PaymentPersistor(private val connection: Connection) {

    private val INSERT_STATEMENT = "INSERT INTO payments (payment_id, account_id, payment_type, credit_card, amount) VALUES (?, ?, ?, ?, ?)"
    private val UPDATE_ACCOUNT_STATEMENT = "UPDATE accounts set last_payment_date = NOW()"

    @Throws(SQLException::class)
    fun save(payment: Payment) {
        val statement = connection.prepareStatement(INSERT_STATEMENT)
        statement.setString(1, payment.payment_id)
        statement.setInt(2, payment.account_id)
        statement.setString(3, payment.payment_type)
        statement.setString(4, payment.credit_card)
        statement.setDouble(5, payment.amount)
        statement.execute()
        val update = connection.createStatement()
        update.execute(UPDATE_ACCOUNT_STATEMENT)
    }

}