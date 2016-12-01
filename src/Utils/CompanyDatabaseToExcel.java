package Utils;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFFontFormatting;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.formula.functions.Intercept;
import org.apache.poi.ss.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sdlds on 2016/11/29.
 */
public class CompanyDatabaseToExcel {
    private static String URL_HEAD = "http://www.yellowpages.com.sg/company/";
    private static String path = "D:\\Company Database";
    private static String excelPath = "D:\\company_database_masachi.xlsx";
    private static String proxyPath = "D:\\proxy.txt";
    private static Sheet sheet;
    private static Workbook wb;
    private static Row row = null;
    private static int rownum = 108;
    private static int colnum = 0;
    private static int proxynum = 0;
    private static String originStr;
    private static Document doc = null;
    private static WebClient wc;
    private static ArrayList<String> proxyList = new ArrayList<>();
    private static ArrayList<Integer> proxyPort = new ArrayList<>();
    private static String companyNameOrigin;
    private static String phoneOrigin;

    public static void main(String args[]){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HideMe();
                    ReadExcel();

                    SetWebClient();

                    File folder = new File(path);
                    File[] companyFile = folder.listFiles();
                    for (int i = 0; i < companyFile.length; i++) {
                        if (companyFile[i].isFile()) {
                            getFile(companyFile[i].getAbsolutePath());
                        }
                    }

                    OutputExcel();
                    System.exit(0);
                }
                catch (Exception e){
                    e.printStackTrace();
                    OutputExcel();
                    System.exit(-1);
                }
            }
        }).start();
    }

    private static void HideMe() throws Exception{
        Scanner reader1 = new Scanner(new File(proxyPath));
        while(reader1.hasNextLine()){
            String proxy = reader1.nextLine();
            proxyList.add(proxy.split(" ")[0].trim());
            proxyPort.add(Integer.parseInt(proxy.split(" ")[1].trim()));
        }
    }

    private static void getFile(String filePath) throws Exception{
        Scanner reader = new Scanner(new File(filePath));
        while(reader.hasNextLine()){
            String originStr = reader.nextLine().trim();
            phoneOrigin = originStr.substring(originStr.length()-9,originStr.length()).replace(" ","");
            String companyName = originStr.substring(0,originStr.length()-9).replace("."," ").trim();
            companyNameOrigin = companyName;
            System.out.println(companyNameOrigin+"  "+phoneOrigin);
            companyName = replaceSpaceToBash(companyName);
            getDataFromWeb(companyName);
//            Thread.sleep(10000);
        }
    }

    private static void getDataFromWeb(String companyName) throws Exception{
        String Url = URL_HEAD + companyName;
        System.out.println(Url);
//        originStr = Utils.streamToString(Utils.getUrlStream(Url));
//        System.out.println(originStr);

        wc.getOptions().setJavaScriptEnabled(true); //启用JS解释器，默认为true
        wc.getOptions().setCssEnabled(false); //禁用css支持
//        wc.getOptions().setProxyConfig(new ProxyConfig("72.159.158.210",3128));
        wc.getCookieManager().setCookiesEnabled(false);
        wc.getOptions().setThrowExceptionOnScriptError(false); //js运行错误时，是否抛出异常
        wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
        wc.getOptions().setTimeout(10000); //设置连接超时时间 ，这里是10S。如果为0，则无限期等待

        wc.waitForBackgroundJavaScript(600*1000);
        wc.setAjaxController(new NicelyResynchronizingAjaxController());

        HtmlPage page = wc.getPage(Url);
        wc.waitForBackgroundJavaScript(1000*3);
        wc.setJavaScriptTimeout(0);
//        System.out.println(page);
        String pageXml = page.asXml(); //以xml的形式获取响应文本
//        doc = Jsoup.connect(Url).get();
        doc = Jsoup.parse(pageXml);
        //System.out.println(doc);
        getData(companyName);
    }

    private static void SetWebClient(){
        wc = new WebClient(BrowserVersion.CHROME,proxyList.get(proxynum),proxyPort.get(proxynum));
    }

    private static void ReadExcel() throws Exception{
        FileInputStream fileInputStream = new FileInputStream(excelPath);
//        POIFSFileSystem ts = new POIFSFileSystem(fileInputStream);
        wb = WorkbookFactory.create(fileInputStream);
        sheet = wb.getSheetAt(0);
        row = sheet.getRow(5);
        //System.out.println(row.getCell(5));
    }

    private static void OutputExcel(){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(excelPath);
            wb.write(fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            System.out.println("Success");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private static String replaceSpaceToBash(String companyName){
        companyName = companyName.trim();
        companyName = companyName.replace(" & ","-").replace("(","").replace(")","").replace("\'"," ").replace("&"," ").trim();
//        companyName = companyName.replace(". "," ").replace("."," ").trim();
        companyName = companyName.replace(" ","-").trim();
        companyName = companyName.toLowerCase().trim();
        return companyName;
    }

    private static void getData(String companyName){
//        Pattern name = Pattern.compile("<div class=\"col-md-8\"><h1>(.*?)</h1></div><div class=\"col-md-4\">");
//        Pattern addr = Pattern.compile("<div class=\"row com_address\"><p><span>(.*?)</span></p></div>");
//        Pattern phone = Pattern.compile("");
//
//        Matcher m_name = name.matcher(originStr);
//
//        while(m_name.find()){
//            System.out.println(m_name.group()+"23333333");
//        }
//        System.out.println(doc.getElementsByTag("title").text());
        try {
            String title = doc.getElementsByTag("title").text().split("\\|")[1];
            if (title.equals(" Internet Yellow Pages ")) {
                System.out.println(companyNameOrigin);
                System.out.println(doc.getElementsByTag("title").text().split("\\|")[0]);
                WriteExcel("",companyNameOrigin,"","",phoneOrigin,"");
                return;
            }
            //System.out.println(title);
            String name = doc.getElementsByClass("com_name").select("h1").text();
            //System.out.println(name);
            String addr1 = doc.getElementsByClass("com_address").select("span").text();
//        System.out.println(addr.substring(0,addr.length()-7));
            String addr = addr1.substring(0, addr1.length() - 7);
//        System.out.println(addr.substring(addr.length()-6,addr.length()));
            String zip = addr1.substring(addr1.length() - 6, addr1.length());
            String tele_head = doc.getElementsByClass("telephone").select("div").text();
            String tele = "";
            if (!tele_head.equals("")) {
                String tele_tail = doc.getElementsByClass("telephone").select("div").toString();
                Pattern p = Pattern.compile("data-last=\"(\\d+)\"");
                Matcher m = p.matcher(tele_tail);
                //System.out.println(tele_tail);
                while (m.find()) {
                    //System.out.println(m.group(1));
                    tele = m.group(1);
                }
                tele = tele_head.replaceAll("\\s+", "").replace(" ", "").substring(0, 4) + tele;
            }
            String fax = "";
            //System.out.println(tele);
            String fax_head = doc.getElementsByClass("fax").select("div").text();
            if (!fax_head.equals("")) {
                String fax_tail = doc.getElementsByClass("fax").select("div").toString();
                Pattern p1 = Pattern.compile("data-last=\"(\\d+)\"");
                Matcher m1 = p1.matcher(fax_tail);
                //System.out.println(fax_tail);
                while (m1.find()) {
                    fax = m1.group(1);
                }
                fax = fax_head.replaceAll("\\s+", "").replace(" ", "").substring(0, 4) + fax;
                //System.out.println(fax);
            }

            System.out.println(title + "," + name + "," + addr + "," + zip + "," + tele + "," + fax);

            WriteExcel(title, name, addr, zip, tele, fax);
        }
        catch (Exception e){
            e.printStackTrace();
            if(e instanceof ArrayIndexOutOfBoundsException){
                proxynum++;
                try{
                    SetWebClient();
                    getDataFromWeb(companyName);
                    return;
                }
                catch (Exception e1){
                    e1.printStackTrace();
                }
            }
        }
    }

    private static void WriteExcel(String title,String name,String addr,String zip,String phone,String fax) throws Exception{
        row = sheet.createRow(rownum);
        String source = "EYP2016" + title;
        Cell cell = null;
        cell = row.createCell(1);
        cell.setCellValue(source);
        cell = row.createCell(2);
        cell.setCellValue("SG");
//        Calendar cal = Calendar.getInstance();
//        String[] month = new String[] {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
//        String date_tmp = cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.DAY_OF_MONTH);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/mm/dd");
//        Date date = dateFormat.parse(date_tmp);
//        System.out.println(date);
//        cell = row.createCell(3);
//        cell.setCellValue(date);
        cell = row.createCell(5);
        cell.setCellValue(name);
        cell = row.createCell(6);
        cell.setCellValue(addr);
        if(!zip.equals("")){
            cell = row.createCell(7);
            cell.setCellValue(Integer.parseInt(zip));
        }
        if(!phone.equals("")) {
            cell = row.createCell(8);
            cell.setCellValue(Integer.parseInt(phone));
        }
        if(!fax.equals("")) {
            cell = row.createCell(10);
            cell.setCellValue(Integer.parseInt(fax));
        }
        System.out.println("YES"+"---"+String.valueOf(rownum));
        rownum++;
        colnum++;
    }
}
