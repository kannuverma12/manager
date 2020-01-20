package com.kv.funds.manager.service.impl;

import com.kv.funds.manager.dto.AccountDTO;
import com.kv.funds.manager.exception.InvalidTransferException;
import com.kv.funds.manager.exception.NotEnoughMoneyException;
import com.kv.funds.manager.model.Account;
import com.kv.funds.manager.repository.AccountRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceImplTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    public void createAccount() {
        int generatedId = AccountRepository.createAccount();
        int generatedId2 = accountService.createAccount();
        assertEquals(Long.valueOf(generatedId2), Long.valueOf(generatedId + 1));
    }

    @Test
    public void deleteAccount() {
        int generatedId = AccountRepository.createAccount();
        Account account = accountService.deleteAccount(generatedId);
        assertEquals(Long.valueOf(generatedId), Long.valueOf(account.getId()));
        Account accountAfterDeletion = AccountRepository.getUsers().get(generatedId);
        assertNull(accountAfterDeletion);
    }

    @Test
    public void whenDeleteNonExisitingAccountGetNull() {
        int generatedId = AccountRepository.createAccount();
        Account account = accountService.deleteAccount(generatedId + 1);
        assertNull(account);
    }

    @Test
    public void addMoney() {
        int generatedId = AccountRepository.createAccount();
        AccountDTO accountDTO = accountService.addMoney(generatedId, BigDecimal.ONE);
        assertEquals(Long.valueOf(String.valueOf(accountDTO.getBalance())),
                Long.valueOf(String.valueOf(BigDecimal.ONE)));
    }

    @Test
    public void getBalance() {
        int generatedId = AccountRepository.createAccount();
        accountService.addMoney(generatedId, BigDecimal.ONE);
        AccountDTO accountDTO2 = accountService.addMoney(generatedId, BigDecimal.ONE);
        assertEquals(Long.valueOf(String.valueOf(accountDTO2.getBalance())),
                Long.valueOf(String.valueOf(new BigDecimal(2))));
    }

    @Test
    public void transfer() throws Exception {
        int generatedId1 = AccountRepository.createAccount();
        int generatedId2 = AccountRepository.createAccount();

        AccountRepository.addMoney(generatedId1, new BigDecimal(5));
        AccountRepository.addMoney(generatedId2, new BigDecimal(5));

        List<AccountDTO> accountDTOList = accountService.transfer(generatedId1,
                generatedId2, new BigDecimal(3));

        assertEquals(Long.valueOf(
                String.valueOf(accountDTOList.get(0).getBalance().add(new BigDecimal(6)))),
                Long.valueOf(String.valueOf(accountDTOList.get(1).getBalance())));
    }

    @Test
    public void whenTransferFromInvalidAccountGetNull() throws Exception {
        int generatedId1 = AccountRepository.createAccount();
        int generatedId2 = AccountRepository.createAccount();

        AccountRepository.addMoney(generatedId1, new BigDecimal(5));
        AccountRepository.addMoney(generatedId2, new BigDecimal(5));

        List<AccountDTO> accountDTOList = accountService.transfer(generatedId1 + 3,
                generatedId2, new BigDecimal(3));

        assertNull(accountDTOList);
    }

    @Test
    public void whenTransferToInvalidAccountGetNull() throws Exception {
        int generatedId1 = AccountRepository.createAccount();
        int generatedId2 = AccountRepository.createAccount();

        AccountRepository.addMoney(generatedId1, new BigDecimal(5));
        AccountRepository.addMoney(generatedId2, new BigDecimal(5));

        List<AccountDTO> accountDTOList = accountService.transfer(generatedId1 + 3,
                generatedId2 + 3, new BigDecimal(3));

        assertNull(accountDTOList);
    }

    @Test(expected = InvalidTransferException.class)
    public void whenTransferToSelfThrowsInvalidTransferException() throws Exception {
        int generatedId1 = AccountRepository.createAccount();
        AccountRepository.addMoney(generatedId1, new BigDecimal(5));
        accountService.transfer(generatedId1,
                generatedId1, new BigDecimal(3));

    }

    @Test(expected = NotEnoughMoneyException.class)
    public void whenTransferMoreMoneyThanBalanceThrowNotEnoughMoneyException() throws Exception {
        int generatedId1 = AccountRepository.createAccount();
        int generatedId2 = AccountRepository.createAccount();

        AccountRepository.addMoney(generatedId1, new BigDecimal(5));

        accountService.transfer(generatedId1,
                generatedId2, new BigDecimal(10));
    }
}
