package com.ampnet.tradeservice.service

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
class InteractiveBrokersApiService(correlationService: InteractiveBrokersCorrelationService) : InitializingBean,
    DisposableBean {

    companion object : KLogging()

    private val signal = EJavaSignal()
    private val client = EClientSocket(correlationService, signal)
    private val reader = EReader(client, signal)

    override fun afterPropertiesSet() {
        logger.info { "Connecting to InteractiveBrokers API..." }
        client.eConnect("127.0.0.1", 7497, 1) // TODO extract as properties
        reader.start()
        logger.info { "Connection to InteractiveBrokers API successful" }

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
    }

    override fun destroy() {
        logger.info { "Disconnecting from InteractiveBrokers API..." }
        client.eDisconnect()
    }

    // TODO methods...
}
