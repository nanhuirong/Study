package com.huirong.hadoop;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.Path;



/**
 * Created by Huirong on 17/1/3.
 */
public class HelloWord {
    public static void main(String[] args) {
        Configuration conf = new Configuration();
        Path path = new Path("file:///Users/quixeynew/1.log");
    }

}
