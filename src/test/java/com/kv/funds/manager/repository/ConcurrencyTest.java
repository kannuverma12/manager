package com.kv.funds.manager.repository;

import com.kv.funds.manager.exception.InvalidTransferException;
import com.kv.funds.manager.exception.NotEnoughMoneyException;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class ConcurrencyTest {

    /**
     * Create concurrent transfers for checking if deadlock can be created.
     * If deadlock occurs, this test case will fail
     */
    @Test(timeout = 100000L)
    public void callConcurrentlyToCreateDeadlock() {
        int[] ids = new int[] {AccountRepository.createAccount()
                , AccountRepository.createAccount()
                , AccountRepository.createAccount()};
        for (int id : ids) {
            AccountRepository.addMoney(id, new BigDecimal("10000000"));
        }

        LongAdder counter = new LongAdder();
        List<Runnable> transfers = new ArrayList<>(12000000);
        for (int i = 0; i < 2000000; i++) {
            transfers.add(transactionRequest(ids[0], ids[1]));
            transfers.add(transactionRequest(ids[1], ids[2]));
            transfers.add(transactionRequest(ids[2], ids[0]));
            transfers.add(transactionRequest(ids[0], ids[2]));
            transfers.add(transactionRequest(ids[2], ids[1]));
            transfers.add(transactionRequest(ids[1], ids[0]));
        }
        transfers
                .parallelStream()
                .map((Function<Runnable, Void>) runnable -> {
                    runnable.run();
                    return null;
                })
                .forEach(x -> counter.increment());
        // check we have really called transfer 12 million times
        assertEquals(12000000L, counter.sum());
        // check we haven't losed any money
        assertEquals(0,
                AccountRepository.getUsers()
                        .values()
                        .stream()
                        .map(it -> it.balance.get())
                        .reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO).compareTo(new BigDecimal("30000000")));
    }

    private Runnable transactionRequest(int id1, int id2) {
        return () -> {
            try {
                AccountRepository.transfer(id1, id2, BigDecimal.ONE);
            } catch (NotEnoughMoneyException | InvalidTransferException ignored) {
            }
        };
    }
}
