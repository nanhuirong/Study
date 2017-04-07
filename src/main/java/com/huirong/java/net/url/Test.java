package com.huirong.java.net.url;


import org.glassfish.grizzly.utils.BufferInputStream;

import java.io.*;
import java.net.URL;

/**
 * Created by huirong on 17-3-20.
 */
public class Test {
    public static void main(String[] args) {
        try {
            URL url = new URL("http://www.baidu.com");
            try (Reader reader = new InputStreamReader(new BufferedInputStream(url.openStream()))){
                   int data;
                   while ((data = reader.read()) != -1){
                       System.out.print((char) data);
                   }
            }
        }catch (Exception ex){
            //ingore
        }
    }
}
