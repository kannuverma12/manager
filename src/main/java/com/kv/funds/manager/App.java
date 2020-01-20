package com.kv.funds.manager;

import com.kv.funds.manager.controller.FundsController;
import org.jooby.Jooby;
import org.jooby.Results;
import org.jooby.apitool.ApiTool;
import org.jooby.json.Jackson;

public class App extends Jooby {

    {
        use(new Jackson());

        use(FundsController.class);

        use(new ApiTool()
                .disableTryIt()
                .raml()
        );

        get("/", () -> Results.redirect("/raml"));
    }

    public static void main(final String[] args) {
        run(App::new, args);
    }
}
