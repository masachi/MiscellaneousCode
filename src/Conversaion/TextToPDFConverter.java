package Conversaion;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;

public class TextToPDFConverter extends Converter {

    public TextToPDFConverter(String inPath, String outPath, InputStream inStream, OutputStream outStream, boolean showMessages, boolean closeStreamsWhenComplete) {
        super(inPath, outPath, inStream, outStream, showMessages, closeStreamsWhenComplete);
    }

    @Override
    public void convert() throws Exception {
        loading();

        BaseFont bfChinese = BaseFont.createFont(this.getClass().getResource("/fonts/simsun.ttc").toString() + ",0", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        Font FontChinese = new Font(bfChinese);

        Rectangle rect = new Rectangle(PageSize.A4.rotate());
        Document doc = new Document(rect);
        PdfWriter writer = PdfWriter.getInstance(doc, outStream);
        doc.open();
        Paragraph p = new Paragraph();
        p.setFont(FontChinese);

        processing();

        BufferedReader read = new BufferedReader(new FileReader(inPath));
        String line = read.readLine();
        while(line != null){
            p.add(line + "\n");
            line = read.readLine();
        }
        read.close();
        doc.add(p);
        doc.close();
        writer.close();

        finished();
    }
}
