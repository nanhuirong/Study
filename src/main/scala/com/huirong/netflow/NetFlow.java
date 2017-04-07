package com.huirong.netflow;

import sun.nio.ch.Net;

import java.io.*;
import java.text.SimpleDateFormat;

/**
 * Created by huirong on 17-3-28.
 */
public class NetFlow {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String date;    // 偷懒, 用字符串表示日期
    private Double duration;
    private String protocol;
    private String srcIp;
    private String srcPort;
    private String destIp;
    private String destPort;
    private String flags;
    private String tos;
    private Integer packets;
    private Long bytes;
    private Integer pps;
    private Long bps;
    private Long bytespp;
    private Integer flows;
    private String tag = null;
    private String timeFrame;

    public static NetFlow parse(String record){
        NetFlow netFlow = new NetFlow();
        String[] split = record.split(" ");
        if (isLegal(split) && isContent(split[0])){
            String[] fields = new String[14];
            int counter = 0;
            for (String field : split){
                String content = field.trim();
                if (content != null && content.length() > 0 && !content.contains("->")){
                    if(content.equals("M"))
                        fields[counter - 1] = String.valueOf((long)
                                (1024L * Double.parseDouble(fields[counter - 1])));
                    else if(content.equals("G")){
                        fields[counter - 1] = String.valueOf((long)
                                (1024L * 1024 * Double.parseDouble(fields[counter - 1])));
                    }
                    else{
                        fields[counter] = field;
                        counter ++;
                    }
                }
            }
            netFlow.date = (fields[0] + " " + fields[1]).substring(0, 19);
            netFlow.duration = Double.parseDouble(fields[2]);
            netFlow.protocol = fields[3];
            String[] src = fields[4].split(":");
            netFlow.srcIp = src[0];
            netFlow.srcPort = src[1];
            String[] dest = fields[5].split(":");
            netFlow.destIp = dest[0];
            netFlow.destPort = dest[1];
            netFlow.flags = fields[6];
            netFlow.tos = fields[7];
            netFlow.packets = Integer.parseInt(fields[8]);
            netFlow.bytes = Long.parseLong(fields[9]);
            netFlow.pps = Integer.parseInt(fields[10]);
            netFlow.bps = Long.parseLong(fields[11]);
            netFlow.bytespp = Long.parseLong(fields[12]);
            netFlow.tos = fields[13];
            return netFlow;
        }
        return null;
    }

    private static boolean isLegal(String[] array){
        if(array == null)
            return false;
        int counter = 0;
        String last = "";
        for(String s : array){
            String content = s.trim();
            if(content != null && content.length() > 0){
                counter ++;
                last = content;
            }
        }
        return counter >= 15 && counter <= 17 && last.equals("1");
    }

    private static boolean isContent(String first){
        if (first.contains("Date") ||
                first.contains("Time") ||
                first.contains("Total") ||
                first.contains("Sys") ||
                first.contains("Summary"))
            return false;
        else
            return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.date).append(",");
        builder.append(this.srcIp).append(",");
        builder.append(this.srcPort).append(",");
        builder.append(this.destIp).append(",");
        builder.append(this.destPort).append(",");
        builder.append(this.protocol);
        return builder.toString();
    }


    public String getSrcPort() {
        return srcPort;
    }

    public String getDestPort() {
        return destPort;
    }

    public boolean isHTTP(){
        if (srcPort.equals("80") || srcPort.equals("8080")){
            return true;
        }else if (destPort.equals("80") || destPort.equals("8080")){
            return true;
        }
        return false;
    }

    public static void main(String[] args)throws Exception {
        String path = "/home/huirong/work/data";
        File[] files = new File(path).listFiles();
        BufferedReader reader = null;
        String record = null;
        NetFlow netFlow = null;
        BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/result.txt"));
        long start = System.currentTimeMillis();
        for (File file : files){
//            System.out.println(file.getName());
            reader = new BufferedReader(new FileReader(file));
            while ((record = reader.readLine()) != null){
                netFlow = NetFlow.parse(record);
                if (netFlow != null && netFlow.isHTTP()){
//                    System.out.println(netFlow.toString());
                    writer.write(netFlow.toString() + "\n");
                }
            }
            reader.close();
        }
        writer.flush();
        writer.close();
        long end = System.currentTimeMillis();
        System.out.println((end - start) / 60000);
    }
}
