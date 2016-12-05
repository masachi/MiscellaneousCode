package Utils;

import Model.Cookies;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.host.file.FileList;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.apache.poi.ss.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sdlds on 2016/12/2.
 */
public class CompanyDatabaseToExcelUpdate {
    private static String URL_HEAD  = "http://www.yellowpages.com.sg/companies/load/all/";
    private static String URL_PER = "?per=60";
    private static String URL_PAGE = "&page=";
    private static String URL_SORT = "&sort=all";

    private static String path = "D:\\Company Database";
    private static String excelPath = "D:\\company_database.xlsx";
    private static String cookiePath = "D:\\cookie.json";

    private static Sheet sheet;
    private static Workbook wb;
    private static Row row = null;
    private static int rownum = 454;
    private static int colnum = 0;
    private static int num = 0;
    private static int proxynum = 0;
    private static String originStr;
    private static Document doc = null;
    private static WebClient wc = new WebClient(BrowserVersion.CHROME);
    private static int page = 1;
    private static int wrong = 0;
    private static List<Cookie> cookie = new ArrayList<>();
    private static List<Cookies> cookies = new ArrayList<>();
    private static JLabel label2;
    private static JTextArea row1;

    public static void main(String[] args){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    ReadExcel();

                    JFrame frame = new JFrame("H Pages");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setSize(800,600);
                    frame.setLayout(new GridLayout(1,2));
                    Container c = frame.getContentPane();
                    JPanel panel1 = new JPanel(new GridLayout(3,2));
                    JTextArea origin = new JTextArea();
                    origin.setSize(800,400);
                    origin.setLineWrap(true);
//                    panel.add(origin, FlowLayout.LEFT);
                    JScrollPane pane = new JScrollPane(origin);
                    c.add(pane);
                    JLabel label = new JLabel();
                    label.setText("行业");
                    panel1.add(label);
                    JTextArea cate = new JTextArea();
                    cate.setSize(50,50);
                    panel1.add(cate);
//                    panel.add(cate,FlowLayout.LEFT);
//                    frame.add(cate,FlowLayout.LEADING);
                    JLabel label1 = new JLabel();
                    label1.setText("行数");
                    panel1.add(label1);
                    row1 = new JTextArea();
                    row1.setSize(50,100);
                    panel1.add(row1);
//                    row.setVisible(true);
//                    panel.add(row,FlowLayout.LEFT);
//                    frame.add(row,FlowLayout.LEADING);
                    JButton button = new JButton();
                    button.setText("输出到Excel");
                    button.setVisible(true);
                    button.setSize(20,20);
                    panel1.add(button);
                    label2 = new JLabel();
                    label2.setText("");
                    panel1.add(label2);
//                    panel.add(button,FlowLayout.LEFT);
                    c.add(panel1);
                    button.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if(!origin.getText().equals("") && !cate.getText().equals("") && !row1.getText().equals("")){
                                try {
                                    rownum = Integer.parseInt(row1.getText()) -1;
                                    originStr = cate.getText();
                                    getDataFromLocal(origin.getText(), cate.getText());
                                    frame.repaint();
                                }
                                catch (Exception e1){
                                    e1.printStackTrace();
                                }
                            }
                            else{
                                label2.setText("空");
                                frame.repaint();
                            }
                        }
                    });
                    frame.setVisible(true);
//                    HideMe();

//                    ReadCookie();
//
//                    ReadExcel();
////                    SetWebClient();
//
//                    SetBrowser();
//
//                    SetCookie();
//
//                    File folder = new File(path);
//                    File[] companyFile = folder.listFiles();
//                    for (int i = 0; i < companyFile.length; i++) {
//                        if (companyFile[i].isFile()) {
//                            getFile(companyFile[i].getAbsolutePath());
//                        }
//                    }



                    //OutputExcel();
                }
                catch (Exception e){
                    e.printStackTrace();
                    //OutputExcel();
                    System.exit(-1);
                }
            }
        }).start();
    }

    private static void ReadCookie() throws Exception{
        cookies = new Gson().fromJson(new JsonReader(new FileReader("cookie/cookie.json")),new TypeToken<List<Cookies>>(){}.getType());
    }

    private static void SetCookie(){
        for(int i=0;i<cookies.size();i++){
            Cookie cookieIns = new Cookie(cookies.get(i).getDomain(),cookies.get(i).getName(),cookies.get(i).getValue());
            wc.getCookieManager().addCookie(cookieIns);
        }
    }

    private static void ReadExcel() throws Exception{
        FileInputStream fileInputStream = new FileInputStream(excelPath);
//        POIFSFileSystem ts = new POIFSFileSystem(fileInputStream);
        wb = WorkbookFactory.create(fileInputStream);
        sheet = wb.getSheetAt(0);
        row = sheet.getRow(5);
        //System.out.println(row.getCell(5));
    }

    private static void SetBrowser(){
        wc.getOptions().setJavaScriptEnabled(true); //启用JS解释器，默认为true
        wc.getOptions().setCssEnabled(false); //禁用css支持
//        wc.getOptions().setProxyConfig(new ProxyConfig("185.10.17.134",3128));
        wc.getCookieManager().setCookiesEnabled(false);
        wc.getOptions().setThrowExceptionOnScriptError(false); //js运行错误时，是否抛出异常
        wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
        wc.getOptions().setTimeout(10000); //设置连接超时时间 ，这里是10S。如果为0，则无限期等待

        wc.waitForBackgroundJavaScript(600*1000);
        wc.setAjaxController(new NicelyResynchronizingAjaxController());

        wc.waitForBackgroundJavaScript(1000*3);
        wc.setJavaScriptTimeout(0);

//        wc.getCookieManager().addCookie(new Cookie());
        wc.addRequestHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        wc.addRequestHeader("Accept-Encoding","gzip, deflate, sdch");
        wc.addRequestHeader("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4");
        wc.addRequestHeader("Cache-Control","max-age=0");
        wc.addRequestHeader("Connection","keep-alive");
        wc.addRequestHeader("DNT","1");
        wc.addRequestHeader("Host","www.yellowpages.com.sg");
        wc.addRequestHeader("If-Modified-Since","Mon, 05 Dec 2016 03:43:37 +0000");
        wc.addRequestHeader("Upgrade-Insecure-Requests","1");
        wc.addRequestHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.59 Safari/537.36");
    }

    private static void getFile(String filePath) throws Exception{
        Scanner reader = new Scanner(new File(filePath));
        while(reader.hasNextLine()){
            originStr = reader.nextLine().trim();
            //String companyName = originStr.substring(0,originStr.length()-9).replace("."," ").trim();
            //System.out.println(companyNameOrigin+"  "+phoneOrigin);
            //companyName = replaceSpaceToBash(companyName);
            String category = replaceSpaceToAdd(originStr);
            wrong = 0;
            //getDataFromWeb(companyName);
//            Thread.sleep(10000);
            getDataFromWeb(category);
        }
    }

    private static String replaceSpaceToAdd(String category){
        category = category.trim().replace(" ","+").trim();
        return category;
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

    private static void getDataFromWeb(String category) throws Exception{
        String Url = URL_HEAD + category + URL_PER + URL_PAGE + page;
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
        getData(category);
    }

    private static void getDataFromLocal(String pageXml,String category) throws Exception{
//        String Url = URL_HEAD + category + URL_PER + URL_PAGE + page;
//        System.out.println(Url);
////        originStr = Utils.streamToString(Utils.getUrlStream(Url));
////        System.out.println(originStr);
//
//        HtmlPage page = wc.getPage(Url);
////        System.out.println(page);
//        String pageXml = page.asXml(); //以xml的形式获取响应文本
//        doc = Jsoup.connect(Url).get();
        //System.out.println(pageXml);
        doc = Jsoup.parse(pageXml);
        //System.out.println(doc);
        getData(category);
    }

    private static void getData(String category) throws Exception{
        if(doc.getElementsByClass("alert").text().contains("No Results Found")){
            System.out.println("This Category is finished");
            page = 1;
            return;
        }

        if(doc.getElementById("solarium_companies_did_you_mean") != null){
            System.out.println("This Category is Wrong");
            page = 1;
            return;
        }

        Elements companyDetailElements = doc.getElementsByClass("cmc_company_detail");
        if(companyDetailElements.size() == 0){
            System.out.println("This Category is finished");
            page = 1;
            return;
        }

        for(Element companyDetail : companyDetailElements){
            String name = companyDetail.getElementsByClass("box-head-left").select("a[title]").text().trim();
            String addr = companyDetail.getElementsByClass("address").text();
            String address = "";
            String zip = "";
            try {
                address = addr.substring(0, addr.length() - 7).trim();
                zip = addr.substring(addr.length() - 6, addr.length()).trim();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            String phone = companyDetail.getElementsByClass("show_number").select("a[href]").attr("href").replace("tel:","").trim();
            String cate = companyDetail.getElementsByClass("com_cats").select("a[href]").attr("href").replace("http://www.yellowpages.com.sg/category/","").trim();
            if(cate.equals(originStr.toLowerCase().replace("&","").replace("  "," ").replace(" ","-").trim())) {
                System.out.println(name + "," + address + "," + zip + "," + phone);
                num++;
                WriteExcel(category, name,address,zip,phone);
            }
            else{
                System.out.println(name + "," + address + "," + zip + "," + phone);
                System.out.println(cate + "     "+ originStr.toLowerCase().replace("&","").replace("  "," ").replace(" ","-").trim());
//                wrong++;
//                if(wrong > 60){
//                    System.out.println("This Category is finished");
//                    page = 1;
//                    return;
//                }
            }
        }

//        Elements companyNameElements = doc.getElementsByClass("box-head-left").select("a[title]");
//        Elements addressElements = doc.getElementsByClass("address");
//        Elements phoneElements = doc.getElementsByClass("show_number").select("a[href]");
//        Elements cateElements = doc.getElementsByClass("com_cats").select("a[href]");
//
//        List<String> companyNameList = new ArrayList<>();
//        List<String> addressList = new ArrayList<>();
//        List<String> zipList = new ArrayList<>();
//        List<String> phoneList = new ArrayList<>();
//        List<String> cateList = new ArrayList<>();
//
//        for(Element companyName : companyNameElements){
//            companyNameList.add(companyName.text().trim());
//        }
//
//        for(Element address : addressElements){
//            addressList.add(address.text().substring(0, address.text().length() - 7).trim());
//            zipList.add(address.text().substring(address.text().length() - 6, address.text().length()).trim());
//        }
//
//         for(Element phone : phoneElements){
//            phoneList.add(phone.attr("href").replace("tel:","").trim());
//         }
//
//        for(Element cate : cateElements){
//            cateList.add(cate.attr("href").replace("http://www.yellowpages.com.sg/category/","").trim());
//        }
//
//        System.out.println(companyNameList.size()+"------"+);
//
//        for(int j=0;j<companyNameList.size();j++){
//            if(cateList.get(j).contains(originStr.toLowerCase().replace(" ","-").trim())) {
//                System.out.println(companyNameList.get(j) + "," + addressList.get(j) + "," + zipList.get(j) + "," + phoneList.get(j));
//                WriteExcel(category, companyNameList.get(j), addressList.get(j), zipList.get(j), phoneList.get(j));
//            }
//            else{
//                System.out.println(companyNameList.get(j) + "," + addressList.get(j) + "," + zipList.get(j) + "," + phoneList.get(j));
//            }
//        }
        OutputExcel();
        String fk = "写了" + page + "次"+","+num+"条";
        page++;
        label2.setText(fk);
        rownum = Integer.parseInt(row1.getText()) + num;
        row1.setText(rownum+"");
        num = 0;
    }
    //把得到的数据写到excal
    private static void WriteExcel(String category,String name,String addr,String zip,String phone) {
        row = sheet.createRow(rownum);
        String source = "EYP2016 " + originStr;
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
            try {
                cell = row.createCell(7);
                cell.setCellValue(Integer.parseInt(zip));
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        if(!phone.equals("")) {
            try {
                cell = row.createCell(8);
                cell.setCellValue(Integer.parseInt(phone));
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        System.out.println("YES"+"---"+String.valueOf(rownum));
        rownum++;
        colnum++;
    }
}
