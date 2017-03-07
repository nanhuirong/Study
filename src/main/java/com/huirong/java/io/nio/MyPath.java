package com.huirong.java.io.nio;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import static java.nio.file.StandardWatchEventKinds.*;
import java.nio.file.WatchEvent.Kind;

/**
 * Created by huirong on 17-3-3.
 */
public class MyPath {
    public static void main(String[] args) {
        Path list = Paths.get("/usr/bin/zip");
        System.out.println("File Name [" + list.getFileName() + "]");
        System.out.println("Number of Name Elements in the Path [" + list.getNameCount() + "]");
        System.out.println("Parent Path [" + list.getParent() + "]");
        System.out.println("Root of Path [" + list.getRoot() + "]");
        System.out.println(list.subpath(0, 2));

        System.out.println("---------------------------------------------");
        MyPath.getProperties();
        System.out.println("---------------------------------------------");
        MyPath.findFiles();
        System.out.println("---------------------------------------------");
        MyPath.getFileAttribute();
        System.out.println("---------------------------------------------");
        MyPath.watchFile();
    }

    //列出目录下的Properties文件
    public static void getProperties(){
        Path dir = Paths.get("/home/huirong/work/jdk1.8.0_121");
        try (DirectoryStream<Path> stream
                     = Files.newDirectoryStream(dir, "*.properties")){
            for (Path entry : stream){
                System.out.println(entry.getFileName());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //遍历目录树
    public static void findFiles(){
        Path dir = Paths.get("/home/huirong/work/jdk1.8.0_121/src");
        try {
            Files.walkFileTree(dir, new FindJavaVisitor());
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    //获取文件属性
    public static void getFileAttribute(){
        try {
            Path zip = Paths.get("/usr/bin/zip");
            System.out.println(Files.getLastModifiedTime(zip));
            System.out.println(Files.size(zip));
            System.out.println(Files.isSymbolicLink(zip));
            System.out.println(Files.isDirectory(zip));
            System.out.println(Files.readAttributes(zip, "*"));
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    //读取文件
    public static void readFile(){
        Path logFile = Paths.get("/tmp/app.log");
        try (BufferedReader reader = Files.newBufferedReader(logFile, StandardCharsets.UTF_8)){
            String line;
            while ((line = reader.readLine()) != null){
                System.out.println(line);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //写文件
    public static void writeFile(){
        Path logFile = Paths.get("/tmp/app.log");
        try (BufferedWriter writer = Files.newBufferedWriter(logFile,
                StandardCharsets.UTF_8, StandardOpenOption.WRITE)){
            writer.write("Hello World");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //监控目录的变化
    public static void watchFile(){
        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path dir = FileSystems.getDefault().getPath("/home/huirong");
            dir.register(watcher, ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE);
            WatchKey key = null;
            while (true){
                key = watcher.take();
                for (WatchEvent event : key.pollEvents()){
                    Kind kind = event.kind();
                    System.out.println("Event on " + event.context().toString() + " is " + kind);
                }
                key.reset();
            }
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }



    private static class FindJavaVisitor extends SimpleFileVisitor<Path>{
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
            if (file.toString().endsWith(".java")){
                System.out.println(file.getFileName());
            }
            return FileVisitResult.CONTINUE;
        }
    }





}


