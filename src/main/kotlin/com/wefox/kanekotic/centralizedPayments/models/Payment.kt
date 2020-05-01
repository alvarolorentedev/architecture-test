package com.wefox.kanekotic.centralizedPayments.models

data class Payment(val payment_id: String, val account_id: Int, val payment_type: String, val credit_card: String, val amount: Int, val delay: Int)