package com.huirong.spark.format;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huirong on 17-3-13.
 */
public class Parse {
    public static JSONObject parse(String line)throws Exception{
        JSONArray rows = new JSONObject(line).getJSONArray("rows");
        Map<String, List<String>> map = new HashMap<>();
        JSONObject obj = new JSONObject();
        for (int i = 0; i < rows.length(); i++){
            JSONArray city = rows.getJSONArray(i);
            JSONArray keyvalues = city.getJSONArray(2);
            for (int j = 0; j < keyvalues.length(); j++){
                String key = keyvalues.getJSONArray(j).getString(0);
                String value = keyvalues.getJSONArray(j).getString(1);
                System.out.println(key + value);
                if (map.containsKey(key)){
                    map.get(key).add(value);
                }else {
                    List<String> list= new ArrayList<>();
                    list.add(value);
                    map.put(key, list);
                }
            }
        }
        for (Map.Entry<String, List<String>> entry : map.entrySet()){
            String key = entry.getKey();
            List<String> values = entry.getValue();
            JSONArray array = new JSONArray();
            int index = 0;
            for (String value : values){
                array.put(index++, value);
            }
            obj.put(key, array);
        }
        System.out.println(obj);
        return obj;
    }

}
