import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.exit;

/**
 * Created by sdlds on 2016/6/1.
 */
public class TwitterPhotoDownload {
    public static void main(String[] args) throws IOException{
        String URL = "https://twitter.com/sherryken777";
        String originstr = Utils.streamToString(Utils.getUrlStream(URL));
        //String originstr = "";
        originstr = originstr.replaceAll("\n", "");
        originstr = originstr.replaceAll("\r", "");

        Pattern p = Pattern.compile(".*?(https://pbs.twimg.com/media/.*?.jpg).*?");
        Matcher m = p.matcher(originstr);
        String URL_HEAD = "https://pbs.twimg.com/media/";

        while(m.find()){
            //System.out.println(m.group(1));
            String value = parsetolarge(m.group(1));
            String filename = "D:\\Onedrive\\IFTTT\\Twitter\\Sherry\\";
            filename = DownloadModule.getFilename(value,filename,URL_HEAD);
            boolean flag = DownloadModule.download(value,filename);
            if(flag){
                System.out.println(value + "已保存到本地");
            }
            else{
                System.out.println("Connection Time Out");
                exit(0);
            }
        }
    }

    private static String parsetolarge(String value) throws IOException{
        value = value + ":large";
        return value;
    }
}