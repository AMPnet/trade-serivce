/* Copyright (C) 2019 Interactive Brokers LLC. All rights reserved. This code is subject to the terms
 * and conditions of the IB API Non-Commercial License or the IB API Commercial License, as applicable. */

package com.ib.client;

import com.ib.partial.EAccount;
import com.ib.partial.EConnection;
import com.ib.partial.EContract;
import com.ib.partial.EError;
import com.ib.partial.EOrder;
import com.ib.partial.ETicker;
import com.ib.partial.EUngrouped;

public interface EWrapper extends EAccount, EConnection, EContract, EError, EOrder, ETicker, EUngrouped {
}

