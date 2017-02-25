package com.huirong.jiangjuan.code.algorithm;

import java.util.*;

/**
 * Created by huirong on 17-2-24.
 */
public class H {
    public static void main(String[] args) {
        HashMap map_Data=new HashMap();
        map_Data.put("A", "98");
        map_Data.put("B", "50");
        map_Data.put("C", "50");
        map_Data.put("D", "25");
        map_Data.put("E", "85");
        System.out.println(map_Data);
        List<Map.Entry<String, String>> list_Data = new ArrayList<Map.Entry<String, String>>(map_Data.entrySet());
        Collections.sort(list_Data, new Comparator<Map.Entry<String, String>>()
        {
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2)
            {
                if(o2.getValue()!=null&&o1.getValue()!=null&&o2.getValue().compareTo(o1.getValue())>0){
                    return 1;
                }else{
                    return -1;
                }

            }
        });
        System.out.println(list_Data);
    }

}

