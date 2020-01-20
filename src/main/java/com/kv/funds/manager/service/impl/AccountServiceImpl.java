package com.kv.funds.manager.service.impl;

import com.kv.funds.manager.dto.AccountDTO;
import com.kv.funds.manager.exception.InvalidTransferException;
import com.kv.funds.manager.exception.NotEnoughMoneyException;
import com.kv.funds.manager.model.Account;
import com.kv.funds.manager.repository.AccountRepository;
import com.kv.funds.manager.service.AccountService;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AccountServiceImpl implements AccountService {

    @Override
    public int createAccount() {
        return AccountRepository.createAccount();
    }

    @Override
    public Account deleteAccount(int id) {
        return AccountRepository.deleteAccount(id);
    }

    @Override
    public AccountDTO addMoney(int id, BigDecimal amount) {
        return AccountRepository.addMoney(id, amount);
    }

    @Override public AtomicReference<BigDecimal> getBalance(int id) {
        return AccountRepository.getBalance(id);
    }

    @Override public List<AccountDTO> transfer(int from, int to, BigDecimal amount)
            throws NotEnoughMoneyException, InvalidTransferException {
        return AccountRepository.transfer(from, to, amount);
    }
}
