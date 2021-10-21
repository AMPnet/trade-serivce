package com.ib.partial;

import com.ib.client.Contract;

public interface EAccount {

    void updateAccountValue(String key, String value, String currency, String accountName);

    void updatePortfolio(Contract contract, double position, double marketPrice, double marketValue,
                         double averageCost, double unrealizedPNL, double realizedPNL, String accountName);

    void updateAccountTime(String timeStamp);

    void accountDownloadEnd(String accountName);

    void accountSummary(int reqId, String account, String tag, String value, String currency);

    void accountSummaryEnd(int reqId);

    void accountUpdateMulti(int reqId, String account, String modelCode, String key, String value, String currency);

    void accountUpdateMultiEnd(int reqId);

    void managedAccounts(String accountsList);
}
