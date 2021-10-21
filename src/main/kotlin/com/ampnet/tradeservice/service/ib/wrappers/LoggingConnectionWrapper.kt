package com.ampnet.tradeservice.service.ib.wrappers

import com.ib.partial.EConnection
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class LoggingConnectionWrapper : EConnection {

    companion object : KLogging()

    override fun connectionClosed() {
        logger.info { "connectionClosed()" }
    }

    override fun connectAck() {
        logger.info { "connectAck()" }
    }
}
