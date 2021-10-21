package com.ampnet.tradeservice.service.ib.wrappers

import com.ib.partial.EError
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class LoggingErrorWrapper : EError {

    companion object : KLogging()

    override fun error(e: Exception?) {
        logger.info { "error(e: $e)" }
    }

    override fun error(str: String?) {
        logger.info { "error(str: $str)" }
    }

    override fun error(id: Int, errorCode: Int, errorMsg: String?) {
        logger.info { "error(id: $id, errorCode: $errorCode, errorMsg: $errorMsg)" }
    }
}
