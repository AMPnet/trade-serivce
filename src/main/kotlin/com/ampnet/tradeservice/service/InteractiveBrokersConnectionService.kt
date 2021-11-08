package com.ampnet.tradeservice.service

import com.ampnet.tradeservice.configuration.InteractiveBrokersProperties
import com.ib.client.EClientSocket
import com.ib.client.EJavaSignal
import com.ib.client.EReader
import mu.KLogging
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Service
import java.io.IOException
import kotlin.concurrent.thread

@Service
class InteractiveBrokersConnectionService(
    correlationService: InteractiveBrokersCorrelationService,
    private val interactiveBrokersProperties: InteractiveBrokersProperties
) :
    InitializingBean, DisposableBean {

    companion object : KLogging()

    private val signal = EJavaSignal()
    final val client = EClientSocket(correlationService, signal)

    @Suppress("MagicNumber")
    override fun afterPropertiesSet() {
        logger.info {
            "Connecting to InteractiveBrokers API @ ${interactiveBrokersProperties.host}:" +
                "${interactiveBrokersProperties.port}..."
        }
        client.eConnect(interactiveBrokersProperties.host, interactiveBrokersProperties.port, 1)
        logger.info { "Connection to InteractiveBrokers API successful" }

        val reader = EReader(client, signal)
        reader.start()

        thread {
            while (client.isConnected) {
                signal.waitForSignal()
                try {
                    reader.processMsgs()
                } catch (e: IOException) {
                    logger.error(e) { "Error while reading message from IB API" }
                }
            }
        }

        // TODO extract?
        client.reqMarketDataType(3) // Set to delayed market data (15 min)
    }

    override fun destroy() {
        logger.info { "Disconnecting from InteractiveBrokers API..." }
        client.eDisconnect()
    }
}
