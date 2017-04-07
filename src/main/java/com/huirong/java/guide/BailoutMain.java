package com.huirong.java.guide;

import javafx.scene.effect.SepiaTone;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by huirong on 17-4-3.
 */
public class BailoutMain {
    public final static int TEST_TIME = 240 * 1000;
//    public static Random random = new Random(Thread.currentThread().getId());
    public final static ThreadLocal<Random> random =
        new ThreadLocal<Random>(){
            @Override
            protected Random initialValue() {
                return new Random(Thread.currentThread().getId());
            }
        };
    private static char[] alphabet = {
            'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x',
            'y', 'z'
    };
    //美国的50个州
    private static String[] states = {
            "Alabama", "Alaska", "Arizona", "Arkansas", "California",
            "Colorado", "Connecticut", "Delaware", "Florida", "Georgia",
            "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa",
            "kansas", "Kentucky", "Louisiana", "Maine", "Maryland",
            "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri",
            "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey",
            "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio",
            "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South carlina",
            "South Dakota", "Tennessee", "texas", "Utah", "Vermont",
            "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"
    };

    public static void main(String[] args) {
        final int numberOfThreads = Runtime.getRuntime().availableProcessors();
//        final int numberOfThreads = 64;
        final int dbSize = TaxpayerBailoutDB.NUMBER_OF_RECORDS_DESIRED;
        final int taxpayerListSize = dbSize / numberOfThreads;
        System.out.println("Number of threads to run: " + numberOfThreads);
        System.out.println("Tax payer database size: " + dbSize);
        System.out.println("Creating tax payer");
        long start = System.nanoTime();
        TaxpayerBailoutDB db = new TaxPayerBailoutDbImpl(dbSize);
        List<String>[] taxpayerList = new ArrayList[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++){
            taxpayerList[i] = new ArrayList<String>(taxpayerListSize);
        }
        populateDatabash(db, taxpayerList, dbSize);
        System.out.println("Tax payer database created");
        ExecutorService pool = Executors.newFixedThreadPool(numberOfThreads);
        Callable<BailoutFuture>[] callables = new TaxCallable[numberOfThreads];
        for (int i = 0; i < callables.length; i++){
            callables[i] = new TaxCallable(taxpayerList[i], db);
        }
        long initDB = System.nanoTime() - start;
        System.out.println("initDB" + initDB / (1000 * 1000));

        System.out.println("threads allocated");
        Set<Future<BailoutFuture>> set = new HashSet<Future<BailoutFuture>>();
        for (int i = 0; i < callables.length; i++){
            Callable<BailoutFuture> callable = callables[i];
            Future<BailoutFuture> future = pool.submit(callable);
            set.add(future);
        }
        System.out.println("wait for threads to completed");
        double iterationPerSeconds = 0;
        long recordsAdded = 0, recordsRemoved = 0;
        long nullCounter = 0;
        int counter = 1;
        for (Future<BailoutFuture> future : set){
            BailoutFuture result = null;
            try {
                result = future.get();
                System.out.println("iterations per seconds on threads " +
                        counter++ + " -> " + result.getIterationsPerSecond());
                iterationPerSeconds += result.getIterationsPerSecond();
                recordsAdded += result.getRecordsAdded();
                recordsRemoved += result.getRecordsRemoved();
                nullCounter += result.getNullCounter();
            }catch (InterruptedException ex){
                ex.printStackTrace();
            }catch (ExecutionException ex){
                ex.printStackTrace();
            }

        }
        DecimalFormat df = new DecimalFormat("#.##");
        System.out.println("total iterations per seconds " + df.format(iterationPerSeconds));
        NumberFormat nf = NumberFormat.getInstance();
        System.out.println("added " + nf.format(recordsAdded));
        System.out.println("removed " + nf.format(recordsRemoved));
        System.out.println("nullCounter " + nf.format(nullCounter));
//        System.exit(0);
    }

    public static TaxPayerRecord makeTaxPayerRecord(){
        String firstName = getRandomName();
        String lastName = getRandomName();
        String ssn = getRandomSSN();
        String address = getRandomAddress();
        String city = getRandomCity();
        String states = getRandomState();
        return new TaxPayerRecord(firstName, lastName, ssn, address, city, states);
    }

    public static void populateDatabash(TaxpayerBailoutDB db,
                                        List<String>[] taxPayerIdList,
                                        int dbSize){
        for (int i = 0; i < dbSize; i++){
            String key = getRandompayerId();
            TaxPayerRecord tpr = makeTaxPayerRecord();
            db.add(key, tpr);
            int index = i % taxPayerIdList.length;
            taxPayerIdList[index].add(key);
        }
    }

    public static String getRandompayerId(){
        StringBuilder builder = new StringBuilder(20);
        for (int i = 0; i < 20; i++){
            int index = random.get().nextInt(alphabet.length);
            builder.append(alphabet[index]);
        }
        return builder.toString();
    }

    public static String getRandomName(){
        StringBuilder builder = new StringBuilder();
        int size = random.get().nextInt(8) + 5;
        for (int i = 0; i < size; i++){
            int index = random.get().nextInt(alphabet.length);
            char c = alphabet[index];
            if (i == 0){
                c = Character.toUpperCase(c);
            }
            builder.append(c);
        }
        return builder.toString();
    }

    public static String getRandomSSN(){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 11; i++){
            if (i == 3 || i == 6){
                builder.append("-");
            }
            int x = random.get().nextInt(9);
            builder.append(x);
        }
        return builder.toString();
    }

    public static String getRandomAddress(){
        StringBuilder builder = new StringBuilder(24);
        int size = random.get().nextInt(14) + 10;
        for (int i = 0; i < size; i++){
            if (i < 5){
                int x = random.get().nextInt(8);
                builder.append(x + 1);
            }
            int index = random.get().nextInt(alphabet.length);
            char c = alphabet[index];
            if (i == 5){
                c = Character.toUpperCase(c);
            }
            builder.append(c);
        }
        return builder.toString();
    }

    public static String getRandomCity(){
        StringBuilder builder = new StringBuilder();
        int size = random.get().nextInt(5) + 6;
        for (int i = 0; i < size; i++){
            int index = random.get().nextInt(alphabet.length);
            char c = alphabet[index];
            if (i == 0){
                c = Character.toUpperCase(c);
            }
            builder.append(c);
        }
        return builder.toString();
    }

    public static String getRandomState(){
        int index = random.get().nextInt(states.length);
        return states[index];
    }
}
