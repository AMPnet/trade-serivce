package com.ampnet.tradeservice.blockchain.properties

import com.ampnet.tradeservice.configuration.ChainProperties
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j

data class ChainPropertiesWithServices(
    val credentials: Credentials,
    val web3j: Web3j,
    val chain: ChainProperties
)
