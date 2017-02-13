package com.huirong.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Huirong on 17/1/6.
 */
public class HelloWord {
    public static void main(String[] args)throws Exception {
        String path = "/Users/quixeynew/1.txt";
        File file = new File(path);
        String line;
        BufferedReader br = new BufferedReader(new FileReader(file));
        while ((line = br.readLine()) != null){
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(line);
                if (jsonObject.has("url")){
                    System.out.println(jsonObject.getString("url"));
                }else {
                    System.out.println(line);
                }
            }catch (JSONException e){
                System.out.println("--------------");
                System.out.println(line);
            }


        }
    }

}
