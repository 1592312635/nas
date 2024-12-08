package com.minyan.nascapi.service;

import com.minyan.nascommon.httpRequest.CurrencyConfirmRequest;
import com.minyan.nascommon.httpRequest.CurrencyDeductRequest;
import com.minyan.nascommon.httpRequest.CurrencySendRequest;

/**
 * @decription
 * @author minyan.he
 * @date 2024/12/1 11:00
 */
public interface HttpService {
  Boolean sendCurrency(CurrencySendRequest sendRequest);

  Boolean deductCurrency(CurrencyDeductRequest deductRequest);

  Boolean confirmCurrency(CurrencyConfirmRequest confirmRequest);
}
