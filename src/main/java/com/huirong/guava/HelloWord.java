package com.huirong.guava;

import com.google.common.io.ByteStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Huirong on 17/1/4.
 */
public class HelloWord {
    private static final Logger LOGGER = LoggerFactory.getLogger(com.huirong.commons.HelloWord.class);

    public static final String PATH = "/Users/quixeynew/1.log";
    public static void main(String[] args) {


        File file = new File(PATH);
        FileInputStream fis = null;
        try {
            LOGGER.info("start----");
            fis = new FileInputStream(file);
            ByteStreams.copy(fis, System.out);
        }catch (FileNotFoundException e){
            LOGGER.error(file.getAbsolutePath() + "not find");
        }catch (IOException e){
            LOGGER.error("ByteStreams.copy failed");
        }finally {
            LOGGER.info("finally");
        }
        LOGGER.info("end------");

    }
}
