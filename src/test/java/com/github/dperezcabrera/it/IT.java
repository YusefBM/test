package com.github.dperezcabrera.it;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class IT {

    private static AgentManager manager = AgentManager.create();

    @BeforeAll
    static void setup() {
        manager.deployConfigServer()
                .deployPostgres()
                .deployTestWeb();
    }

    @Test
    public void testChrome() throws Exception {
        manager.newChromeAgent()
                .goToWebTest()
                .checkTitleContains("App Test");
    }
}
