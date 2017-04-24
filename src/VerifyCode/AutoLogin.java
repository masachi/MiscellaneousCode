package VerifyCode;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.File;

/**
 * Created by Masachi on 2017/4/4.
 */
public class AutoLogin {
    private WebClient webClient = new WebClient(BrowserVersion.CHROME);
    private String URL = "http://202.118.201.228/homepage/index.do";
    private HtmlPage page;
    //http://202.118.201.228/academic/getCaptcha.do

    private void setWebClient(){
        webClient.getOptions().setJavaScriptEnabled(true); //启用JS解释器，默认为true
        webClient.getOptions().setCssEnabled(false); //禁用css支持
//        webClient.getOptions().setProxyConfig(new ProxyConfig("185.10.17.134",3128));
        webClient.getCookieManager().setCookiesEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false); //js运行错误时，是否抛出异常
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setTimeout(10000); //设置连接超时时间 ，这里是10S。如果为0，则无限期等待

        webClient.waitForBackgroundJavaScript(600*1000);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());

        webClient.waitForBackgroundJavaScript(1000*3);
        webClient.setJavaScriptTimeout(0);
    }

    public void loginToRemoteServer(String username,String password, String capture){
        try {
            setWebClient();
            getPage();
            HtmlElement usernameElement = page.getElementByName("j_username");
            HtmlElement passwordElement = page.getElementByName("j_password");
            HtmlElement captureElement = page.getElementByName("j_captcha");
            HtmlElement loginButton = page.getElementByName("login");
            HtmlImage captureImg = (HtmlImage) page.getElementByName("jcaptcha");
            // TODO: 2017/4/4 htmlimage下载成图片然后OCR
            captureImg.saveAs(new File("file/temp.jpg"));
            System.out.println(VerifyCode.getVerifyCode());
            usernameElement.focus();
            usernameElement.type(username);
            passwordElement.focus();
            passwordElement.type(password);
            captureElement.focus();
            captureElement.type(capture);
            //loginButton.click();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getPage(){
        try{
            page = webClient.getPage(URL);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
