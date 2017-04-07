package com.huirong.java.net.address;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by huirong on 17-3-20.
 */
public class Test {
    public static void main(String[] args) throws UnknownHostException {
        InetAddress address = InetAddress.getByName("www.oreilly.com");
        System.out.println(address);
        //反向查找
        address = InetAddress.getByName("23.34.50.187");
        System.out.println(address.getHostName());
        //返回所有的地址列表
        InetAddress[] addresses = InetAddress.getAllByName("www.baidu.com");
        for (InetAddress address1 : addresses){
            System.out.println(address1);
        }
        //返回本机的域名
        address = InetAddress.getLocalHost();
        System.out.println(address);
    }
}
