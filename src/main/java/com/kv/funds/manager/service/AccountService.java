package com.kv.funds.manager.service;

import com.kv.funds.manager.dto.AccountDTO;
import com.kv.funds.manager.exception.InvalidTransferException;
import com.kv.funds.manager.exception.NotEnoughMoneyException;
import com.kv.funds.manager.model.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public interface AccountService {
    int createAccount();

    Account deleteAccount(int id);

    AccountDTO addMoney(int id, BigDecimal amount);

    AtomicReference<BigDecimal> getBalance(int id);

    List<AccountDTO> transfer(int from, int to, BigDecimal amount) throws NotEnoughMoneyException,
            InvalidTransferException;
}
