package com.ib.partial;

import com.ib.client.Bar;
import com.ib.client.CommissionReport;
import com.ib.client.Contract;
import com.ib.client.ContractDescription;
import com.ib.client.ContractDetails;
import com.ib.client.DeltaNeutralContract;
import com.ib.client.DepthMktDataDescription;
import com.ib.client.FamilyCode;
import com.ib.client.HistogramEntry;
import com.ib.client.HistoricalTick;
import com.ib.client.HistoricalTickBidAsk;
import com.ib.client.HistoricalTickLast;
import com.ib.client.NewsProvider;
import com.ib.client.PriceIncrement;
import com.ib.client.SoftDollarTier;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface EUngrouped {

    void updateMktDepth(int tickerId, int position, int operation, int side, double price, int size);

    void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation,
                          int side, double price, int size, boolean isSmartDepth);

    void updateNewsBulletin(int msgId, int msgType, String message, String origExchange);

    void receiveFA(int faDataType, String xml);

    void historicalData(int reqId, Bar bar);

    void scannerParameters(String xml);

    void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance,
                     String benchmark, String projection, String legsStr);

    void scannerDataEnd(int reqId);

    void realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume,
                     double wap, int count);

    void currentTime(long time);

    void fundamentalData(int reqId, String data);

    void deltaNeutralValidation(int reqId, DeltaNeutralContract deltaNeutralContract);

    void tickSnapshotEnd(int reqId);

    void marketDataType(int reqId, int marketDataType);

    void commissionReport(CommissionReport commissionReport);

    void position(String account, Contract contract, double pos, double avgCost);

    void positionEnd();

    void verifyMessageAPI(String apiData);

    void verifyCompleted(boolean isSuccessful, String errorText);

    void verifyAndAuthMessageAPI(String apiData, String xyzChallenge);

    void verifyAndAuthCompleted(boolean isSuccessful, String errorText);

    void displayGroupList(int reqId, String groups);

    void displayGroupUpdated(int reqId, String contractInfo);

    void positionMulti(int reqId, String account, String modelCode, Contract contract, double pos, double avgCost);

    void positionMultiEnd(int reqId);

    void securityDefinitionOptionalParameter(int reqId, String exchange, int underlyingConId, String tradingClass,
                                             String multiplier, Set<String> expirations, Set<Double> strikes);

    void securityDefinitionOptionalParameterEnd(int reqId);

    void softDollarTiers(int reqId, SoftDollarTier[] tiers);

    void familyCodes(FamilyCode[] familyCodes);

    void symbolSamples(int reqId, ContractDescription[] contractDescriptions);

    void historicalDataEnd(int reqId, String startDateStr, String endDateStr);

    void mktDepthExchanges(DepthMktDataDescription[] depthMktDataDescriptions);

    void smartComponents(int reqId, Map<Integer, Map.Entry<String, Character>> theMap);

    void newsProviders(NewsProvider[] newsProviders);

    void newsArticle(int requestId, int articleType, String articleText);

    void historicalNews(int requestId, String time, String providerCode, String articleId, String headline);

    void historicalNewsEnd(int requestId, boolean hasMore);

    void headTimestamp(int reqId, String headTimestamp);

    void histogramData(int reqId, List<HistogramEntry> items);

    void historicalDataUpdate(int reqId, Bar bar);

    void rerouteMktDataReq(int reqId, int conId, String exchange);

    void rerouteMktDepthReq(int reqId, int conId, String exchange);

    void marketRule(int marketRuleId, PriceIncrement[] priceIncrements);

    void pnl(int reqId, double dailyPnL, double unrealizedPnL, double realizedPnL);

    void pnlSingle(int reqId, int pos, double dailyPnL, double unrealizedPnL, double realizedPnL, double value);

    void historicalTicks(int reqId, List<HistoricalTick> ticks, boolean done);

    void historicalTicksBidAsk(int reqId, List<HistoricalTickBidAsk> ticks, boolean done);

    void historicalTicksLast(int reqId, List<HistoricalTickLast> ticks, boolean done);
}
