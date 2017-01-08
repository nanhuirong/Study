package com.huirong.jiangjuan.code.parse;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

/**
 * Created by Huirong on 17/1/8.
 */
public class PDFReader {
    public static String getPdfFileText(String fileName){
        int pageNumber = 1;
        StringBuilder sb = new StringBuilder();
        try {
            PdfReader reader = new PdfReader(fileName);
            while (pageNumber <= reader.getNumberOfPages()){
                try {
                    sb.append(PdfTextExtractor.getTextFromPage(reader, pageNumber));
                }catch (RuntimeException e){
                    pageNumber++;
                    System.out.println("-----");
                }finally {
                    pageNumber++;
                }

            }
        }catch (Exception e){

        }
        return sb.toString();
    }
}
