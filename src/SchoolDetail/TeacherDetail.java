package SchoolDetail;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by sdlds on 2017/2/26.
 */
public class TeacherDetail {
    private static String BASE_URL = "http://yzw.gdut.edu.cn/dsfc/sssds/";
    private static String EXTRA_URL = ".htm";
    private static Document doc = null;
    private static WebClient wc;

//    public static void main(String[] args){
//        try{
//            getData("jsjxy");
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    public static void getData(String school) throws Exception{
        SetBrowser();
        String url = BASE_URL + school + EXTRA_URL;
        getDataFromWeb(url);
    }

    private static void getDataFromWeb(String url) throws Exception{
        System.out.println(url);
        HtmlPage page = wc.getPage(url);
        String pageXml = page.asXml();
        doc = Jsoup.parse(pageXml);
        getTeacher();
    }

    private static void getTeacher(){
        String name;
        String pic;
        Elements results = doc.getElementById("s_llist").select("li");
        for(Element teacher : results){
            name = teacher.select("strong").text();
            System.out.println(name);
        }
    }

    private static void SetBrowser() {
        wc = new WebClient(BrowserVersion.CHROME);
        wc.getOptions().setJavaScriptEnabled(true); //启用JS解释器，默认为true
        wc.getOptions().setCssEnabled(false); //禁用css支持
//        wc.getOptions().setProxyConfig(new ProxyConfig("185.10.17.134",3128));
        wc.getCookieManager().setCookiesEnabled(false);
        wc.getOptions().setThrowExceptionOnScriptError(false); //js运行错误时，是否抛出异常
        wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
        wc.getOptions().setTimeout(10000); //设置连接超时时间 ，这里是10S。如果为0，则无限期等待

        wc.waitForBackgroundJavaScript(600 * 1000);
        wc.setAjaxController(new NicelyResynchronizingAjaxController());

        wc.waitForBackgroundJavaScript(1000 * 3);
        wc.setJavaScriptTimeout(0);
        wc.getOptions().setTimeout(50000);
    }
}
