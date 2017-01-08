package com.huirong.jiangjuan.code.parse;

import com.huirong.jiangjuan.code.download.LocalPath;

import java.io.File;

/**
 * Created by Huirong on 17/1/8.
 */
public class Parse {
    public static void parseFiles(){
        File source = new File(LocalPath.getContentPath());
        File[] files = source.listFiles();
        for (File file: files){
            PDFReader.getPdfFileText(file.getAbsolutePath());
        }
    }
}
