package com.kv.funds.manager.dto;

import com.kv.funds.manager.model.Account;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDTO {
    private final int             accountId;
    private final BigDecimal balance;

    public static AccountDTO fromAccount(Account account) {
        return new AccountDTO(account.id, account.balance.get());
    }
}
