package com.github.sparkzxl.word;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import java.io.FileInputStream;
import java.io.InputStream;

public class Java2Word {

    public static String readWord(String path) {
        String buffer = "";
        try {
            if (path.endsWith(".doc")) {
                InputStream is = new FileInputStream(path);
                WordExtractor ex = new WordExtractor(is);
                buffer = ex.getText();
                ex.close();
            } else if (path.endsWith("docx")) {
                OPCPackage opcPackage = POIXMLDocument.openPackage(path);
                POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
                buffer = extractor.getText();
                extractor.close();
            } else {
                System.out.println("此文件不是word文件！");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return buffer;
    }

    public static String toDbc(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);

            }
        }
        return new String(c);
    }

    public static void main(String[] args) {
        String content = Java2Word.readWord("/Users/fin-9062/Downloads/法本 Java 孟进喜 北京.docx");
        String data = StringUtils.deleteWhitespace(content);
        data = toDbc(data);
        System.out.println(data);
        int index0 = data.indexOf("姓名:") + 3;
        String name = data.substring(index0, index0 + 3);
        System.out.println(name);
        int index = data.indexOf("性别:") + 3;
        String sex = data.substring(index, index + 1);
        System.out.println(sex);
        int index1 = data.indexOf("年龄:") + 3;
        String age = data.substring(index1, index1 + 2);
        System.out.println(age);
        int index2 = data.indexOf("籍贯:") + 3;
        String city = data.substring(index2, index2 + 6);
        System.out.println(city);
        int index3 = data.indexOf("手机:") + 3;
        String mobile = data.substring(index3, index3 + 11);
        System.out.println(mobile);
        int index4 = data.indexOf("邮箱:") + 3;
        int endIndex = data.indexOf(".com") + 4;
        String email = data.substring(index4, endIndex);
        System.out.println(email);
        int index5 = data.indexOf("住址:") + 3;
        String address = data.substring(index5, index5 + 6);
        System.out.println(address);
        int index6 = data.indexOf("年限:") + 3;
        String time = data.substring(index6, index6 + 3);
        System.out.println(time);
    }
}
