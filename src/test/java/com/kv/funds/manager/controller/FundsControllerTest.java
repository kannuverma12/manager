package com.kv.funds.manager.controller;

import com.kv.funds.manager.App;
import org.jooby.test.JoobyRule;
import org.junit.ClassRule;
import org.junit.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class FundsControllerTest {

    @ClassRule
    public static JoobyRule app           = new JoobyRule(new App());

    final private String accountEndpoint = "/funds/manager/account";
    final private String depositEndpoint = "/funds/manager/account/deposit/";
    final private String balanceEndpoint = "/funds/manager/account/balance/";
    final private String transferEndpoint = "/funds/manager/account/transfer/";

    @Test
    public void createAccount() {
        Integer currentId = post(accountEndpoint)
                .thenReturn()
                .as(Integer.class);

        post(accountEndpoint)
                .then()
                .assertThat()
                .body(equalTo(String.valueOf(currentId+1)))
                .statusCode(200);

        post(accountEndpoint)
                .then()
                .assertThat()
                .body(equalTo(String.valueOf(currentId + 2)))
                .statusCode(200);
    }

    @Test
    public void deleteAccount() {
        Integer generatedId = post(accountEndpoint)
                .thenReturn()
                .as(Integer.class);

        delete(accountEndpoint + "/" + generatedId)
                .then()
                .assertThat()
                .statusCode(204);
    }

    @Test
    public void whenDeleteNotExistingAccountGet404() {
        Integer generatedId = post(accountEndpoint)
                .thenReturn()
                .as(Integer.class);

        delete(accountEndpoint + "/" + generatedId + 1)
                .then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    public void addMoney() {
        Integer generatedId = post(accountEndpoint)
                .thenReturn()
                .as(Integer.class);

        patch(depositEndpoint + generatedId + "/" + BigDecimal.ONE)
                .then()
                .body("accountId", equalTo(generatedId))
                .body("balance", equalTo(1));

        patch(depositEndpoint + generatedId + "/" + BigDecimal.ONE)
                .then()
                .body("accountId", equalTo(generatedId))
                .body("balance", equalTo(2));
    }

    @Test
    public void whenAddMoneyToNonExistingAccountGet404() {
        Integer generatedId = post(accountEndpoint)
                .thenReturn()
                .as(Integer.class);

        patch(depositEndpoint + (generatedId+1) + "/" + BigDecimal.ONE)
                .then()
                .statusCode(404);
    }

    @Test
    public void getBalance() {
        Integer generatedId = post(accountEndpoint)
                .thenReturn()
                .as(Integer.class);
        patch(depositEndpoint + generatedId + "/" + BigDecimal.ONE);

        get(balanceEndpoint + generatedId)
                .then()
                .assertThat()
                .body(equalTo(String.valueOf(1)))
                .statusCode(200);
    }

    @Test
    public void whenGetBalanceFromNonExistingAccountGet404() {
        Integer generatedId = post(accountEndpoint)
                .thenReturn()
                .as(Integer.class);
        patch(depositEndpoint + generatedId + "/" + BigDecimal.ONE);

        get(balanceEndpoint + generatedId + 1)
                .then()
                .statusCode(404);
    }

    @Test
    public void transfer() {
        Integer generatedId1 = post(accountEndpoint)
                .thenReturn()
                .as(Integer.class);
        Integer generatedId2 = post(accountEndpoint)
                .thenReturn()
                .as(Integer.class);
        patch(depositEndpoint + generatedId1 + "/" + new BigDecimal(5));
        patch(depositEndpoint + generatedId2 + "/" + new BigDecimal(5));

        patch(transferEndpoint + generatedId1 + "/" + generatedId2 + "/2")
                .then()
                .body("[0].accountId", equalTo(generatedId1))
                .body("[0].balance", equalTo(3))
                .body("[1].accountId", equalTo(generatedId2))
                .body("[1].balance", equalTo(7));
    }

    @Test
    public void whenTransferFromNonExistingAccountGet404() {
        Integer generatedId1 = post(accountEndpoint)
                .thenReturn()
                .as(Integer.class);
        Integer generatedId2 = post(accountEndpoint)
                .thenReturn()
                .as(Integer.class);
        patch(depositEndpoint + generatedId1 + "/" + new BigDecimal(5));
        patch(depositEndpoint + generatedId2 + "/" + new BigDecimal(5));

        patch(transferEndpoint + (generatedId1+5) + "/" + generatedId2 + "/2")
                .then()
                .statusCode(404);
    }

    @Test
    public void whenTransferToNonExistingAccountGet404() {
        Integer generatedId1 = post(accountEndpoint)
                .thenReturn()
                .as(Integer.class);
        Integer generatedId2 = post(accountEndpoint)
                .thenReturn()
                .as(Integer.class);
        patch(depositEndpoint + generatedId1 + "/" + new BigDecimal(5));
        patch(depositEndpoint + generatedId2 + "/" + new BigDecimal(5));

        patch(transferEndpoint + generatedId1 + "/" + (generatedId2 + 6) + "/2")
                .then()
                .statusCode(404);
    }

    @Test
    public void whenTransferMoneyMoreThanBalanceGetError() {
        Integer generatedId1 = post(accountEndpoint)
                .thenReturn()
                .as(Integer.class);
        Integer generatedId2 = post(accountEndpoint)
                .thenReturn()
                .as(Integer.class);
        patch(depositEndpoint + generatedId2 + "/" + new BigDecimal(5));
        patch(transferEndpoint + generatedId1 + "/" + generatedId2 + "/10")
                .then()
                .statusCode(412);
    }

    @Test
    public void whenTransferMoneyToSelfAccountGet400() {
        Integer generatedId1 = post(accountEndpoint)
                .thenReturn()
                .as(Integer.class);
        patch(transferEndpoint + generatedId1 + "/" + generatedId1 + "/10")
                .then()
                .statusCode(400);
    }

    @Test
    public void redirectToRamlWhenOpening() {
        given()
                .redirects()
                .follow(false)
                .when()
                .get("/")
                .then()
                .statusCode(302);
    }
}
