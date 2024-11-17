package com.recorddb.server.comands;

import java.util.concurrent.Callable;

public class InvalidCommand implements Callable<String> {
    @Override
    public String call() throws Exception {
        return "INVALID_COMMAND";
    }
}
