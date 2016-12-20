package AutoLogin;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;

/**
 * Created by sdlds on 2016/12/12.
 */
public class LoginUtils {
    private static Cookie cookie;
    private static String Url = "http://202.118.201.228/academic/j_acegi_security_check";
    private static WebClient wc = new WebClient(BrowserVersion.CHROME);
    private static Document doc = null;

    public static void main(String[] args){

    }

    private static void SetBrowser(){
        wc.getOptions().setJavaScriptEnabled(true); //启用JS解释器，默认为true
        wc.getOptions().setCssEnabled(false); //禁用css支持
        wc.getCookieManager().setCookiesEnabled(false);
        wc.getOptions().setThrowExceptionOnScriptError(false); //js运行错误时，是否抛出异常
        wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
        wc.getOptions().setTimeout(10000); //设置连接超时时间 ，这里是10S。如果为0，则无限期等待

        wc.waitForBackgroundJavaScript(600*1000);
        wc.setAjaxController(new NicelyResynchronizingAjaxController());

        wc.waitForBackgroundJavaScript(1000*3);
        wc.setJavaScriptTimeout(0);
    }

    private static void getDataFromWeb(String category) throws Exception{
        System.out.println(Url);
//        originStr = Utils.streamToString(Utils.getUrlStream(Url));
//        System.out.println(originStr);

        HtmlPage page = wc.getPage(Url);
//        System.out.println(page);
        String pageXml = page.asXml(); //以xml的形式获取响应文本
//        doc = Jsoup.connect(Url).get();
        //System.out.println(pageXml);
        doc = Jsoup.parse(pageXml);
        //System.out.println(doc);
    }

    private static void getForm(){

    }
}
