package com.gtedx.file_converter;

import com.aspose.cells.CellsHelper;
import com.aspose.slides.FontsLoader;
import com.aspose.words.FontSettings;

import java.io.InputStream;
import java.io.OutputStream;

import static com.gtedx.file_converter.MainClass.getInFileStream;
import static com.gtedx.file_converter.MainClass.getOutFileStream;

public class FileConverter {
    private static boolean shouldShowMessages = true;
    private static Converter converter;
    private static String fontsPath = null;

    public static void setFontsPath(String path) {
        fontsPath = path;
    }

    public static void convertFiles(String inPath) {
        initializeConverter(inPath, MainClass.changeExtensionToPDF(inPath), inPath.toLowerCase().substring(inPath.lastIndexOf(".") + 1).toUpperCase());
    }

    public static void convertFiles(String inPath, ConvertType convertType) {
        switch (convertType) {
            case PDF:
                initializeConverter(inPath, MainClass.changeExtensionToPDF(inPath), inPath.toLowerCase().substring(inPath.lastIndexOf(".") + 1).toUpperCase());
                break;
            case HTML:
                initializeConverter(inPath, MainClass.changeExtensionToHTML(inPath), inPath.toLowerCase().substring(inPath.lastIndexOf(".") + 1).toUpperCase());
                break;
            default:
                initializeConverter(inPath, MainClass.changeExtensionToPDF(inPath), inPath.toLowerCase().substring(inPath.lastIndexOf(".") + 1).toUpperCase());
                break;
        }
    }

    public static void convertFiles(String inPath, String outPath) {
        initializeConverter(inPath, outPath, inPath.toLowerCase().substring(inPath.lastIndexOf(".") + 1).toUpperCase());
    }

    public static void convertFiles(String inPath, String outPath, String type) {
        initializeConverter(inPath, outPath, type.toUpperCase());
    }

    private static void initializeConverter(String inPath, String outPath, String type) {
        try {
            initializeLicense();
            initializeFonts();

            InputStream inStream = getInFileStream(inPath);
            OutputStream outStream = getOutFileStream(outPath);

            if ("DOC".equals(type)) {
                switch (outPath.toLowerCase().substring(outPath.lastIndexOf(".") + 1).toUpperCase()) {
                    case "PDF":
                        converter = new DocToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                        break;
                    case "HTML":
                        converter = new DocToHTMLConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                        break;
                    default:
                        converter = null;
                        break;
                }

            } else if ("DOCX".equals(type)) {
                switch (outPath.toLowerCase().substring(outPath.lastIndexOf(".") + 1).toUpperCase()) {
                    case "PDF":
                        converter = new DocxToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                        break;
                    case "HTML":
                        converter = new DocxToHTMLConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                        break;
                    default:
                        converter = null;
                        break;
                }

            } else if ("PPT".equals(type)) {
                switch (outPath.toLowerCase().substring(outPath.lastIndexOf(".") + 1).toUpperCase()) {
                    case "PDF":
                        converter = new PptToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                        break;
                    case "HTML":
                        converter = new PptToHTMLConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                        break;
                    default:
                        converter = null;
                        break;
                }

            } else if ("PPTX".equals(type)) {
                switch (outPath.toLowerCase().substring(outPath.lastIndexOf(".") + 1).toUpperCase()) {
                    case "PDF":
                        converter = new PptxToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                        break;
                    case "HTML":
                        converter = new PptxToHTMLConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                        break;
                    default:
                        converter = null;
                        break;
                }

            } else if ("TXT".equals(type)) {
                switch (outPath.toLowerCase().substring(outPath.lastIndexOf(".") + 1).toUpperCase()) {
                    case "PDF":
                        converter = new TextToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                        break;
                    case "HTML":
                        converter = new TextToHTMLConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                        break;
                    default:
                        converter = null;
                        break;
                }

            } else if ("XLS".equals(type)) {
                switch (outPath.toLowerCase().substring(outPath.lastIndexOf(".") + 1).toUpperCase()) {
                    case "PDF":
                        converter = new XlsToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                        break;
                    case "HTML":
                        converter = new XlsToHTMLConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                        break;
                    default:
                        converter = null;
                        break;
                }

            } else if ("XLSX".equals(type)) {
                switch (outPath.toLowerCase().substring(outPath.lastIndexOf(".") + 1).toUpperCase()) {
                    case "PDF":
                        converter = new XlsxToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                        break;
                    case "HTML":
                        converter = new XlsxToHTMLConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                        break;
                    default:
                        converter = null;
                        break;
                }

            } else {
                converter = null;

            }

            convert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void convert() {
        if (converter == null) {
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

    private static void initializeFonts() {
        if (fontsPath != null) {
            System.out.println("Using given fonts path");
            FontSettings.setFontsFolder(fontsPath, true);
            CellsHelper.setFontDir(fontsPath);
            FontsLoader.loadExternalFonts(new String[]{fontsPath});
        } else {
            System.out.println("Using system default fonts");
        }
    }

    public static void main(String[] args) {
//        setFontsPath("/Users/masachi/Documents/temp/fonts");
//        convertFilesToPDF("/home/allen/Downloads/邮件工具详细设计.docx", "/tmp/1.pdf");
        convertFiles("/Users/masachi/Documents/Pareto APP商用版 交互设计－1.0.docx", "/Users/masachi/Documents/Pareto APP商用版 交互设计－1.0.html");
//        convertFiles("/Users/masachi/Documents/180712 bug.xlsx", "/Users/masachi/Documents/180712 bug.html");
//        convertFiles("/Users/masachi/Documents/IM.pptx", "/Users/masachi/Documents/IM.pptx.html");
//        convertFiles("/Users/masachi/Documents/test.txt", "/Users/masachi/Documents/test.txt.pdf");
//        convertFiles("/Users/masachi/Documents/test.txt", "/Users/masachi/Documents/test.txt.html");
//        convertFiles("/Users/masachi/Desktop/聊天SDK详细设计.docx", "/Users/masachi/Downloads/聊天SDK详细设计.html");
//        convertFilesToPDF("/Users/masachi/Documents/180712 bug.xlsx", "/Users/masachi/Documents/180712 bug.pdf");
    }
}
