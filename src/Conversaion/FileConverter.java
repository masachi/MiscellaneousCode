package Conversaion;

import Conversaion.Converter;
import com.aspose.cells.CellsHelper;
import com.aspose.slides.FontsLoader;
import com.aspose.words.FontSettings;
import com.sun.istack.internal.Nullable;

import java.io.InputStream;
import java.io.OutputStream;

import static Conversaion.MainClass.getInFileStream;
import static Conversaion.MainClass.getOutFileStream;


public class FileConverter {
    private static boolean shouldShowMessages = true;
    private static Converter converter;

    public static void setFontsPath(String fontsPath) {
        initializeFonts(fontsPath);
    }

    public static void convertFilesToPDF (String inPath) {
        initializeConverter(inPath, MainClass.changeExtensionToPDF(inPath), inPath.toLowerCase().substring(inPath.lastIndexOf(".") + 1).toUpperCase());
    }

    public static void convertFilesToPDF (String inPath, String outPath) {
        initializeConverter(inPath, outPath, inPath.toLowerCase().substring(inPath.lastIndexOf(".") + 1).toUpperCase());
    }

    public static void convertFilesToPDF (String inPath, String outPath, String type){
        initializeConverter(inPath, outPath, type.toUpperCase());
    }

    private static void initializeConverter (String inPath, String outPath, String type){
        try {

            initializeLicense();

            InputStream inStream = getInFileStream(inPath);
            OutputStream outStream = getOutFileStream(outPath);

            if ("DOC".equals(type)) {
                converter = new DocToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);

            } else if ("DOCX".equals(type)) {
                converter = new DocxToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);

            } else if ("PPT".equals(type)) {
                converter = new PptToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);

            } else if ("PPTX".equals(type)) {
                converter = new PptxToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);

            } else if ("TXT".equals(type)) {
                converter = new TextToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);

            } else if ("XLS".equals(type)) {
                converter = new XlsToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);

            } else if ("XLSX".equals(type)) {
                converter = new XlsxToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);

            } else {
                converter = null;

            }

            convert();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void convert() {
        if(converter == null){
            System.out.println("Unable to determine type of input file.");
        } else {
            try {
                converter.convert();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void initializeLicense() throws Exception {
        com.aspose.words.License wordLicense = new com.aspose.words.License();
        wordLicense.setLicense(ClassLoader.getSystemResourceAsStream("license.xml"));
        com.aspose.cells.License excelLicense = new com.aspose.cells.License();
        excelLicense.setLicense(ClassLoader.getSystemResourceAsStream("license.xml"));
        com.aspose.slides.License pptLicense = new com.aspose.slides.License();
        pptLicense.setLicense(ClassLoader.getSystemResourceAsStream("license.xml"));
    }

    private static void initializeFonts(String fontPath) {
        if(fontPath != null) {
            System.out.println("Using given fonts path");
            FontSettings.setFontsFolder(fontPath, true);
            CellsHelper.setFontDir(fontPath);
            FontsLoader.loadExternalFonts(new String[]{fontPath});
        }
        else{
            System.out.println("Using system default fonts");
        }
    }

    public static void main(String[] args) {
//        convertFilesToPDF("/home/allen/Downloads/邮件工具详细设计.docx", "/tmp/1.pdf");
//        convertFilesToPDF("/Users/masachi/Documents/Pareto APP商用版 交互设计－1.0.docx", "/Users/masachi/Documents/Pareto APP商用版 交互设计－1.0.pdf");
//        convertFilesToPDF("/Users/masachi/Documents/180712 bug.xlsx", "/Users/masachi/Documents/180712 bug.pdf");
        convertFilesToPDF("/Users/masachi/Downloads/Pareto WEB商用版交互文档 v1.0.docx", "/Users/masachi/Downloads/Pareto WEB商用版交互文档 v1.0.docx.pdf");
    }
}
