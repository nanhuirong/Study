//package com.huirong.alluxio;
//
//import alluxio.AlluxioURI;
//import alluxio.client.lineage.AlluxioLineage;
//import alluxio.job.JobConf;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by huirong on 2/16/17.
// */
//public class Lineage {
//    public static void main(String[] args) {
//        AlluxioLineage lineage = AlluxioLineage.get();
//        List<AlluxioURI> inputs = new ArrayList<AlluxioURI>();
//        inputs.add(new AlluxioURI(AlluxioTool.PREFIX_URI + "lineageInput1.txt"));
//        inputs.add(new AlluxioURI(AlluxioTool.PREFIX_URI + "lineageInput2.txt"));
//        List<AlluxioURI> outputs = new ArrayList<AlluxioURI>();
//        outputs.add(new AlluxioURI(AlluxioTool.PREFIX_URI + "lineageOutput.txt"));
//
//        JobConf conf = new JobConf("");
//    }
//}
