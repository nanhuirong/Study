package com.huirong.java.concurrent.callable.travel;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;

/**
 * Created by huirong on 17-3-8.
 */
public class QuoteTask implements Callable<TravelQuote> {
    private final TravelCompany company;
    private final TravelInfo travelInfo;

    public QuoteTask(TravelCompany company, TravelInfo travelInfo) {
        this.company = company;
        this.travelInfo = travelInfo;
    }

    TravelQuote getFailedQuote(Throwable t){
        return null;
    }

    TravelQuote getTimeoutQuote(CancellationException e){
        return null;
    }

    @Override
    public TravelQuote call() throws Exception {
        return company.solicitQuote(travelInfo);
    }
}
