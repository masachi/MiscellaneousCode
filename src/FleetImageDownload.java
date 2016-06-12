import Encrypt.Base64Encrypt;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sdlds on 2016/6/2.
 */
public class FleetImageDownload {
    public static void main(String[] args) throws Exception{
        String URL = "http://fleet.diablohu.com/";
        String URL_MIDDLE = "/!/assets/images/homebg/";
        for(int i=0;i<30;i++){
            //String filename = "D:\\Onedrive\\IFTTT\\Fleets\\";
            String filename = "D:\\data\\Twitter\\";
            String temp = i + ".jpg";
            String value = URL + URL_MIDDLE + temp;
            //System.out.println(value);
            filename = DownloadModule.getFilename(temp,filename,URL_MIDDLE);
            boolean flag = DownloadModule.download(value, filename);
            if(flag){
                System.out.println(temp + "已保存到本地");
            }
            else{
                System.out.println(temp + "没有");
            }
            //String text = Base64Encrypt.encodeBase64(Base64Encrypt.ImageToByte(value));
            //System.out.println(i+"、"+text);
            /*boolean flag = Base64Encrypt.ByteToImage(filename,Base64Encrypt.decodeBase64(text));
            if(flag){
                System.out.println(temp + "已保存到本地");
            }
            else{
                System.out.println(temp + "没有");
            }*/
        }
    }
}
