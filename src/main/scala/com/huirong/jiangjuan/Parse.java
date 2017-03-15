package com.huirong.jiangjuan;


import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.lang.model.util.Elements;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by huirong on 17-3-13.
 * 2.	处理xml文件：
 * 按行row提取，每一行提取出标签为“Body”，“Title”，“Tags”的内容，
 * 存为一行，简单预处理（全部小写，去标点、符号、数字），
 * 最后按行保存为一个txt文档。共33566857行左右
 *
 * 每一行提取出标签为“Body”，“Title”，“Tags”的内容
 */
public class Parse {
    //解析XML文件
    public static String extract(String content){
        StringBuilder sb = null;
        try {
            sb = new StringBuilder();
            Document document = DocumentHelper.parseText(content);
            Element root = document.getRootElement();
            Attribute body = root.attribute("Body");
            sb.append(extractBody(body.getValue()));
            Attribute title = root.attribute("Title");
            if (title != null){
                sb.append(" " + title.getValue());
            }
            Attribute tags = root.attribute("Tags");
            if (tags != null){
                sb.append(extractTags(tags.getValue()));
            }
        }catch (Exception e){
            sb.append("");
        }finally {
            return sb.toString();
        }
    }

    private static String extractBody(String content){
        String regex = "[\n]";
        String subString = content.replaceAll(regex, "");
        regex = "<pre([\\s\\S])*>([\\s\\S]*)</pre>";
        subString = subString.replaceAll(regex, "");
        regex = "<code>([\\s\\S]*)</code>";
        subString = subString.replaceAll(regex, "");
        regex = "<.*?>";
        subString = subString.replaceAll(regex, "");
        return subString;
    }

    private static String extractTags(String content){
        String regex = "[<|>]";
        String subString = content.replaceAll(regex, " ");
        return subString;
    }


    //简单预处理
    public static String scrape(String content){
        String regex = "[^a-zA-Z]";
        content = content.replaceAll(regex, "");
        return content;
    }

    public static void main(String[] args)throws Exception {
        File file = new File("/home/huirong/shabi/Posts.xml");
        List<String> list = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        br.readLine();
        br.readLine();
        String line = null;
        int count = 0;
        while ((line = br.readLine()) != null && count < 10000){
            list.add(line);
            count++;
        }
        for (String content : list){
            System.out.println(extract(content));
        }
        br.close();
    }
}
