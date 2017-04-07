package com.huirong.java.net.socket.tcp;

import java.io.*;
import java.net.Socket;

/**
 * Created by huirong on 17-3-21.
 * 基于网络的英语-拉丁语翻译
 */
public class DictClient {
    public static final String SERVER = "dict.org";
    public static final int PORT = 2628;
    public static final int TIMEOUT = 35000;

    public static void main(String[] args) throws Exception{
        Socket socket = null;
        socket = new Socket(SERVER, PORT);
        socket.setSoTimeout(TIMEOUT);
        String[] array = {"gold", "uranium", "silver", "copper", "lead"};
        OutputStream out =socket.getOutputStream();
        Writer writer = new OutputStreamWriter(out, "UTF-8");
        InputStream in = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        for (String word: array){
            define(word, writer, reader);
        }
        writer.write("quit\r\n");
        writer.flush();
    }

    static void define(String word, Writer writer, BufferedReader reader) throws IOException {
        writer.write("DEFINE english " + word + "\r\n");
        writer.flush();
        String line = null;
        while ((line = reader.readLine()) != null){
            if (line.startsWith("250 ")){
                return;
            }else if (line.startsWith("552 ")){
                return;
            }
            else if (line.matches("\\d\\d\\d .*"))
                continue;
            else if (line.trim().equals("."))
                continue;
            else
                System.out.println(line);
        }
    }
}
