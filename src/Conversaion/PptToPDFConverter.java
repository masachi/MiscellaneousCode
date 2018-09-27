package com.gtedx.file_converter;

import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;

import java.io.InputStream;
import java.io.OutputStream;

public class PptToPDFConverter extends Converter {

    public PptToPDFConverter(String inPath, String outPath, InputStream inStream, OutputStream outStream, boolean showMessages, boolean closeStreamsWhenComplete) {
        super(inPath, outPath, inStream, outStream, showMessages, closeStreamsWhenComplete);
    }

    @Override
    public void convert() throws Exception {
        loading();
        processing();

        Presentation powerpoint = new Presentation(inPath);
        powerpoint.save(outStream, SaveFormat.Pdf);

        finished();
    }
}
