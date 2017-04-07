package com.huirong.java.guide;

/**
 * Created by huirong on 17-4-6.
 */
public class DBinitializerFuture {
    private int recordsCreated;

    public DBinitializerFuture() {
    }
    public void addToRecordsCreated(int value){
        recordsCreated += value;
    }
    public int getRecordsCreated(){
        return recordsCreated;
    }
}
