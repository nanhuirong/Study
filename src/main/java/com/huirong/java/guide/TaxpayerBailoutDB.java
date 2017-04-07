package com.huirong.java.guide;

/**
 * Created by huirong on 17-4-3.
 */
public interface TaxpayerBailoutDB {
    static final int NUMBER_OF_RECORDS_DESIRED = 2 * 1000000;

    //根据tax payer的id从数据库中读取记录
    TaxPayerRecord get(String id);

    //往数据库添加新的tax payer
    TaxPayerRecord add(String id, TaxPayerRecord record);

    //从数据删除一条记录
    TaxPayerRecord remove(String id);

    //返回数据库大小
    int size();
}
