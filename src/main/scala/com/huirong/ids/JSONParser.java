package com.huirong.ids;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by huirong on 17-2-23.
 */
public class JSONParser {
    public static JSONObject parse(String line){
        String[] split = line.replaceAll(" ", "").split("\\(none\\)");
        String newLine = split[1].substring(1);
        JSONObject obj = null;
        try {
            obj = new JSONObject(newLine);
//            System.out.println(obj.get("level"));
        }catch (JSONException e){
//            System.out.println(newLine);
            obj = null;
        }finally {
            return obj;
        }

    }
}
