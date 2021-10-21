package com.ib.partial;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;

public interface EOrder {

    void nextValidId(int orderId);

    void orderStatus(int orderId, String status, double filled, double remaining,
                     double avgFillPrice, int permId, int parentId, double lastFillPrice,
                     int clientId, String whyHeld, double mktCapPrice);

    void openOrder(int orderId, Contract contract, Order order, OrderState orderState);

    void openOrderEnd();

    void orderBound(long orderId, int apiClientId, int apiOrderId);

    void completedOrder(Contract contract, Order order, OrderState orderState);

    void completedOrdersEnd();
}
