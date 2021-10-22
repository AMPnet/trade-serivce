package com.ampnet.tradeservice.exception

class ResourceNotFoundException(val errorCode: ErrorCode, exceptionMessage: String) : Exception(exceptionMessage)

class InternalException(val errorCode: ErrorCode, exceptionMessage: String, throwable: Throwable? = null) :
    Exception(exceptionMessage, throwable)

class InvalidRequestException(
    val errorCode: ErrorCode,
    exceptionMessage: String,
    throwable: Throwable? = null
) : Exception(exceptionMessage, throwable)
