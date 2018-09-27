package com.gtedx.file_converter;

import com.aspose.words.*;

import java.io.InputStream;
import java.io.OutputStream;

public class DocxToHTMLConverter extends Converter{

    public DocxToHTMLConverter(String inPath, String outPath, InputStream inStream, OutputStream outStream, boolean showMessages, boolean closeStreamsWhenComplete) {
        super(inPath, outPath, inStream, outStream, showMessages, closeStreamsWhenComplete);
    }

    @Override
    public void convert() throws Exception {

        loading();
        processing();

        HtmlFixedSaveOptions options = new HtmlFixedSaveOptions();
//        options.setExportImagesAsBase64(true);
        options.setExportEmbeddedImages(true);
        options.setExportEmbeddedFonts(true);
        options.setExportEmbeddedCss(true);
        options.setExportEmbeddedSvg(true);
        options.setShowPageBorder(false);


        Document doc = new Document(inPath);
        doc.save(outStream, options);

        finished();

    }
}
