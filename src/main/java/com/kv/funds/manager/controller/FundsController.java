package com.kv.funds.manager.controller;

import com.google.inject.Inject;
import com.kv.funds.manager.dto.AccountDTO;
import com.kv.funds.manager.exception.InvalidTransferException;
import com.kv.funds.manager.exception.NotEnoughMoneyException;
import com.kv.funds.manager.model.Account;
import com.kv.funds.manager.service.impl.AccountServiceImpl;
import org.jooby.Err;
import org.jooby.Status;
import org.jooby.mvc.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Path("/funds/manager/account")
public class FundsController {

    private AccountServiceImpl accountService;

    @Inject
    public FundsController(AccountServiceImpl accountService) {
        this.accountService = accountService;
    }

    @POST
    public Integer createAccount() {
        return accountService.createAccount();
    }

    @DELETE
    @Path("/:id")
    public void deleteAccount(Integer id) {
        Account userInfo = accountService.deleteAccount(id);
        if (userInfo == null)
            throw new Err(Status.NOT_FOUND, "No user with id " + id + " found");
    }

    @PATCH
    @Path("/deposit/:id/:balance")
    public AccountDTO addMoney(int id, BigDecimal balance) {
        AccountDTO accountDTO = accountService.addMoney(id, balance);
        if (accountDTO == null)
            throw new Err(404, "Account " + id + " not found");
        return accountDTO;
    }

    @GET
    @Path("/balance/:id")
    public AtomicReference<BigDecimal> getBalance(int id) {
        AtomicReference<BigDecimal> balance = accountService.getBalance(id);
        if (balance == null)
            throw new Err(404, "Account " + id + " not found");
        return balance;
    }

    @PATCH
    @Path("transfer/:from/:to/:balance")
    public List<AccountDTO> transfer(int from, int to, BigDecimal balance) {
        try {
            List<AccountDTO> transfer = accountService.transfer(from, to, balance);
            if (transfer == null)
                throw new Err(404, "One of accounts not found");
            return transfer;
        } catch (NotEnoughMoneyException e) {
            throw new Err(Status.PRECONDITION_FAILED, "User with id " + from + " low balance", e);
        } catch (InvalidTransferException e) {
            throw new Err(Status.BAD_REQUEST, "Transfer from and to account can't be same");
        }
    }
}
