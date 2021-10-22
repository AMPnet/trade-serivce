package com.ampnet.tradeservice.repository

import com.ampnet.tradeservice.generated.jooq.tables.Task
import com.ampnet.tradeservice.generated.jooq.tables.records.TaskRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class PollingTaskRepository(private val dslContext: DSLContext) {

    fun getTaskForChainId(chainId: Long): TaskRecord? =
        dslContext.selectFrom(Task.TASK)
            .where(Task.TASK.CHAIN_ID.eq(chainId))
            .fetchOne()

    fun updateTaskForChainId(chainId: Long, blockNumber: Long, timestamp: Long): Int =
        dslContext.update(Task.TASK)
            .set(Task.TASK.BLOCK_NUMBER, blockNumber)
            .set(Task.TASK.TIMESTAMP, timestamp)
            .where(Task.TASK.CHAIN_ID.eq(chainId))
            .execute()
}
