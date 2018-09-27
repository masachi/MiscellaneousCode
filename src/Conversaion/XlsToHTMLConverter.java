package com.gtedx.file_converter;

import com.aspose.cells.HtmlSaveOptions;
import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.SaveOptions;
import com.aspose.cells.Workbook;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class XlsToHTMLConverter extends Converter {
    public XlsToHTMLConverter(String inPath, String outPath, InputStream inStream, OutputStream outStream, boolean showMessages, boolean closeStreamsWhenComplete) {
        super(inPath, outPath, inStream, outStream, showMessages, closeStreamsWhenComplete);
    }

    @Override
    public void convert() throws Exception {
        loading();
        processing();

        Workbook wb = new Workbook(inPath);
        HtmlSaveOptions options = new HtmlSaveOptions();
//        options.setAllColumnsInOnePagePerSheet(true);
        options.setExportImagesAsBase64(true);
        options.setExportActiveWorksheetOnly(true);

        for(int i = 0; i< wb.getWorksheets().getCount(); i++) {
            wb.getWorksheets().setActiveSheetIndex(i);
            wb.save(outPath.substring(0, outPath.lastIndexOf("/") + 1) + wb.getWorksheets().get(i).getName() + ".html", options);
        }

        Path outFile = Paths.get(outPath);
        FileChannel out = FileChannel.open(outFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        for(int i = 0; i< wb.getWorksheets().getCount(); i++) {
            Path inFile = Paths.get(outPath.substring(0, outPath.lastIndexOf("/") + 1) + wb.getWorksheets().get(i).getName() + ".html");
            FileChannel inChannel = FileChannel.open(inFile, StandardOpenOption.READ);
            for(long p =0, l = inChannel.size(); p<l ;) {
                p+= inChannel.transferTo(p, l-p, out);
            }

            new File(outPath.substring(0, outPath.lastIndexOf("/") + 1) + wb.getWorksheets().get(i).getName() + ".html").delete();
        }

        out.close();

        finished();
    }
}
