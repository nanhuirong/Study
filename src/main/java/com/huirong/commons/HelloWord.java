package com.huirong.commons;


import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.tuple.Pair;
//import static java.util.Objects.requireNonNull;

import java.io.*;

/**
 * Created by Huirong on 17/1/3.
 */
public class HelloWord {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWord.class);

    public static void main(String[] args) {
        LOGGER.info("start");
        //test Pair
        Pair<String, String> pair = new ImmutablePair<String, String>("nan", "huirong");
        LOGGER.info(pair.getLeft());
        LOGGER.info(pair.getRight());
        LOGGER.info("end");

        //test
        String line = "nanhuirong";
//        requireNonNull(line);

        //test
        String format = String.format(
                "var %s = %s",
                "nan", "huirong");
        System.out.println(format);

    }
}
