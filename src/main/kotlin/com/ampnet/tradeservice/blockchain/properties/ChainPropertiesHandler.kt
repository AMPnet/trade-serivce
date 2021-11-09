package com.ampnet.tradeservice.blockchain.properties

import com.ampnet.tradeservice.configuration.ApplicationProperties
import com.ampnet.tradeservice.configuration.ChainProperties
import com.ampnet.tradeservice.exception.ErrorCode
import com.ampnet.tradeservice.exception.InternalException
import com.ampnet.tradeservice.exception.InvalidRequestException
import org.springframework.stereotype.Service
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService

@Service
class ChainPropertiesHandler(private val applicationProperties: ApplicationProperties) {

    private val blockchainPropertiesMap = mutableMapOf<Long, ChainPropertiesWithServices>()

    @Throws(InvalidRequestException::class)
    fun getBlockchainProperties(chainId: Long): ChainPropertiesWithServices {
        blockchainPropertiesMap[chainId]?.let { return it }
        val chain = getChain(chainId)
        val properties = generateBlockchainProperties(chain)
        blockchainPropertiesMap[chainId] = properties
        return properties
    }

    fun getChainProperties(chain: Chain): ChainProperties? {
        val chainProperties = when (chain) {
            Chain.MATIC_MAIN -> applicationProperties.chainMatic
            Chain.MATIC_TESTNET_MUMBAI -> applicationProperties.chainMumbai
            Chain.ETHEREUM_MAIN -> applicationProperties.chainEthereum
            Chain.GOERLI_TESTNET -> applicationProperties.chainGoerli
            Chain.HARDHAT_TESTNET -> applicationProperties.chainHardhatTestnet
        }
        return if (chainProperties.callerAddress.isBlank() || chainProperties.orderBookAddress.isBlank()) {
            null
        } else {
            chainProperties
        }
    }

    fun getGasPriceFeed(chainId: Long): String? = getChain(chainId).priceFeed

    private fun generateBlockchainProperties(chain: Chain): ChainPropertiesWithServices {
        val chainProperties = getChainProperties(chain) ?: throw InternalException(
            ErrorCode.BLOCKCHAIN_CONFIG_MISSING,
            "Config for chain: ${chain.name} not defined in the application properties"
        )
        val web3j = Web3j.build(HttpService(getChainRpcUrl(chain)))
        return ChainPropertiesWithServices(
            Credentials.create(chainProperties.privateKey),
            web3j,
            chainProperties
        )
    }

    private fun getChain(chainId: Long) = Chain.fromId(chainId)
        ?: throw InternalException(ErrorCode.BLOCKCHAIN_ID, "Blockchain id: $chainId not supported")

    internal fun getChainRpcUrl(chain: Chain): String =
        if (chain.infura == null || applicationProperties.infuraId.isBlank()) {
            chain.rpcUrl
        } else {
            "${chain.infura}${applicationProperties.infuraId}"
        }
}
