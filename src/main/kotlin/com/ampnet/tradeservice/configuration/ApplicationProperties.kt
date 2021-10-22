package com.ampnet.tradeservice.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "custom")
class ApplicationProperties {
    val chainEthereum = ChainProperties()
    val chainGoerli = ChainProperties()
    val chainMatic = ChainProperties()
    val chainMumbai = ChainProperties()
    val chainHardhatTestnet = ChainProperties()
    lateinit var infuraId: String
}

@Suppress("MagicNumber")
class ChainProperties {
    var callerAddress: String = "0x0000000000000000000000000000000000000000"
    var privateKey: String = ""
    var stockAddress: String = ""
    var orderBookAddress: String = ""
    var startBlockNumber: Long = 1
    var numOfConfirmations: Long = 10
    var maxBlocks: Long = 1000
}
