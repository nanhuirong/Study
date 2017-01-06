package com.huirong.quixey;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Huirong on 17/1/6.
 */
public class Test {
    public static void main(String[] args)throws Exception {
        File file = new File("/Users/quixeynew/1.txt");
        System.out.println(file.exists());
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        Map<String, Integer> map = new HashMap<String, Integer>();
        int count = 0;
        while ((line = br.readLine()) != null){
            count++;
            if (map.containsKey(line)){
                map.put(line, map.get(line) + 1);
            }else {
                map.put(line, 1);
            }
        }
        br.close();
        for (Map.Entry m : map.entrySet()){
            if ((Integer)m.getValue() > 1)
            System.out.println(m.getKey() + "\t" + m.getValue());
        }
        System.out.println(count);
        System.out.println(map.size());
    }
}
