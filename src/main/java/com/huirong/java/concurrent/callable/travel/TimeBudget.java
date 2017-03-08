package com.huirong.java.concurrent.callable.travel;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by huirong on 17-3-8.
 */
public class TimeBudget {
    private static ExecutorService executor = Executors.newCachedThreadPool();


    public List<TravelQuote> getRankedTravelQuotes(TravelInfo info, Set<TravelCompany> companies
            , Comparator<TravelQuote> ranking, long time, TimeUnit unit)throws InterruptedException{
        List<QuoteTask> tasks = new ArrayList<>();
        for (TravelCompany company : companies){
            tasks.add(new QuoteTask(company, info));
        }
        List<Future<TravelQuote>> futures = executor.invokeAll(tasks, time, unit);
        List<TravelQuote> quotes = new ArrayList<>(tasks.size());
        Iterator<QuoteTask> iterator = tasks.iterator();
        for (Future<TravelQuote> future : futures){
            QuoteTask task = iterator.next();
            try {
                quotes.add(future.get());
            }catch (ExecutionException e){
                quotes.add(task.getFailedQuote(e.getCause()));
            }catch (CancellationException e){
                quotes.add(task.getTimeoutQuote(e));
            }
        }
        Collections.sort(quotes, ranking);
        return quotes;
    }
}
