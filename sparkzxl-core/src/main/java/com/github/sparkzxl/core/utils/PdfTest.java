package com.github.sparkzxl.core.utils;

public class PdfTest {

    public static void main(String[] args) {
        DocumentPdfTemplate documentPdfTemplate = new DocumentPdfTemplate();
        String fileName = "test.pdf";
        documentPdfTemplate.fileName = fileName;
        documentPdfTemplate.initPdfData();
        documentPdfTemplate.currPage = 1;
        documentPdfTemplate.top = 785;
        documentPdfTemplate.currLeft = 55;
        documentPdfTemplate.addMainTitle(0, "test");
        documentPdfTemplate.wrap();
        documentPdfTemplate.addText("testhhhhh");
        documentPdfTemplate.getDocument().close();
    }
}
