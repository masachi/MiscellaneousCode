package AutoLogin;
import Encrypt.Base64Encrypt;
//import Utils.Down;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sdlds on 2016/7/4.
 */
public class AutoLoginToSomething {
    private static  String responseCookie;

    public static void main(String[] args) throws Exception{
        try {
            //String username = "MrZhang";
            //String password = "******";
            String username = "1304010330";
            String password = "MzIwNjgxMTk5NDExMTExNDMx";
            post(WebInput(username, password));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private static List<NameValuePair> WebInput(String username,String password_encoded) throws Exception{
        //String password = new String(Base64Encrypt.decodeBase64(password_encoded));
        String password = password_encoded;
        System.out.println(password);
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("j_username",username));
        list.add(new BasicNameValuePair("j_password",password));
        //list.add(new BasicNameValuePair("global_username",username));
        //list.add(new BasicNameValuePair("global_password",password_encoded));
        return list;
    }

    private static void post(List<NameValuePair> list) throws Exception{
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://202.118.201.228/academic/j_acegi_security_check");
        //HttpPost httpPost = new HttpPost("http://acm.hrbust.edu.cn/index.php?m=User&amp;a=login");
        //<form name="globalformLogin" id="globalformLogin" action="/index.php?m=User&amp;a=login" method="POST"></form>
        HttpResponse httpResponse = httpClient.execute(httpPost);
        String COOKIE = httpResponse.getFirstHeader("Set-Cookie").getValue();
        System.out.println(COOKIE);
        Pattern p = Pattern.compile("(.*?); Path");
        Matcher m = p.matcher(COOKIE);
        while(m.find()){
            COOKIE = m.group(1);
            System.out.println(COOKIE);
        }
        UrlEncodedFormEntity urlEncodedFormEntity;
        //Down.downloadFile();
        Scanner reader = new Scanner(System.in);
        String capture = reader.nextLine();
        list.add(new BasicNameValuePair("j_captcha",capture));
        list.add(new BasicNameValuePair("login",""));
        urlEncodedFormEntity = new UrlEncodedFormEntity(list,"UTF-8");
        httpPost.setHeader("Cookie",COOKIE);
        httpPost.setEntity(urlEncodedFormEntity);
        System.out.println("处理中......."+httpPost.getURI());
        CloseableHttpResponse response = httpClient.execute(httpPost);
        HttpEntity httpEntity = response.getEntity();
        StringBuilder result = new StringBuilder();
        //String cookie = response.getFirstHeader("Set-Cookie").getValue();
        //System.out.println(cookie);
        System.out.println(response.getStatusLine().getStatusCode());
        //System.out.println(response.getAllHeaders().toString());
        Header[] headers = response.getHeaders("Location");
        System.out.println(headers[0]);
        if(httpEntity!=null){
            System.out.println("Response"+ EntityUtils.toString(httpEntity));
            System.out.println(EntityUtils.toString(httpEntity).length());
        }
        System.out.println(result);
        //HttpGet httpGet = new HttpGet("http://202.118.201.228/academic/index_new.jsp");
        //HttpGet httpGet = new HttpGet(headers[0].toString().split("Location:")[0]);
        //httpGet.addHeader("Connection","keep-alive");
        //httpGet.addHeader("Cache-Control","max-age=0");
        //httpGet.addHeader("Upgrade-Insecure-Requests","1");
        //httpGet.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.79 Safari/537.36");
        //httpGet.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        //httpGet.addHeader("Cookie",cookie);
        //httpGet.addHeader("Cookie",cookie);
        //response = httpClient.execute(httpGet);
        //httpEntity = response.getEntity();
        //if(httpEntity!=null){
        //    System.out.println("Response"+ EntityUtils.toString(httpEntity));
        //}
        //System.out.println(EntityUtils.toString(httpEntity).length());
        response.close();
        /*
        <form action="/academic/j_acegi_security_check" method="post" id="login">
        <ul>
        <li><span>用户名</span><input type="text" name="j_username" class="input"></li>
        <li><span>密&nbsp;&nbsp;&nbsp;&nbsp;码</span><input type="password" name="j_password" class="input"></li>
        <li style="height:auto;"><span>验证码</span><input type="text" name="j_captcha" class="input">
        <img name="jcaptcha" id="jcaptcha" onclick="refresh_jcaptcha(this)" src="/academic/getCaptcha.do" alt="点击刷新验证码" title="点击刷新验证码" style="cursor:pointer;margin-left:66px;" border="1">
        <script language="Javascript">
                function refresh_jcaptcha(obj) {
            obj.src = "/academic/getCaptcha.do?" + Math.random();
        }
        </script></li>
        </ul>
        <div class="button"><input type="submit" name="login" value="" class="button"></div>
        </form>
        */
    }
}
