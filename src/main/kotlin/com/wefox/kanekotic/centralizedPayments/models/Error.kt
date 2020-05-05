package com.wefox.kanekotic.centralizedPayments.models

enum class ErrorType { database, network, other }

data class Error(val type: ErrorType, val message: String)
