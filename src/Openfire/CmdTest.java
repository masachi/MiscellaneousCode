package Openfire;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Created by Masachi on 2017/3/31.
 */
public class CmdTest {
    public static void main(String[] args){
        try{
            Process p = Runtime.getRuntime().exec("http 127.0.0.1:9090");  //调用Linux的相关命令

            InputStreamReader ir = new InputStreamReader(p.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);//创建IO管道，准备输出命令执行后的显示内容

            String line;
            while ((line = input.readLine ()) != null){     //按行打印输出内容
                System.out.println(line);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
