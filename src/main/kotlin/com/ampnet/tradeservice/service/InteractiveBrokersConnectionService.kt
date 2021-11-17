package com.ampnet.tradeservice.service

import com.ampnet.tradeservice.configuration.InteractiveBrokersProperties
import com.ampnet.tradeservice.service.ib.wrappers.LoggingErrorAndConnectionWrapper
import com.ib.client.EClientSocket
import com.ib.client.EJavaSignal
import com.ib.client.EReader
import mu.KLogging
import org.springframework.beans.factory.BeanInitializationException
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Service
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

@Service
class InteractiveBrokersConnectionService(
    correlationService: InteractiveBrokersCorrelationService,
    private val errorWrapper: LoggingErrorAndConnectionWrapper,
    private val interactiveBrokersProperties: InteractiveBrokersProperties
) : InitializingBean, DisposableBean {

    companion object : KLogging()

    private val signal = EJavaSignal()
    private val readerRunning = AtomicBoolean(false)
    private val isReconnecting = AtomicBoolean(false)

    final val client = EClientSocket(correlationService, signal)

    override fun afterPropertiesSet() {
        logger.info {
            "Connecting to InteractiveBrokers API @ ${interactiveBrokersProperties.host}:" +
                "${interactiveBrokersProperties.port}..."
        }
        client.eConnect(interactiveBrokersProperties.host, interactiveBrokersProperties.port, 1)
        logger.info { "Connection to InteractiveBrokers API requested" }

        startReaderThread()

        if (!errorWrapper.isConnected()) {
            throw BeanInitializationException("Unable to connect to InteractiveBrokers API")
        }

        setMarketDataTypeToDelayed()

        errorWrapper.setReconnectAction {
            if (!isReconnecting.get()) {
                isReconnecting.set(true)

                try {
                    client.eDisconnect()
                    stopReaderThread()

                    logger.info { "Reconnecting to InteractiveBrokers API..." }
                    client.eConnect(interactiveBrokersProperties.host, interactiveBrokersProperties.port, 1)

                    startReaderThread()
                    setMarketDataTypeToDelayed()
                } finally {
                    isReconnecting.set(false)
                }
            }
        }
    }

    override fun destroy() {
        logger.info { "Disconnecting from InteractiveBrokers API..." }
        client.eDisconnect()
        stopReaderThread()
    }

    private fun startReaderThread() {
        val reader = EReader(client, signal)
        reader.start()

        readerRunning.set(true)

        thread {
            while (client.isConnected && readerRunning.get()) {
                signal.waitForSignal()
                try {
                    reader.processMsgs()
                } catch (e: IOException) {
                    logger.error(e) { "Error while reading message from IB API" }
                }
            }
        }
    }

    private fun stopReaderThread() {
        readerRunning.set(false)
    }

    @Suppress("MagicNumber")
    private fun setMarketDataTypeToDelayed() {
        client.reqMarketDataType(3) // Set to delayed market data (15 min)
    }
}
