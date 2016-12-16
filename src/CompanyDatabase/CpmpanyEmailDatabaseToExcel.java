package CompanyDatabase;

import Model.Cookies;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.apache.poi.ss.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sdlds on 2016/12/14.
 */
public class CpmpanyEmailDatabaseToExcel {
    private static String excelPath = "D:\\company_database_masachi.xlsx";
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
    private static JTextArea row2;
    private static JLabel label;
    private static JTextArea cate;
    private static int emailnum = 0;
    private static JTextArea origin;
    private static Random random = new Random();


    public static void main(String[] args){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    ReadExcel();
                    SetBrowser();

                    JFrame frame = new JFrame("H Pages");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setSize(800,600);
                    frame.setLayout(new GridLayout(1,2));
                    Container c = frame.getContentPane();
                    JPanel panel1 = new JPanel(new GridLayout(5,2));
                    origin = new JTextArea();
                    origin.setSize(800,400);
                    origin.setLineWrap(true);
//                    panel.add(origin, FlowLayout.LEFT);
                    JScrollPane pane = new JScrollPane(origin);
                    c.add(pane);
                    label = new JLabel();
                    label.setText("邮箱");
                    panel1.add(label);
                    cate = new JTextArea();
                    cate.setSize(50,50);
                    panel1.add(cate);
//                    panel.add(cate,FlowLayout.LEFT);
//                    frame.add(cate,FlowLayout.LEADING);
                    JLabel label1 = new JLabel();
                    label1.setText("公司");
                    panel1.add(label1);
                    row1 = new JTextArea();
                    row1.setSize(50,100);
                    panel1.add(row1);
//                    row.setVisible(true);
//                    panel.add(row,FlowLayout.LEFT);
//                    frame.add(row,FlowLayout.LEADING);
                    JLabel label3 = new JLabel();
                    label3.setText("行数");
                    panel1.add(label3);
                    row2 = new JTextArea();
                    row2.setSize(50,100);
                    panel1.add(row2);
                    JButton button2 = new JButton();
                    button2.setText("显示Email");
                    button2.setVisible(true);
                    button2.setSize(20,20);
                    panel1.add(button2);
                    JLabel label4 = new JLabel();
                    label4.setText("");
                    panel1.add(label4);
                    button2.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try{
                                //getDataFromWeb();
                                getDataFromLocal();
                            }
                            catch (Exception e1){
                                e1.printStackTrace();
                            }
                            int row2num = Integer.parseInt(row2.getText());
                            row1.setText(sheet.getRow(Integer.parseInt(row2.getText())-1).getCell(5).toString()+ "\n" +sheet.getRow(Integer.parseInt(row2.getText())-1).getCell(8).toString());
                            label4.setText("输出完成");
                            frame.repaint();
                        }
                    });
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
                            if(!cate.getText().equals("") && !row1.getText().equals("")){
                                try {
                                    rownum = Integer.parseInt(row2.getText()) -1;
                                    emailnum++;
                                    WriteExcel(cate.getText());
                                    label2.setText("输出" + emailnum + "条");
                                    frame.repaint();
                                    OutputExcel();
                                }
                                catch (Exception e1){
                                    e1.printStackTrace();
                                }
                            }
                            else{
                                label2.setText("空");
                                rownum = Integer.parseInt(row2.getText()) -1;
                                emailnum++;
                                WriteExcel(cate.getText());
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

    private static void getDataFromLocal() throws Exception{
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
        String pageXml = origin.getText();
        doc = Jsoup.parse(pageXml);
        //System.out.println(doc);
        getEmailLocal(doc.toString());
    }

    private static void getDataFromWeb() throws Exception{
        String Url = cate.getText();
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
        getContact(doc.toString());
    }

    private static void getContact(String page) throws Exception{
//        page = page.replace("\r|\n","");
        String contact = "";
//        Pattern p1 = Pattern.compile("<a href=\"(.*?)\"> Contact </a> | <a href=\"(.*?)\"> Contact Us </a> | <a href=\"(.*?)\"> contact </a> | <a href=\"(.*?)\"> contact us </a>");
//        Pattern p2 = Pattern.compile("<a href=\"(.*?)\"> Contact Us </a>");
//        Pattern p3 = Pattern.compile("<a href=\"(.*?)\"> contact </a>");
//        Pattern p4 = Pattern.compile("<a href=\"(.*?)\"> contact us </a>");
//
//        Matcher m1 = p1.matcher(page);
//        while(m1.find()){
//            contact = m1.group(1);
//            System.out.println(contact);
//        }
//        contact = cate.getText() + contact;
//        System.out.println(contact);
        Elements url = doc.select("a");
        for(Element ele : url){
            String temp = ele.attr("href");
            if(temp.contains("Contact") || temp.contains("contact") || temp.contains("contact_us") || temp.contains("Contact_Us")){
                contact = ele.attr("href");
                break;
            }
        }
        contact = cate.getText() + contact;
        System.out.println(contact);
        getEmail(contact);
    }

    private static void getEmail(String contact) throws Exception{
        if(!contact.equals("")) {
            HtmlPage page = wc.getPage(contact);
//        System.out.println(page);
            String pageXml = page.asXml(); //以xml的形式获取响应文本
            System.out.println(pageXml);
            Pattern p_e = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
            Matcher m_e = p_e.matcher(pageXml);

            String email = "";

            while (m_e.find()) {
                if (email.equals("")) {
                    email = email + m_e.group();
                } else {
                    email = email + "\n" + m_e.group();
                }
            }

            System.out.println(email);

            origin.setText(email);
        }
    }

    private static void getEmailLocal(String page) throws Exception{
//        System.out.println(page);
            Pattern p_e = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
            Matcher m_e = p_e.matcher(page);

            String email = "";

            while (m_e.find()) {
                if (email.equals("")) {
                    email = email + m_e.group();
                } else {
                    email = email + "\n" + m_e.group();
                }
            }

            System.out.println(email);

            cate.setText(email);
    }

    private static void WriteExcel(String email) {
        email = email.replace("\r|\n","").trim();
        row = sheet.getRow(rownum);
        Cell cell = row.createCell(11);
        cell.setCellValue(email);
        System.out.println("YES"+"---"+String.valueOf(rownum));
        rownum++;
        colnum++;
        row2.setText(rownum + 1 +"");
        int row2num;
        if(email.equals("")){
            row2num = Integer.parseInt(row2.getText());
        }
        else{
            row2num = Integer.parseInt(row2.getText()) + random.nextInt(4);
        }
        row1.setText(sheet.getRow(row2num-1).getCell(5).toString());
        row2.setText(row2num+"");
        cate.setText("");
        origin.setText("");
    }
}
