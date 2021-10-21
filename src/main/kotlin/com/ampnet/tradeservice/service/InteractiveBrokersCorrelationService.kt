package com.ampnet.tradeservice.service

import com.ampnet.tradeservice.service.ib.wrappers.LoggingAccountWrapper
import com.ampnet.tradeservice.service.ib.wrappers.LoggingConnectionWrapper
import com.ampnet.tradeservice.service.ib.wrappers.LoggingContractWrapper
import com.ampnet.tradeservice.service.ib.wrappers.LoggingErrorWrapper
import com.ampnet.tradeservice.service.ib.wrappers.LoggingOrderWrapper
import com.ampnet.tradeservice.service.ib.wrappers.LoggingTickerWrapper
import com.ampnet.tradeservice.service.ib.wrappers.LoggingUngroupedWrapper
import com.ib.client.EWrapper
import com.ib.partial.EAccount
import com.ib.partial.EConnection
import com.ib.partial.EContract
import com.ib.partial.EError
import com.ib.partial.EOrder
import com.ib.partial.ETicker
import com.ib.partial.EUngrouped
import org.springframework.stereotype.Service

@Service
class InteractiveBrokersCorrelationService(
    accountWrapper: LoggingAccountWrapper,
    connectionWrapper: LoggingConnectionWrapper,
    contractWrapper: LoggingContractWrapper,
    errorWrapper: LoggingErrorWrapper,
    orderWrapper: LoggingOrderWrapper,
    tickerWrapper: LoggingTickerWrapper,
    ungroupedWrapper: LoggingUngroupedWrapper
) : EAccount by accountWrapper,
    EConnection by connectionWrapper,
    EContract by contractWrapper,
    EError by errorWrapper,
    EOrder by orderWrapper,
    ETicker by tickerWrapper,
    EUngrouped by ungroupedWrapper,
    EWrapper
