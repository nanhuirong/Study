package com.huirong.spark.graphx1.metrics;

import com.huirong.netflow.NetFlow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by huirong on 17-6-12.
 * 整合某个子网对应的外网的相对不确定性（源端口，目的端口）
 *      1.外网的各个子网对应的相对不确定性  流量占比
 *      2.外网分类结果对应的相对不确定性   流量占比
 */
public class Merge {
    private static String netflowPath = "/home/huirong/graph/tjut/2017-05-08/";
    private static String clusterPath = "/home/huirong/graph/tjut/2017-05-08-src-cluster/";
    private static String time = "13-30-00";
    private static String prefix = "59.67.153";

    public static void main(String[] args) throws Exception{
        Merge merge = new Merge();
        merge.merge();
    }

    /**
     * 整合并计算存盘
     */
    public void merge()throws Exception{
        List<NetFlow> list = readFileNetflow();
//        mergeNetflow(list);
        mergeCluster(list);
    }
    /**
     * 整合Netflow源数据
     */
    private void mergeNetflow(List<NetFlow> netFlows)throws Exception{
        String timeNetflow = time.substring(0, time.lastIndexOf("-")).replace("-", ":");
//        System.out.println(timeNetflow);
        Map<String, Integer> inPort = new HashMap<>();
        Map<String, Integer> outPort = new HashMap<>();
        for (NetFlow record: netFlows){
            if (record.getSrcIp().startsWith(prefix)){
                if (inPort.containsKey(record.getSrcPort())){
                    inPort.put(record.getSrcPort(), inPort.get(record.getSrcPort()) + 1);
                }else{
                    inPort.put(record.getSrcPort(), 1);
                }
            }else if (record.getDestIp().startsWith(prefix)){
                if (outPort.containsKey(record.getDestPort())){
                    outPort.put(record.getDestPort(), outPort.get(record.getDestPort()) + 1);
                }else{
                    outPort.put(record.getDestPort(), 1);
                }
            }
        }
//        for (Map.Entry entry: inPort.entrySet()){
//            System.out.println(entry.getKey() + "\t" + entry.getValue());
//        }
//        for (Map.Entry entry: outPort.entrySet()){
//            System.out.println(entry.getKey() + "\t" + entry.getValue());
//        }
        double inRU = calculateRU(inPort);
        double outRU = calculateRU(outPort);
        System.out.println(inRU + "\t" + outRU);
    }

    /**
     * 整合cluster数据
     */
    private void mergeCluster(List<NetFlow> netFlows)throws Exception{
        List<String> list = readFileCluster();
        Map<String, List<String>> map = new HashMap<>();
        for (String line : list){
            String[] split = line.split(",");
            if (map.containsKey(split[1])){
                map.get(split[1]).add(split[0]);
            }else {

                map.put(split[1], new LinkedList<String>());
                map.get(split[1]).add(split[0]);
            }
        }
        for (Map.Entry entry: map.entrySet()){
//            System.out.println(entry.getKey() +"\t" + ((List<String>)entry.getValue()).size());
            Set<String> ips = new HashSet<>((List<String>)entry.getValue());
            Map<String, Integer> inPort = new HashMap<>();
            Map<String, Integer> outPort = new HashMap<>();
            for (NetFlow record: netFlows){
                if (ips.contains(record.getSrcIp())){
                    if (inPort.containsKey(record.getSrcPort())){
                        inPort.put(record.getSrcPort(), inPort.get(record.getSrcPort()) + 1);
                    }else{
                        inPort.put(record.getSrcPort(), 1);
                    }
                }else if (ips.contains(record.getDestIp())){
                    if (outPort.containsKey(record.getDestPort())){
                        outPort.put(record.getDestPort(), outPort.get(record.getDestPort()) + 1);
                    }else{
                        outPort.put(record.getDestPort(), 1);
                    }
                }
            }

        }
    }

    /**
     * 计算相对不确定性
     */
    private double calculateRU(Map<String, Integer> map){
        Double counter = 0.0;
        for (Map.Entry entry : map.entrySet()){
            counter += (Integer) entry.getValue();
        }
        //计算信息熵
        Double H = 0.0;
        for (Map.Entry entry: map.entrySet()){
            Double p = (Integer)entry.getValue() / counter;
            H += p * Math.log(p);
        }
        Double RU = Math.abs(H / Math.log(map.size()));
        //四舍五入取一位小数
        return new BigDecimal(RU).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
//        return RU;
    }

    /**
     * 读取文件
     */
    private List<NetFlow> readFileNetflow()throws Exception{
        String timeNetflow = time.substring(0, time.lastIndexOf("-")).replace("-", ":");
        List<NetFlow> list = new LinkedList<>();
        File[] files = new File(netflowPath).listFiles();
        BufferedReader reader = null;
        for (File file: files){
            String line = null;
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null){
                NetFlow record = NetFlow.parse(line);
                if (record != null &&
                        record.getDate().split(" ")[1].startsWith(timeNetflow)){
                    list.add(record);
                }
            }
            reader.close();
        }
        return list;
    }

    /**
     * 读取分组数据
     */
    private List<String> readFileCluster()throws Exception{
        List<String> list = new LinkedList<>();
        BufferedReader reader = new BufferedReader(
                new FileReader(clusterPath + time + "/level_0_vertices/part-00000"));
        String line = null;
        while ((line = reader.readLine()) != null){
//            System.out.println(line);
            list.add(line);
        }
        reader.close();
        return list;
    }

    private class Record {
        private String srcPort;
        private String destPort;
        private Long bytes;

        public Record(String srcPort, String destPort, Long bytes) {
            this.srcPort = srcPort;
            this.destPort = destPort;
            this.bytes = bytes;
        }
    }
}
