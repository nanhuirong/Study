package com.huirong.java.net.address;


import java.net.InetAddress;
import java.util.concurrent.Callable;

/**
 * Created by huirong on 17-3-20.
 */
public class LookupTask implements Callable<String> {
    private String line;
    public LookupTask(String line) {
        this.line = line;
    }
    @Override
    public String call() {
        try {
            //分解IP地址
            int index = line.indexOf(' ');
            String address = line.substring(0, index);
            String result = line.substring(index);
            String hostName = InetAddress.getByName(address).getHostName();
            return hostName + " " + result;
        }catch(Exception ex){
            return line;
        }
    }
}
