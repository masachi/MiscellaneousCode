package Conversaion;

import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.Workbook;

import java.io.InputStream;
import java.io.OutputStream;

public class XlsToPDFConverter extends Converter {
    public XlsToPDFConverter(String inPath, String outPath, InputStream inStream, OutputStream outStream, boolean showMessages, boolean closeStreamsWhenComplete) {
        super(inPath, outPath, inStream, outStream, showMessages, closeStreamsWhenComplete);
    }

    @Override
    public void convert() throws Exception {
        loading();
        processing();

        Workbook wb = new Workbook(inPath);
        PdfSaveOptions options = new PdfSaveOptions();
        options.setAllColumnsInOnePagePerSheet(true);

        wb.save(outStream, options);

        finished();
    }
}