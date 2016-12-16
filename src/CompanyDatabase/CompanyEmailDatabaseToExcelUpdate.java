package CompanyDatabase;

import Model.Cookies;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.host.file.File;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.poi.ss.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import static CompanyDatabase.MultiThread.WriteExcel;

/**
 * Created by sdlds on 2016/12/15.
 */
public class CompanyEmailDatabaseToExcelUpdate implements Runnable {
//    private static String excelPath = "D:\\company_database_masachi.xlsx";
    private Sheet sheet;
//    private static Workbook wb;
//    private static Row row = null;
//    private static int colnum = 0;
//    private static int num = 0;
//    private static int proxynum = 0;
//    private static String originStr;
    private static Document doc = null;
    private WebClient wc;
//    private static int page = 1;
//    private static int wrong = 0;
//    private static List<Cookie> cookie = new ArrayList<>();
//    private static List<Cookies> cookies = new ArrayList<>();
//    private static JLabel label2;
//    private static JTextArea row1;
//    private static JTextArea row2;
//    private static JLabel label;
//    private static JTextArea cate;
//    private static int emailnum = 0;
//    private static JTextArea origin;
    private static Random random = new Random();
//    private static String UA_PATH = "file/user_agents";
    private static String URL = "http://directory.stclassifieds.sg/singapore-directory/";
    private static String query = "/q/";
    private Map<Integer, String> excel = new HashMap<>();
    private List<String> ua = new ArrayList<>();
    private int j;
    private static String company;
    private int end;
    private CountDownLatch signal;

    public CompanyEmailDatabaseToExcelUpdate(Sheet sheet,Map<Integer,String> excel,List<String> ua,int j,int end,CountDownLatch signal){
        this.sheet = sheet;
        this.excel = excel;
        this.ua = ua;
        this.j = j;
        this.end = end;
        this.signal = signal;
    }

    @Override
    public void run() {
        try {
            //ReadExcel();
            //ReadUA();

            System.out.println(Thread.currentThread().getName()+"开始处理数据!!!!!!!");

            signal.countDown();
            SetBrowser();

            for (int i = j; i < end; i++) {
                company = excel.get(j).replaceAll("Ltd|LTD|Pte|PTE|\\.|S'pore|\\(|\\)|'", "").replace("Engrg", "Engineering").trim().toLowerCase();
                String url = URL + company.replace(" ", "+") + query;
                if (sheet.getRow(j - 1).getCell(11) == null || sheet.getRow(j - 1).getCell(11).toString().equals("")) {
                    getDataFromWeb(url);
                } else {
                    System.out.println("Already!" + "-----" + j);
                }
                j++;
//                if (j % 20 == 0) {
//                    OutputExcel();
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //OutputExcel();
        }
    }

//    private static void ReadUA() {
//        java.io.File file = new java.io.File(UA_PATH);
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(new FileReader(file));
//            String temp = "";
//            while ((temp = reader.readLine()) != null) {
//                ua.add(temp);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void ReadExcel() throws Exception {
//        FileInputStream fileInputStream = new FileInputStream(excelPath);
////        POIFSFileSystem ts = new POIFSFileSystem(fileInputStream);
//        wb = WorkbookFactory.create(fileInputStream);
//        sheet = wb.getSheetAt(0);
//        int totalrow = sheet.getLastRowNum();
//        for (int i = 3; i <= totalrow; i++) {
//            excel.put(i + 1, sheet.getRow(i).getCell(5).toString());
//        }
//        //System.out.println(row.getCell(5));
//    }

    private void SetBrowser() {
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

//        wc.getCookieManager().addCookie(new Cookie());
        wc.addRequestHeader("User-Agent", ua.get(random.nextInt(9800)));

        wc.getOptions().setTimeout(50000);
    }

//    private static void OutputExcel() {
//        try {
//            FileOutputStream fileOutputStream = new FileOutputStream(excelPath);
//            wb.write(fileOutputStream);
//            fileOutputStream.flush();
//            fileOutputStream.close();
//            System.out.println("Success");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void getDataFromWeb(String Url) throws Exception {
        System.out.println(j+"+++++"+Url);
        HtmlPage page = wc.getPage(Url);
////        System.out.println(page);
//        String pageXml = page.asXml(); //以xml的形式获取响应文本
//        doc = Jsoup.connect(Url).get();
        //System.out.println(pageXml);
        String pageXml = page.asXml();
        doc = Jsoup.parse(pageXml);
        //System.out.println(doc);
        getEmail();
    }

    private void getEmail() {
        String email = "";
        String companyName = "";
        Elements sr1 = doc.select(".sr");
        Elements sr = sr1.select(".sr-results");
        if (sr == null) {
            WriteExcel(j - 1, email);
            return;
        }
        for (Element ele : sr) {
            companyName = ele.select(".sr-title").select("span").select("h2").text().toLowerCase();
            email = ele.select(".sr-details").select("p:contains(@)").text();
            if (companyName.contains(company) && !email.equals("")) {
                WriteExcel(j - 1, email);
                return;
            }
        }
        if (email.equals("")) {
            WriteExcel(j - 1, email);
        }
    }

//    private static void WriteExcel(int rownum, String email) {
//        email = email.replace("\r|\n", "").trim();
//        row = sheet.getRow(rownum);
//        Cell cell;
//        cell = row.createCell(11);
//        cell.setCellValue(email);
//        System.out.println("YES" + "---" + String.valueOf(rownum + 1) + "----" + email);
//    }


}
