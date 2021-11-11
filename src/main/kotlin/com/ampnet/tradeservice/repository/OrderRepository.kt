package com.ampnet.tradeservice.repository

import com.ampnet.tradeservice.generated.jooq.enums.OrderStatus
import com.ampnet.tradeservice.generated.jooq.enums.OrderType
import com.ampnet.tradeservice.generated.jooq.tables.Order
import com.ampnet.tradeservice.generated.jooq.tables.records.OrderRecord
import com.ampnet.tradeservice.model.BlockchainOrderId
import com.ampnet.tradeservice.model.InteractiveBrokersOrderId
import com.ampnet.tradeservice.model.PlacedBuyOrder
import com.ampnet.tradeservice.model.PlacedOrder
import com.ampnet.tradeservice.model.PlacedSellOrder
import com.ampnet.tradeservice.model.SerialId
import org.jooq.DSLContext
import org.jooq.TableField
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.Duration
import java.time.OffsetDateTime

@Repository
@Suppress("TooManyFunctions")
class OrderRepository(private val dslContext: DSLContext) {

    fun storeBuyOrder(order: PlacedBuyOrder): SerialId<PlacedBuyOrder> {
        return dslContext.insertInto(Order.ORDER)
            .set(order.toRecord())
            .returning()
            .fetchOne()
            ?.toOrderObject()
            ?: throw IllegalStateException("Could not insert order into database: $order")
    }

    fun storeSellOrder(order: PlacedSellOrder): SerialId<PlacedSellOrder> {
        return dslContext.insertInto(Order.ORDER)
            .set(order.toRecord())
            .returning()
            .fetchOne()
            ?.toOrderObject()
            ?: throw IllegalStateException("Could not insert order into database: $order")
    }

    fun <O : PlacedOrder> markAsPending(order: SerialId<O>): SerialId<O> {
        return updateOrderStatus(
            order = order,
            fromStatus = OrderStatus.PREPARED,
            toStatus = OrderStatus.PENDING,
            timestampField = Order.ORDER.SUBMITTED_AT
        )
    }

    fun <O : PlacedOrder> markAsSuccessful(order: SerialId<O>): SerialId<O> {
        return updateOrderStatus(
            order = order,
            fromStatus = OrderStatus.PENDING,
            toStatus = OrderStatus.SUCCESSFUL,
            timestampField = Order.ORDER.COMPLETED_AT
        )
    }

    fun <O : PlacedOrder> markAsFailed(order: SerialId<O>): SerialId<O> {
        return updateOrderStatus(
            order = order,
            fromStatus = OrderStatus.PENDING,
            toStatus = OrderStatus.FAILED,
            timestampField = Order.ORDER.COMPLETED_AT
        )
    }

    fun getPreparedBuyOrders(minAge: Duration): List<SerialId<PlacedBuyOrder>> {
        return getOrders(OrderType.BUY, OrderStatus.PREPARED, minAge, Order.ORDER.CREATED_AT)
    }

    fun getPreparedSellOrders(minAge: Duration): List<SerialId<PlacedSellOrder>> {
        return getOrders(OrderType.SELL, OrderStatus.PREPARED, minAge, Order.ORDER.CREATED_AT)
    }

    fun getPendingBuyOrders(minAge: Duration): List<SerialId<PlacedBuyOrder>> {
        return getOrders(OrderType.BUY, OrderStatus.PENDING, minAge, Order.ORDER.SUBMITTED_AT)
    }

    fun getPendingSellOrders(minAge: Duration): List<SerialId<PlacedSellOrder>> {
        return getOrders(OrderType.SELL, OrderStatus.PENDING, minAge, Order.ORDER.SUBMITTED_AT)
    }

    fun deleteOrder(id: Int) {
        dslContext.deleteFrom(Order.ORDER)
            .where(Order.ORDER.ID.eq(id))
            .execute()
    }

    private fun <O : PlacedOrder> updateOrderStatus(
        order: SerialId<O>,
        fromStatus: OrderStatus,
        toStatus: OrderStatus,
        timestampField: TableField<OrderRecord, OffsetDateTime?>
    ): SerialId<O> {
        return dslContext.update(Order.ORDER)
            .set(Order.ORDER.ORDER_STATUS, toStatus)
            .set(timestampField, OffsetDateTime.now())
            .where(
                Order.ORDER.ID.eq(order.serialId)
                    .and(Order.ORDER.ORDER_TYPE.eq(order.orderType()))
                    .and(Order.ORDER.ORDER_STATUS.eq(fromStatus))
            )
            .returning()
            .fetchOne()
            ?.toOrderObject()
            ?: order
    }

    private fun <O : PlacedOrder> getOrders(
        orderType: OrderType,
        orderStatus: OrderStatus,
        minAge: Duration,
        timestampField: TableField<OrderRecord, OffsetDateTime?>
    ): List<SerialId<O>> {
        return dslContext.selectFrom(Order.ORDER)
            .where(
                Order.ORDER.ORDER_TYPE.eq(orderType)
                    .and(Order.ORDER.ORDER_STATUS.eq(orderStatus))
                    .and(timestampField.le(OffsetDateTime.now().minus(minAge)))
            )
            .fetch { it.toOrderObject() }
    }

    private fun SerialId<out PlacedOrder>.orderType(): OrderType {
        return when (this.order) {
            is PlacedBuyOrder -> OrderType.BUY
            is PlacedSellOrder -> OrderType.SELL
        }
    }

    private fun PlacedOrder.toRecord(): OrderRecord {
        val order = this
        val record = OrderRecord().apply {
            interactiveBrokersOrderId = order.interactiveBrokersOrderId.value
            blockchainOrderId = order.blockchainOrderId.value.toLong()
            chainId = order.chainId
            wallet = order.wallet
            stockId = order.stockId
            orderStatus = OrderStatus.PREPARED
            createdAt = OffsetDateTime.now()
        }

        when (order) {
            is PlacedBuyOrder -> {
                record.amountUsd = order.amountUsd
                record.minOrMaxPrice = order.maxPrice
                record.numShares = order.numShares
                record.orderType = OrderType.BUY
            }

            is PlacedSellOrder -> {
                record.amountUsd = BigDecimal.ZERO
                record.minOrMaxPrice = order.minPrice
                record.numShares = order.numShares
                record.orderType = OrderType.SELL
            }
        }

        return record
    }

    private fun <O : PlacedOrder> OrderRecord.toOrderObject(): SerialId<O> {
        val order = when (this.orderType!!) {
            OrderType.BUY -> PlacedBuyOrder(
                interactiveBrokersOrderId = InteractiveBrokersOrderId(this.interactiveBrokersOrderId!!),
                blockchainOrderId = BlockchainOrderId(this.blockchainOrderId!!.toBigInteger()),
                chainId = this.chainId!!,
                wallet = this.wallet!!,
                stockId = this.stockId!!,
                amountUsd = this.amountUsd!!,
                maxPrice = this.minOrMaxPrice!!,
                numShares = this.numShares!!
            )
            OrderType.SELL -> PlacedSellOrder(
                interactiveBrokersOrderId = InteractiveBrokersOrderId(this.interactiveBrokersOrderId!!),
                blockchainOrderId = BlockchainOrderId(this.blockchainOrderId!!.toBigInteger()),
                chainId = this.chainId!!,
                wallet = this.wallet!!,
                stockId = this.stockId!!,
                minPrice = this.minOrMaxPrice!!,
                numShares = this.numShares!!
            )
        }

        @Suppress("UNCHECKED_CAST")
        return SerialId(
            this.id!!,
            this.orderStatus!!,
            order as O
        )
    }
}
