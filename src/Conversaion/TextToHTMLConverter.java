package com.gtedx.file_converter;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.*;

public class TextToHTMLConverter extends Converter {

    public TextToHTMLConverter(String inPath, String outPath, InputStream inStream, OutputStream outStream, boolean showMessages, boolean closeStreamsWhenComplete) {
        super(inPath, outPath, inStream, outStream, showMessages, closeStreamsWhenComplete);
    }

    @Override
    public void convert() throws Exception {
        loading();

        processing();

        InputStreamReader read = new InputStreamReader(inStream, "UTF-8");
        // 考虑到编码格式
        BufferedReader bufferedReader = new BufferedReader(read);
        // 写文件

        OutputStreamWriter osw = new OutputStreamWriter(outStream, "UTF-8");
        BufferedWriter bw = new BufferedWriter(osw);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            bw.write(lineTxt + "</br>");
        }
        bw.close();
        osw.close();
        outStream.close();
        read.close();

        finished();
    }
}
