package com.huirong.alluxio;

import alluxio.AlluxioURI;
import alluxio.client.file.FileInStream;
import alluxio.client.file.FileOutStream;
import alluxio.client.file.FileSystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by huirong on 2/15/17.
 */
public class AlluxioFile {
    public static final String PATH = "alluxio://localhost:19998/huirong.txt";

    public static void main(String[] args) throws Exception{
        AlluxioFile tool = new AlluxioFile();
        tool.write(PATH);
        tool.read(PATH);
    }

    public void write(String path)throws Exception{
        AlluxioURI uri = new AlluxioURI(path);
        FileSystem fs = FileSystem.Factory.get();
        FileOutStream fos = fs.createFile(uri);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        for (int i = 0; i < 100000000; i++){
            bw.write("nan hui rong\n");
        }
        bw.close();
        fos.close();
    }

    public void read(String path)throws Exception{
        FileSystem fs = FileSystem.Factory.get();
        AlluxioURI uri = new AlluxioURI(path);
        FileInStream fis = fs.openFile(uri);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line = null;
        while ((line = br.readLine()) != null){
            System.out.println(line);
        }
        br.close();
        fis.close();
    }
}
