import Utils.Utils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.exit;

/**
 * Created by sdlds on 2016/9/4.
 */
public class SaberImageDownload {
    public static void main(String[] args) throws IOException {
        String URL = "http://www.saber.xn--6qq986b3xl/";
        String[] image = {"http://saber-img.qiniudn.com/xuejian.jpg","http://saber-img.qiniudn.com/aisaberbg3.jpg","http://saber-img.qiniudn.com/aisaberbg7.jpg","http://saber-img.qiniudn.com/aisaberbg8.jpg","http://saber-img.qiniudn.com/aisaberbg9.jpg","http://7xk6om.com1.z0.glb.clouddn.com/萝莉夏日海滩壁纸.jpg","http://saber-img.qiniudn.com/穹妹美图.jpg","http://saber-img.qiniudn.com/Nekopara-BG1.jpg","http://7xk6om.com1.z0.glb.clouddn.com/miku2.jpg","http://saber-img.qiniudn.com/miku0.jpg","http://7xk6om.com1.z0.glb.clouddn.com/saberbg4.jpg","http://7xk6om.com1.z0.glb.clouddn.com/miku1.jpg","http://7xk6om.com1.z0.glb.clouddn.com/saberbg2.jpg","http://7xk6om.com1.z0.glb.clouddn.com/%E5%B1%81%E5%90%95%E5%B0%BE%E7%94%B0%E3%83%9D%E3%83%88%E3%83%95.jpg","http://7xk6om.com1.z0.glb.clouddn.com/四季映姫.jpg","http://7xk6om.com1.z0.glb.clouddn.com/loli.jpg","http://7xk6om.com1.z0.glb.clouddn.com/miku3.jpg","http://7xk6om.com1.z0.glb.clouddn.com/car.jpg","http://7xk6om.com1.z0.glb.clouddn.com/ef1.jpg","http://7xk6om.com1.z0.glb.clouddn.com/ef2.jpg","http://7xk6om.com1.z0.glb.clouddn.com/ef4.jpg","http://7xk6om.com1.z0.glb.clouddn.com/%E5%A6%B9%E3%81%AE%E5%BD%A21.jpg","http://7xk6om.com1.z0.glb.clouddn.com/%E6%B0%B4%E4%B8%ADmiku_2.jpg","http://7xk6om.com1.z0.glb.clouddn.com/websitebg0.jpg","http://7xk6om.com1.z0.glb.clouddn.com/re0.jpg","http://7xk6om.com1.z0.glb.clouddn.com/haitan.jpg","http://7xk6om.com1.z0.glb.clouddn.com/emilia1920.jpg","http://7xk6om.com1.z0.glb.clouddn.com/%E9%AC%BC%E5%88%80%E8%83%8C%E6%99%AF1.jpg","http://7xk6om.com1.z0.glb.clouddn.com/%E9%AC%BC%E5%88%80%E8%83%8C%E6%99%AF2.jpg","http://7xk6om.com1.z0.glb.clouddn.com/%E9%AC%BC%E5%88%80%E8%83%8C%E6%99%AF3.jpg"};
        for(String value : image){
            String[] temp = value.split("/");
            String img = new String();
            img = temp[temp.length-1];
            System.out.println(img);
            /*img = img + ".jpg";*/

            String filename = "D:\\Onedrive\\IFTTT\\Saber\\";

            filename = filename + img;

            boolean flag = DownloadModule.download(value,filename);
            if(flag){
                System.out.println(img + "已保存到本地");
            }
            else{
                System.out.println("Connection Time Out");
                exit(0);
            }
        }
    }
}
