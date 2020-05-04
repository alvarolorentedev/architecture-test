package com.wefox.kanekotic.centralizedPayments.persistor

import com.wefox.kanekotic.centralizedPayments.Faker
import com.wefox.kanekotic.centralizedPayments.models.Account
import com.wefox.kanekotic.centralizedPayments.models.Payment
import com.wefox.kanekotic.centralizedPayments.persistors.PaymentPersistor
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement
import java.sql.Timestamp
import java.time.Instant
import java.util.Date
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class PaymentPersistorTest {

    private val H2_CONNECTION_STRING = "jdbc:h2:mem:test"

    val INSERT_ACCOUNT_STATEMENT = "INSERT INTO accounts (name, email, birthdate, last_payment_date) VALUES (?, ?, ?, ?)"
    val SELECT_ACCOUNT_STATEMENT = "SELECT * FROM accounts WHERE account_id = ?"
    val SELECT_PAYMENT_STATEMENT = "SELECT * FROM payments WHERE payment_id = ?"

    private lateinit var driver: Connection

    @BeforeEach
    @Throws(SQLException::class)
    fun setup_database() {
        driver = DriverManager.getConnection(H2_CONNECTION_STRING)
        try {
            val statement: Statement = driver.createStatement()
            val content = Files.readString(Paths.get("Database/schema.sql"), Charset.defaultCharset())
            statement.executeUpdate(content)
            insertAccount(Account(1, "pepe", "pep@gmail.com", Date(), Timestamp(Instant.now().toEpochMilli())))
        } catch (ex: SQLException) {
            System.err.println(ex.message)
        }
    }

    @AfterEach
    @Throws(SQLException::class)
    fun tear_down_database() {
        driver.close()
    }

    @Test
    fun itSavesPaymentsOnTheDatabaseForExistingAccounts() {
        val subject = PaymentPersistor(driver)
        val payment = Faker.payment().copy(account_id = 1)
        val oldAccount = getAccount(1)
        subject.save(payment)
        try {
            val savedPayment = getPayment(payment.payment_id)
            val updatedAccount = getAccount(payment.account_id)
            Assertions.assertEquals(payment, savedPayment)
            Assertions.assertNotEquals(oldAccount.last_payment_date, updatedAccount.last_payment_date)
        } catch (ex: SQLException) {
            fail(ex)
        }
    }

    @Test
    fun itShouldThrowIfAccountDoesNotExist() {
        val subject = PaymentPersistor(driver)
        val payment = Faker.payment().copy(account_id = 1234)
        try {
            subject.save(payment)
            fail("should have thrown an sql exception")
        } catch (ex: SQLException) {
        }
    }

    @Throws(SQLException::class)
    fun insertAccount(account: Account) {
        val statement = driver.prepareStatement(INSERT_ACCOUNT_STATEMENT)
        statement.setString(1, account.name)
        statement.setString(2, account.email)
        statement.setObject(3, account.birthdate)
        statement.setObject(4, account.last_payment_date)
        statement.execute()
    }

    fun getAccount(accountId: Int): Account {
        val statement = driver.prepareStatement(SELECT_ACCOUNT_STATEMENT)
        statement.setInt(1, accountId)
        val result = statement.executeQuery()
        result.first()
        val name = result.getString("name")
        val email = result.getString("email")
        val birthdate = result.getDate("birthdate")
        val lastPayment = result.getTimestamp("last_payment_date")
        return Account(accountId, name, email, birthdate, lastPayment)
    }

    fun getPayment(paymentId: String): Payment {
        val statement = driver.prepareStatement(SELECT_PAYMENT_STATEMENT)
        statement.setString(1, paymentId)
        val result = statement.executeQuery()
        result.first()
        val accountId = result.getInt("account_id")
        val paymentType = result.getString("payment_type")
        val creditCard = result.getString("credit_card")
        val amount = result.getDouble("amount")
        return Payment(paymentId, accountId, paymentType, creditCard, amount, 10)
    }
}
