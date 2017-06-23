package com.huirong.spark.graphx1;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by huirong on 17-5-7.
 */
public class Merge {
    public static final String PATH = "/home/huirong/graph/tjut/2017-05-08-src-cluster/13/";
    public static void main(String[] args)throws Exception {
//        String link_path = PATH + "level_final_edges/part-00000";
//        String vertice_path = PATH + "level_final_vertices/part-00000";
//        JSONObject obj = new JSONObject();
//        JSONArray array = new JSONArray();
//        //添加边
//        String line = null;
//        BufferedReader br = new BufferedReader(new FileReader(link_path));
//        //统计所有的社区
//        HashSet<Long> comm = new HashSet();
//        class Link{
//            String source;
//            String target;
//            Long value;
//
//            public Link(String source, String target, Long value) {
//                this.source = source;
//                this.target = target;
//                this.value = value;
//            }
//        }
//        class Node{
//            String id;
//            Long comm;
//
//            public Node(String id, Long comm) {
//                this.id = id;
//                this.comm = comm;
//            }
//
//        }
//        List<Link> links = new ArrayList();
//        List<Node> nodes = new ArrayList();
//
//        while ((line = br.readLine()) != null){
//            String[] splits = line.split(",");
//            Link link = new Link(splits[0], splits[1], Long.parseLong(splits[2]));
//            links.add(link);
//        }
//        br = new BufferedReader(new FileReader(vertice_path));
//        line = null;
//        while ((line = br.readLine()) != null){
//            String[] splits = line.split(",");
//            String id = splits[0];
//            Long group = Long.parseLong(splits[1]);
//            Node node = new Node(id, group);
//            comm.add(group);
//            nodes.add(node);
//        }
//        System.out.println("分组数量:" + comm.size());
//        System.out.println("节点数量:" + nodes.size());
//        System.out.println("边数:" + links.size());
//
//        JSONArray nodeArr = new JSONArray();
//        for (Node node: nodes){
//            JSONObject object = new JSONObject();
//            object.put("id", node.id);
//            object.put("group", node.comm);
//            nodeArr.put(object);
//        }
//        JSONArray linkArr = new JSONArray();
//        for (Link link: links){
//            JSONObject object = new JSONObject();
//            object.put("source", link.source);
//            object.put("target", link.target);
//            object.put("value", link.value);
//            linkArr.put(object);
//        }
//        obj.put("links", linkArr);
//        obj.put("nodes", nodeArr);
//        BufferedWriter bw = new BufferedWriter(
//                new FileWriter(PATH + "total.json"));
//        obj.write(bw);
//        bw.close();



        metrics();

//        for (Long com : comm){
//            int counter = 0;
//            List<String> ips = new ArrayList<>();
//            JSONArray nodeArr = new JSONArray();
//            for (Node node : nodes){
//                if (Long.compare(node.comm, (long)com) == 0){
//                    ips.add(node.id);
//                    JSONObject object = new JSONObject();
//                    object.put("id", node.id);
//                    object.put("group", node.comm);
//                    nodeArr.put(object);
//                    counter += 1;
//                }
//            }
//            if (counter > 10){
//                JSONArray linkArr = new JSONArray();
//                for (Link link : links){
//                    if (ips.contains(link.source) && ips.contains(link.target)){
//                        JSONObject object = new JSONObject();
//                        object.put("source", link.source);
//                        object.put("target", link.target);
//                        object.put("value", link.value);
//                        linkArr.put(object);
//                    }
//                }
//                obj.put("links", linkArr);
//                obj.put("nodes", nodeArr);
//                System.out.println(com + "\t" + counter);
//                BufferedWriter bw = new BufferedWriter(
//                        new FileWriter(PATH + "out-" + com + "-" + counter + ".json"));
//                obj.write(bw);
//                bw.close();
//                System.out.println(counter);
//            }
//
////            if(counter > 100){
////                obj.put("links", linkArr);
////                obj.put("nodes", nodeArr);
////                System.out.println(com + "\t" + counter);
////                BufferedWriter bw = new BufferedWriter(
////                        new FileWriter(PATH + "out-" + com + "-" + counter + ".json"));
////                obj.write(bw);
////                bw.close();
////            }
//
//            counter = 0;
//        }
    }

    /**
     * 统计每分钟的ip和对应的分类数
     */
    public static void metrics()throws Exception{
        File[] files = new File(PATH).listFiles();
        List<String> list = new ArrayList<>();
        for (File file: files){
            String name = PATH + file.getName() + "/level_final_vertices/part-00000";
            BufferedReader reader = new BufferedReader(new FileReader(name));
            String line = null;
            Set<String> ips = new HashSet<>();
            Set<String> groups = new HashSet<>();
            while ((line = reader.readLine()) != null){
                String[] splits = line.split(",");
               ips.add(splits[0]);
               groups.add(splits[1]);
            }
            list.add(groups.size() + "," + ips.size());
            reader.close();
        }
        for (String line : list){
            System.out.println(line);
        }
    }
}
