package com.ampnet.tradeservice.repository

import com.ampnet.tradeservice.generated.jooq.enums.TaskStatus
import com.ampnet.tradeservice.generated.jooq.tables.BlockchainTask
import com.ampnet.tradeservice.generated.jooq.tables.records.BlockchainTaskRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class BlockchainTaskRepository(private val dslContext: DSLContext) {

    fun getChainIdAndStatus(chainId: Long, status: TaskStatus): List<BlockchainTaskRecord> =
        dslContext.selectFrom(BlockchainTask.BLOCKCHAIN_TASK)
            .where(BlockchainTask.BLOCKCHAIN_TASK.CHAIN_ID.eq(chainId))
            .and(BlockchainTask.BLOCKCHAIN_TASK.STATUS.eq(status))
            .fetch()
}
