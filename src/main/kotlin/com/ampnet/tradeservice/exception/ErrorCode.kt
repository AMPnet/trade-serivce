package com.ampnet.tradeservice.exception

enum class ErrorCode(val categoryCode: String, var specificCode: String, var message: String) {
    // Blockchain: 04
    BLOCKCHAIN_ID("04", "01", "Blockchain id not supported"),
    BLOCKCHAIN_CONFIG_MISSING("04", "02", "Blockchain data is not provided"),
    BLOCKCHAIN_TX_NOT_A_CONTRACT_CALL(
        "04", "03", "Transaction is not a contract function call"
    ),
    BLOCKCHAIN_TX_MISSING("04", "04", "Transaction is missing"),
    BLOCKCHAIN_JSON_RPC("08", "03", "Failed JSON-RPC call to blockchain"),
}
