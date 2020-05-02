package com.wefox.kanekotic.centralizedPayments.models


data class Error(val payment_id: String, val type: String, val error: String)
