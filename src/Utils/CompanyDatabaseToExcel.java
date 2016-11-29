package Utils;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sdlds on 2016/11/29.
 */
public class CompanyDatabaseToExcel {
    private static String URL_HEAD = "http://www.yellowpages.com.sg/company/";
    private static String path = "D:\\Company Database";
    private static String excelPath = "D:\\company_database_masachi.xlsx";
    private static Sheet sheet;
    private static Workbook wb;
    private static Row row = null;
    private static int rownum = 11;
    private static int colnum = 0;
    private static String originStr;
    private static Document doc = null;

    public static void main(String args[]) throws Exception{
        ReadExcel();

        File folder = new File(path);
        File[] companyFile = folder.listFiles();
        for(int i=0;i<companyFile.length;i++){
            if(companyFile[i].isFile()){
                getFile(companyFile[i].getAbsolutePath());
            }
        }

        OutputExcel();
    }

    private static void getFile(String filePath) throws Exception{
        Scanner reader = new Scanner(new File(filePath));
        while(reader.hasNextLine()){
            String companyName = reader.nextLine();
            companyName = replaceSpaceToBash(companyName);
            getDataFromWeb(companyName);
        }
    }

    private static void getDataFromWeb(String companyName) throws Exception{
        String Url = URL_HEAD + companyName;
        System.out.println(Url);
//        originStr = Utils.streamToString(Utils.getUrlStream(Url));
//        System.out.println(originStr);
        WebClient wc = new WebClient(BrowserVersion.EDGE);

        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");
        java.util.logging.Logger.getLogger("net.sourceforge.htmlunit").setLevel(java.util.logging.Level.OFF);

        wc.getOptions().setJavaScriptEnabled(true); //启用JS解释器，默认为true
        wc.getOptions().setCssEnabled(false); //禁用css支持
        wc.getOptions().setThrowExceptionOnScriptError(false); //js运行错误时，是否抛出异常
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
        System.out.println(doc);
        getData();
    }

    private static void ReadExcel() throws Exception{
        FileInputStream fileInputStream = new FileInputStream(excelPath);
//        POIFSFileSystem ts = new POIFSFileSystem(fileInputStream);
        wb = WorkbookFactory.create(fileInputStream);
        sheet = wb.getSheetAt(0);
        row = sheet.getRow(5);
        //System.out.println(row.getCell(5));
    }

    private static void OutputExcel() throws IOException{
        FileOutputStream fileOutputStream = new FileOutputStream(excelPath);
        wb.write(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    private static String replaceSpaceToBash(String companyName){
        companyName = companyName.replace(" & ","-").replace("(","").replace(")","");
        companyName = companyName.replace(".","");
        companyName = companyName.replace(" ","-");
        companyName = companyName.toLowerCase();
        return companyName;
    }

    private static void getData() throws Exception{
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
        String title = doc.getElementsByTag("title").text().split("\\|")[1];
        if(title.equals(" Internet Yellow Pages ")){
            return;
        }
        System.out.println(title);
        String name = doc.getElementsByClass("com_name").select("h1").text();
        System.out.println(name);
        String addr1 = doc.getElementsByClass("com_address").select("span").text();
//        System.out.println(addr.substring(0,addr.length()-7));
        String addr = addr1.substring(0,addr1.length()-7);
//        System.out.println(addr.substring(addr.length()-6,addr.length()));
        String zip = addr1.substring(addr1.length()-6,addr1.length());
        String tele_head = doc.getElementsByClass("telephone").select("div").text();
        String tele_tail = doc.getElementsByClass("telephone").select("div").toString();
        Pattern p = Pattern.compile("data-last=\"(\\d+)\"");
        Matcher m = p.matcher(tele_tail);
        //System.out.println(tele_tail);
        String tele = "";
        while(m.find()){
            //System.out.println(m.group(1));
            tele = m.group(1);
        }
        tele = tele_head.replaceAll("\\s+","").replace(" ","").substring(0,4) + tele;
        String fax = "";
        System.out.println(tele);
//        if(doc.getElementsByClass("fax") != null){
//            String fax_head = doc.getElementsByClass("fax").select("div").text();
//            String fax_tail = doc.getElementsByClass("fax").select("div").toString();
//            Pattern p1 = Pattern.compile("data-last=\"(\\d+)\"");
//            Matcher m1 = p1.matcher(fax_tail);
//            System.out.println(fax_tail);
//            while(m1.find()){
//                fax = m.group(1);
//            }
//            fax = fax_head.replaceAll("\\s+","").replace(" ","").substring(0,4) + fax;
//            System.out.println(fax);
//        }

        WriteExcel(title,name,addr,zip,tele);
    }

    private static void WriteExcel(String title,String name,String addr,String zip,String phone) throws Exception{
        row = sheet.getRow(rownum);
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
        cell = row.createCell(7);
        cell.setCellValue(Integer.parseInt(zip));
        cell = row.createCell(8);
        cell.setCellValue(Integer.parseInt(phone));
        rownum++;
    }
}
