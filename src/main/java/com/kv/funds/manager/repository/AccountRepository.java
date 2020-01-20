package com.kv.funds.manager.repository;

import com.google.common.annotations.VisibleForTesting;
import com.kv.funds.manager.dto.AccountDTO;
import com.kv.funds.manager.exception.InvalidTransferException;
import com.kv.funds.manager.exception.NotEnoughMoneyException;
import com.kv.funds.manager.model.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.google.common.collect.Lists.newArrayList;

public class AccountRepository {

    private static AtomicInteger         userCounter = new AtomicInteger(0);
    private static Map<Integer, Account> users       = new ConcurrentHashMap<>();

    private static ReadWriteLock lock = new ReentrantReadWriteLock();

    private static ReadWriteLock getLock() {
        return lock;
    }

    public static int createAccount() {
        int id = userCounter.incrementAndGet();
        users.put(id, new Account(id, new AtomicReference<>(BigDecimal.ZERO)));
        return id;
    }

    public static Account deleteAccount(int id) {
        return users.remove(id);
    }

    public static AccountDTO addMoney(int id, BigDecimal amount) {
        Account account = users.get(id);
        if (account == null)
            return null;
        account.balance.updateAndGet(current -> current.add(amount));
        return AccountDTO.fromAccount(account);
    }

    public static AtomicReference<BigDecimal> getBalance(int id) {
        Account account = users.get(id);
        if (account == null)
            return null;
        return account.getBalance();
    }

    public static List<AccountDTO> transfer(Integer from, Integer to, BigDecimal amount)
            throws NotEnoughMoneyException, InvalidTransferException {
        if (from == null || to == null) {
            return null;
        }
        if (Objects.equals(from, to)) {
            throw new InvalidTransferException();
        }
        Account accountFrom = users.get(from);
        Account accountTo = users.get(to);
        if (accountFrom == null || accountTo == null)
            return null;

        getLock().writeLock().lock();
        try {
            if (accountFrom.balance.get().compareTo(amount) < 0) {
                throw new NotEnoughMoneyException("User with id " + from
                        + " has not enough money to transfer to user with id " + to);
            }

            accountFrom.balance.updateAndGet(current -> current.subtract(amount));
            accountTo.balance.updateAndGet(current -> current.add(amount));
            return newArrayList(AccountDTO.fromAccount(accountFrom),
                    AccountDTO.fromAccount(accountTo));
        } finally {
            getLock().writeLock().unlock();
        }
    }

    @VisibleForTesting
    public static Map<Integer, Account> getUsers() {
        return users;
    }
}
