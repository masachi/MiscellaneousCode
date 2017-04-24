package VerifyCode;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by Masachi on 2017/4/24.
 */
public class VerifyCode {

    public static String getVerifyCode(){
        ITesseract iTesseract = new Tesseract();
        iTesseract.setDatapath("libs/tessdata/");
        String text = "";
        try{
            text = iTesseract.doOCR(new File("file/temp.jpg"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return text;
    }

    private static byte[] streamToByte(){
        try{
            InputStream in = new FileInputStream(new File("file/temp.jpg"));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] data = new byte[4096];
            int count = -1;
            while((count = in.read(data,0,4096)) != -1){
                outputStream.write(data,0, count);
            }

            return outputStream.toByteArray();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new byte[0];
    }
}
