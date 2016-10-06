package MultiThread;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by sdlds on 2016/9/16.
 */
public class MultiThreadDownload {
    public static void main(String[] args) {

        int threadNum = 10;
        long block = 0L;
        URL url = null;
        int len = 0;
        Path path = null;
        try {
            //url = new URL("http://dlsw.baidu.com/sw-search-sp/soft/3a/12350/QQ_8.1.17283.0_setup.1458109312.exe");
            url = new URL("http://img5.duitang.com/uploads/item/201412/19/20141219092843_wLGTF.png");
//            url = new URL("http://cv4.jikexueyuan.com/21961266f6cab8d6bef2d7ec17c086e0/201603222320/course/1801-1900/1847/video/5036_b_h264_sd_960_540.mp4");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            len = conn.getContentLength();
            conn.disconnect();

            path = Paths.get("C:/Users/sdlds/Desktop/233.jpg");

            block = len % threadNum == 0 ? len / threadNum : len / threadNum + 1;
            System.out.println("每个线程下载：" + block);

            long start = 0L;
            long end = 0L;

            for (int i = 0; i < threadNum; i++) {
                start = i * block;
                end = start + (block - 1);
                System.out.println("start:" + start + " --> end:" + end);
                new Thread(new DownloadThread(path.toFile(), start, end, url)).start();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
