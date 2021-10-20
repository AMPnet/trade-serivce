package com.ampnet.tradeservice.repository

import com.ampnet.tradeservice.generated.jooq.tables.Dummy
import com.ampnet.tradeservice.generated.jooq.tables.records.DummyRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class DummyRepository(private val dslContext: DSLContext) {

    fun getById(id: Int): DummyRecord? {
        return dslContext.fetchOne(Dummy.DUMMY, Dummy.DUMMY.DUMMY_COLUMN.eq(id))
        // alternative way to fetch one record using SQL DSL:
        // return dslContext.selectFrom(Dummy.DUMMY)
        //     .where(Dummy.DUMMY.DUMMY_COLUMN.eq(id))
        //     .fetchOne()
    }
}
