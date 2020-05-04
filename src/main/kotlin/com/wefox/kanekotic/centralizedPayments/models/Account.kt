package com.wefox.kanekotic.centralizedPayments.models

import java.sql.Timestamp
import java.util.Date

data class Account(val account_id: Int, val name: String, val email: String, val birthdate: Date, val last_payment_date: Timestamp)
