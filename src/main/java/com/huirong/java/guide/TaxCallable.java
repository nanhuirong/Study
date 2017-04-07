package com.huirong.java.guide;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Created by huirong on 17-4-3.
 */
public class TaxCallable implements Callable<BailoutFuture> {
    private static long runTimeInMillis = BailoutMain.TEST_TIME;
    private final static Random generator = BailoutMain.random.get();
    private long nullCounter, recordsRemoved, newRecordsAdded;
    private int index;
    private String taxPayerId;
    private final List<String> taxPayerList;
    private final TaxpayerBailoutDB db;

    public TaxCallable(List<String> taxPayerList, TaxpayerBailoutDB db) {
        this.taxPayerList = taxPayerList;
        this.db = db;
    }

    @Override
    public BailoutFuture call() throws Exception {
        long iterations = 0L, elapsedTime = 0L;
        long startTime = System.currentTimeMillis();
        double iterationsPerSeconds = 0;
        do {
            setTaxPayer();
            iterations++;
            TaxPayerRecord tpr = null;
            //处理iterations溢出
            if (iterations == Integer.MAX_VALUE){
                long elapsed = System.currentTimeMillis() - startTime;
                iterationsPerSeconds = iterations / (double)(elapsed / 1000);
                System.err.println("Iterations counter about to overflow");
                System.err.println("Calculating current operations per second...");
                System.err.println("Iterations per Second: " + iterationsPerSeconds);
                runTimeInMillis -= elapsedTime;
            }
            if (iterations % 1001 == 0){
                tpr = addNewTaxPayer(tpr);
            }else if (iterations % 60195 == 0){
                tpr = removeTaxPayer(tpr);
            }else {
                tpr = updateTaxPayer(iterations, tpr);
            }
            if (iterations % 1000 == 0){
                elapsedTime = System.currentTimeMillis() - startTime;
            }
        }while (elapsedTime < runTimeInMillis);
        if (iterations >= 1000){
            iterationsPerSeconds = iterations / (double)(elapsedTime / 1000);
        }
        BailoutFuture bailoutFuture = new BailoutFuture(iterationsPerSeconds,
                newRecordsAdded, recordsRemoved, nullCounter);
        return bailoutFuture;
    }

    private TaxPayerRecord removeTaxPayer(TaxPayerRecord record){
        record = db.remove(taxPayerId);
        if (record != null){
            taxPayerList.remove(index);
            recordsRemoved++;
        }
        return record;
    }

    private TaxPayerRecord updateTaxPayer(long iterations, TaxPayerRecord record){
        if (iterations % 1001 == 0){
            record = db.get(taxPayerId);
        }else {
            record = db.get(taxPayerId);
            if (record != null){
                long tax = generator.nextInt(10) + 15;
                record.taxPaid(tax);
            }
        }
        if (record == null){
            nullCounter++;
        }
        return record;
    }

    private TaxPayerRecord addNewTaxPayer(TaxPayerRecord record){
        String tmpTaxPayerId = BailoutMain.getRandompayerId();
        record = BailoutMain.makeTaxPayerRecord();
        TaxPayerRecord old = db.add(tmpTaxPayerId, record);
        if (old == null){
            taxPayerList.add(tmpTaxPayerId);
            newRecordsAdded++;
        }
        return record;
    }

    public void setTaxPayer(){
        if (++index >= taxPayerList.size()){
            index = 0;
        }
        this.taxPayerId = taxPayerList.get(index);
    }
}
