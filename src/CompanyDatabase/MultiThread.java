package CompanyDatabase;

import Model.Cookies;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.apache.poi.ss.usermodel.*;
import org.jsoup.nodes.Document;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Created by sdlds on 2016/12/16.
 */
public class MultiThread {
    private static String excelPath = "D:\\company_database_masachi.xlsx";
    private static Sheet sheet;
    private static Workbook wb;
    private static Row row = null;
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
    private static String UA_PATH = "file/user_agents";
    private static String URL = "http://directory.stclassifieds.sg/singapore-directory/";
    private static String query = "/q/";
    private static Map<Integer, String> excel = new HashMap<>();
    private static List<String> ua = new ArrayList<>();
    private static int j = 60;
    private static String company;
    private static CountDownLatch signal = new CountDownLatch(20);

    public static void main(String[] args) {
        try {
            ReadExcel();
            ReadUA();

            int start = 4;
            int end;

            for(int i=0;i<20;i++){
                end = start + 1000;
                new Thread(new CompanyEmailDatabaseToExcelUpdate(sheet,excel,ua,start,end,signal)).start();
                start = end;
            }

//            signal.await();
//            OutputExcel();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void ReadUA() {
        java.io.File file = new java.io.File(UA_PATH);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String temp = "";
            while ((temp = reader.readLine()) != null) {
                ua.add(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void ReadExcel() throws Exception {
        FileInputStream fileInputStream = new FileInputStream(excelPath);
//        POIFSFileSystem ts = new POIFSFileSystem(fileInputStream);
        wb = WorkbookFactory.create(fileInputStream);
        sheet = wb.getSheetAt(0);
        int totalrow = sheet.getLastRowNum();
        for (int i = 3; i <= totalrow; i++) {
            excel.put(i + 1, sheet.getRow(i).getCell(5).toString());
        }
        System.out.println(excel.size());
        //System.out.println(row.getCell(5));
    }

    public static synchronized void WriteExcel(int rownum, String email) {
        email = email.replace("\r|\n", "").trim();
        row = sheet.getRow(rownum);
        Cell cell;
        cell = row.createCell(11);
        cell.setCellValue(email);
        System.out.println("YES" + "---" + String.valueOf(rownum + 1) + "----" + email);
        OutputExcel();
    }

    private static void OutputExcel() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(excelPath);
            wb.write(fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            System.out.println("Success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
