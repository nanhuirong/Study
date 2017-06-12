package com.huirong.netflow;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by huirong on 17-6-6.
 */
public class NetFlowTool {
    private static Set<String> TJUTIP = new HashSet<>();
    private static Set<String> EDUIP = new HashSet<>();
    private static String local = "192.168.";
    private static String wlans = "172.";

    static {
        //天津理工大学内网ip
        for (int i = 64; i <= 79; i++){
            StringBuilder builder = new StringBuilder();
            builder.append("202.113.")
                    .append(i);
//                    .append(".");
            TJUTIP.add(builder.toString());
        }
        for (int i = 144; i <= 159; i++){
            StringBuilder builder = new StringBuilder();
            builder.append("59.67.")
                    .append(i);
//                    .append(".");
            TJUTIP.add(builder.toString());
        }

        for (int i = 48; i <= 63; i++){
            StringBuilder builder = new StringBuilder();
            builder.append("58.207.")
                    .append(i);
//                    .append(".");
            EDUIP.add(builder.toString());
        }
        for (int i = 0; i <= 191; i++){
            StringBuilder builder = new StringBuilder();
            builder.append("59.67.")
                    .append(i);
//                    .append(".");
            EDUIP.add(builder.toString());
        }
        for (int i = 0; i <= 255; i++){
            StringBuilder builder = new StringBuilder();
            builder.append("202.113.")
                    .append(i);
//                    .append(".");
            EDUIP.add(builder.toString());
        }
        for (int i = 0; i <= 63; i++){
            StringBuilder builder = new StringBuilder();
            builder.append("211.81.")
                    .append(i);
//                    .append(".");
            EDUIP.add(builder.toString());
        }
    }

    /**
     * 判断ip是否为理工内网ip
     * @return
     */
    public static boolean isTJUT(String ip){
        if (ip == null || ip.length() == 0){
            return false;
        }
        if (ip.startsWith(local) || ip.startsWith(wlans)){
            return true;
        }
//        ip = ip.substring(0, ip.lastIndexOf("."));
        ip = ip.substring(0, ip.lastIndexOf("."));
        if (TJUTIP.contains(ip)){
            return true;
        }
        return false;
    }

    /**
     * 判断ip是否为教育网数据
     * @param ip
     * @return
     */
    public static boolean isEDU(String ip){
        if (ip == null || ip.length() == 0){
            return false;
        }
//        ip = ip.substring(0, ip.lastIndexOf("."));
        ip = ip.substring(0, ip.lastIndexOf("."));
        if (EDUIP.contains(ip)){
            return true;
        }
        return false;
    }

    public static boolean isWans(String ip){
        if (ip == null || ip.length() == 0){
            return false;
        }
        if (ip.startsWith(wlans)){
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        Iterator iterator = EDUIP.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
        System.out.println(isTJUT("59.67.152.245"));
    }
}
