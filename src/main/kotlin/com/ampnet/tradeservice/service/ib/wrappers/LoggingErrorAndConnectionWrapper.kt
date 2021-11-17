package com.ampnet.tradeservice.service.ib.wrappers

import com.ib.partial.EConnection
import com.ib.partial.EError
import mu.KLogging
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicReference

@Component
class LoggingErrorAndConnectionWrapper : EError, EConnection {

    companion object : KLogging()

    private val connectionFuture = CompletableFuture<Boolean>()
    private val reconnectAction = AtomicReference {}

    fun setReconnectAction(reconnectAction: () -> Unit) {
        this.reconnectAction.set(reconnectAction)
    }

    @Suppress("MagicNumber")
    fun isConnected(): Boolean =
        try {
            connectionFuture.get(20L, TimeUnit.SECONDS)
        } catch (e: TimeoutException) {
            false
        }

    override fun error(e: Exception?) {
        logger.info { "error(e: $e)" }
    }

    override fun error(str: String?) {
        logger.info { "error(str: $str)" }
    }

    @Suppress("MagicNumber")
    override fun error(id: Int, errorCode: Int, errorMsg: String?) {
        logger.info { "error(id: $id, errorCode: $errorCode, errorMsg: $errorMsg)" }

        if (errorCode == 502) {
            connectionFuture.complete(false)
        } else if (errorCode == 504) {
            reconnectAction.get().invoke()
        }
    }

    override fun connectionClosed() {
        logger.info { "connectionClosed()" }
    }

    override fun connectAck() {
        logger.info { "connectAck()" }
        connectionFuture.complete(true)
    }
}
