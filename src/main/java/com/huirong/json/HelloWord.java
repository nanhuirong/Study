package com.huirong.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.json.JSONObject;

/**
 * Created by Huirong on 17/1/6.
 */
public class HelloWord {
    public static void main(String[] args)throws Exception {
        File file = new File("/Users/quixeynew/work/data/waimai.meiyuan.com/out/2017-01-06-08_05_55.157/cp/1.txt");
        String line = null;
        BufferedReader br = new BufferedReader(new FileReader(file));
        int count = 0;
        while ((line = br.readLine()) != null){
            JSONObject object = new JSONObject(line);
//            System.out.println(object.getString("url"));
            if (object.getJSONArray("location").getJSONObject(0).has("geo")){
                JSONObject geoObj = object.getJSONArray("location").getJSONObject(0).getJSONObject("geo");
                if (geoObj.has("latitude") && geoObj.has("longitude")){

                }else {

                    System.out.println(object.getString("url"));
                    System.out.println(line);
                }
            }else {
                System.out.println("not geo");
                System.out.println(line);
                System.out.println(object.getString("url"));
            }

        }
        System.out.println();
    }

}
