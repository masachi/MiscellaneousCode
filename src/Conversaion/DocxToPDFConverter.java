package Conversaion;

import com.aspose.words.Document;

import java.io.InputStream;
import java.io.OutputStream;

public class DocxToPDFConverter extends Converter {

    public DocxToPDFConverter(String inPath, String outPath, InputStream inStream, OutputStream outStream, boolean showMessages, boolean closeStreamsWhenComplete) {
        super(inPath, outPath, inStream, outStream, showMessages, closeStreamsWhenComplete);
    }

    @Override
    public void convert() throws Exception {
        loading();
        processing();

//        WordprocessingMLPackage mlPackage = WordprocessingMLPackage.load(new File(inPath));
//        //Mapper fontMapper = new BestMatchingMapper();
//        Mapper fontMapper = new IdentityPlusMapper();
//
//        String fontFamily = "SimSun";
//        String arialFamily = "ArialMT";
//
//        URL simsunUrl = this.getClass().getResource("/fonts/simsun.ttc"); //加载字体文件（解决linux环境下无中文字体问题）
//        PhysicalFonts.addPhysicalFonts(fontFamily, simsunUrl);
//        PhysicalFonts.addPhysicalFonts(arialFamily, arialUrl);
//        PhysicalFont simsunFont = PhysicalFonts.get(fontFamily);
//        PhysicalFont arialFont = PhysicalFonts.get(arialFamily);
//        fontMapper.put(fontFamily, simsunFont);
//        fontMapper.put(arialFamily, arialFont);
//
//        mlPackage.setFontMapper(fontMapper);
//
//        OutputStream os = new java.io.FileOutputStream(outPath);
//
//        FOSettings foSettings = Docx4J.createFOSettings();
//        foSettings.setWmlPackage(mlPackage);
//
//        foSettings.setApacheFopMime("application/pdf");
//        foSettings.setApacheFopConfiguration("<?xml version=\"1.0\" encoding=\"UTF-8\"?><fop version=\"1.1\"><renderers><renderer mime=\"application/pdf\"><fonts><directory>resources/fonts/</directory><auto-detect/></fonts></renderer></renderers></fop>");
//
////        Docx4J.toPDF(mlPackage, os);
//        Docx4J.toFO(foSettings, os, FLAG_EXPORT_PREFER_XSL);


        Document doc = new Document(inPath);
        doc.save(outStream, com.aspose.words.SaveFormat.PDF);

        finished();
    }
}
