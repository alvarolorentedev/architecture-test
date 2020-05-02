package com.wefox.kanekotic.centralizedPayments.models

data class GenericTypeMessage<T>(val value: T, val error: Array<Error>)