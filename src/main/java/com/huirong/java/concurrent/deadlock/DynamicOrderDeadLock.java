package com.huirong.java.concurrent.deadlock;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by huirong on 17-3-9.
 *
 */
public class DynamicOrderDeadLock {
    private static final Object tieLock = new Object();

    public static void transferMoney(final Account src,
                                     final Account dest,
                                     final DollarAmount amount)
            throws InsufficientFundsException{
        class Helper{
            public void transfer()throws InsufficientFundsException{
                if (src.getBalance().compareTo(amount) < 0){
                    throw new InsufficientFundsException();
                }else {
                    src.debit(amount);
                    dest.credit(amount);
                }
            }
        }
        int srcHash = System.identityHashCode(src);
        int destHash = System.identityHashCode(dest);
        if (srcHash < destHash){
            synchronized (src){
                synchronized (dest){
                    new Helper().transfer();
                }
            }
        }else if (srcHash > destHash){
            synchronized (dest){
                synchronized (src){
                    new Helper().transfer();
                }
            }
        }else {
            synchronized (tieLock){
                synchronized (src){
                    synchronized (dest){
                        new Helper().transfer();
                    }
                }
            }
        }
    }

    interface DollarAmount extends Comparable<DollarAmount>{

    }

    interface Account{
        void debit(DollarAmount amount);
        void credit(DollarAmount amount);
        DollarAmount getBalance();
        int getAccNo();
    }

    static class InsufficientFundsException extends Exception{

    }
}
