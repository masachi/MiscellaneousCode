import Utils.Utils;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sdlds on 2016/8/29.
 */
public class ProblemGet {
    public static void main(String[] args) throws Exception{
        Scanner reader =new Scanner(System.in);
        System.out.println("请输入题目网址......");
        String URL = reader.nextLine();
        String originstr =Utils.streamToString(Utils.getUrlStream(URL));
        originstr = originstr.replaceAll("\n", "");
        originstr = originstr.replaceAll("\r", "");
        Pattern p = Pattern.compile("http://codeforces.com/problemset/problem/(.*?)");
        Matcher m = p.matcher(originstr);
        String problem = "";
        while(m.find()){
             problem = m.group();
        }
        String[] split_problem = problem.split("/");
        String Status_URL = "http://codeforces.com/problemset/status/"+split_problem[0]+"/problem/"+split_problem[1];
        String status_originstr = Utils.streamToString(Utils.getUrlStream(Status_URL));
    }
}
