package com.ib.partial;

import com.ib.client.TickAttrib;
import com.ib.client.TickAttribBidAsk;
import com.ib.client.TickAttribLast;

public interface ETicker {

    void tickPrice(int tickerId, int field, double price, TickAttrib attrib);

    void tickSize(int tickerId, int field, int size);

    void tickOptionComputation(int tickerId, int field, double impliedVol,
                               double delta, double optPrice, double pvDividend,
                               double gamma, double vega, double theta, double undPrice);

    void tickGeneric(int tickerId, int tickType, double value);

    void tickString(int tickerId, int tickType, String value);

    void tickEFP(int tickerId, int tickType, double basisPoints,
                 String formattedBasisPoints, double impliedFuture, int holdDays,
                 String futureLastTradeDate, double dividendImpact, double dividendsToLastTradeDate);

    void tickByTickAllLast(int reqId, int tickType, long time, double price, int size, TickAttribLast tickAttribLast,
                           String exchange, String specialConditions);

    void tickByTickBidAsk(int reqId, long time, double bidPrice, double askPrice, int bidSize, int askSize,
                          TickAttribBidAsk tickAttribBidAsk);

    void tickByTickMidPoint(int reqId, long time, double midPoint);

    void tickNews(int tickerId, long timeStamp, String providerCode, String articleId, String headline,
                  String extraData);

    void tickReqParams(int tickerId, double minTick, String bboExchange, int snapshotPermissions);
}

