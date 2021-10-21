package com.ib.partial;

import com.ib.client.ContractDetails;

public interface EContract {

    void contractDetails(int reqId, ContractDetails contractDetails);

    void bondContractDetails(int reqId, ContractDetails contractDetails);

    void contractDetailsEnd(int reqId);
}
