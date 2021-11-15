package com.ampnet.tradeservice.service

import com.ampnet.tradeservice.service.ib.wrappers.LoggingAccountWrapper
import com.ampnet.tradeservice.service.ib.wrappers.LoggingContractWrapper
import com.ampnet.tradeservice.service.ib.wrappers.LoggingErrorAndConnectionWrapper
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
import java.util.concurrent.atomic.AtomicInteger

@Service
class InteractiveBrokersCorrelationService(
    accountWrapper: LoggingAccountWrapper,
    contractWrapper: LoggingContractWrapper,
    errorAndConnectionWrapper: LoggingErrorAndConnectionWrapper,
    orderWrapper: LoggingOrderWrapper,
    tickerWrapper: LoggingTickerWrapper,
    ungroupedWrapper: LoggingUngroupedWrapper
) : EAccount by accountWrapper,
    EContract by contractWrapper,
    EError by errorAndConnectionWrapper,
    EConnection by errorAndConnectionWrapper,
    EOrder by orderWrapper,
    ETicker by tickerWrapper,
    EUngrouped by ungroupedWrapper,
    EWrapper {
    private val requestId = AtomicInteger()

    fun nextRequestId(): Int = requestId.getAndIncrement()
}
