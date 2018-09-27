package com.gtedx.file_converter;

import com.aspose.words.Document;
import java.io.InputStream;
import java.io.OutputStream;

public class DocToPDFConverter extends Converter{

    public DocToPDFConverter(String inPath, String outPath, InputStream inStream, OutputStream outStream, boolean showMessages, boolean closeStreamsWhenComplete) {
        super(inPath, outPath, inStream, outStream, showMessages, closeStreamsWhenComplete);
    }

    @Override
    public void convert() throws Exception {
        loading();

        processing();

        Document doc = new Document(inPath);
        doc.save(outStream, com.aspose.words.SaveFormat.PDF);

        finished();
    }

}
