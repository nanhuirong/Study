package com.huirong.java.guide;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by huirong on 17-4-6.
 */
public class DBinitializer implements Callable<DBinitializerFuture>{
    private TaxpayerBailoutDB db;
    private List<String>[] taxPayerList;
    private int recordsToCreate;

    public DBinitializer(TaxpayerBailoutDB db,
                         List<String>[] taxPayerList,
                         int recordsToCreate) {
        this.db = db;
        this.taxPayerList = taxPayerList;
        this.recordsToCreate = recordsToCreate;
    }

    @Override
    public DBinitializerFuture call() throws Exception {
        return BailoutMain1.populateDatabash(db, taxPayerList, recordsToCreate);
    }
}
