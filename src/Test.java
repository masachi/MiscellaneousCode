import Encrypt.Base64Encrypt;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sdlds on 2016/9/26.
 */
public class Test {
    private static String type="[{\t\"name\": \"睦月型\",\t\"ctype\": 28}, {\t\"name\": \"千歳型\",\t\"ctype\": 15}, {\t\"name\": \"翔鶴型\",\t\"ctype\": 33}, {\t\"name\": \"夕張型\",\t\"ctype\": 34}, {\t\"name\": \"伊168型\",\t\"ctype\": 35}, {\t\"name\": \"綾波型\",\t\"ctype\": 1}, {\t\"name\": \"大和型\",\t\"ctype\": 37}, {\t\"name\": \"夕雲型\",\t\"ctype\": 38}, {\t\"name\": \"阿賀野型\",\t\"ctype\": 41}, {\t\"name\": \"大鳳型\",\t\"ctype\": 43}, {\t\"name\": \"香取型\",\t\"ctype\": 56}, {\t\"name\": \"あきつ丸型\",\t\"ctype\": 45}, {\t\"name\": \"まるゆ型\",\t\"ctype\": 46}, {\t\"name\": \"陽炎型\",\t\"ctype\": 30}, {\t\"name\": \"Bismarck型\",\t\"ctype\": 47}, {\t\"name\": \"Z1型\",\t\"ctype\": 48}, {\t\"name\": \"明石型\",\t\"ctype\": 49}, {\t\"name\": \"大淀型\",\t\"ctype\": 52}, {\t\"name\": \"大鯨型\",\t\"ctype\": 50}, {\t\"name\": \"龍鳳型\",\t\"ctype\": 51}, {\t\"name\": \"初春型\",\t\"ctype\": 10}, {\t\"name\": \"白露型\",\t\"ctype\": 23}, {\t\"name\": \"秋月型\",\t\"ctype\": 54}, {\t\"name\": \"U-511型\",\t\"ctype\": 57}, {\t\"name\": \"Graf Zeppelin型\",\t\"ctype\": 63}, {\t\"name\": \"呂500型\",\t\"ctype\": 57}, {\t\"name\": \"秋津洲型\",\t\"ctype\": 59}, {\t\"name\": \"Zara型\",\t\"ctype\": 64}, {\t\"name\": \"瑞穂型\",\t\"ctype\": 62}, {\t\"name\": \"速吸型\",\t\"ctype\": 60}, {\t\"name\": \"島風型\",\t\"ctype\": 22}, {\t\"name\": \"最上型\",\t\"ctype\": 9}, {\t\"name\": \"利根型\",\t\"ctype\": 31}, {\t\"name\": \"祥鳳型\",\t\"ctype\": 11}, {\t\"name\": \"飛鷹型\",\t\"ctype\": 24}, {\t\"name\": \"龍驤型\",\t\"ctype\": 32}, {\t\"name\": \"伊勢型\",\t\"ctype\": 2}, {\t\"name\": \"金剛型\",\t\"ctype\": 6}, {\t\"name\": \"長門型\",\t\"ctype\": 19}, {\t\"name\": \"赤城型\",\t\"ctype\": 14}, {\t\"name\": \"加賀型\",\t\"ctype\": 3}, {\t\"name\": \"鳳翔型\",\t\"ctype\": 27}, {\t\"name\": \"記念日夕雲型\",\t\"ctype\": 12}, {\t\"name\": \"蒼龍型\",\t\"ctype\": 17}, {\t\"name\": \"記念日瑞穂型\",\t\"ctype\": 25}, {\t\"name\": \"朝潮型\",\t\"ctype\": 18}, {\t\"name\": \"球磨型\",\t\"ctype\": 4}]";
    public static void main(String[] args) throws Exception{
        List<CType> clist = new Gson().fromJson(type,new TypeToken<List<CType>>(){}.getType());
        CType ctype = new CType();
        for(int i=0;i<clist.size();i++){
            System.out.println(clist.get(i).getName()+clist.get(i).getCtype());
        }
    }
}
final class CType implements Serializable{
    private String name;
    private int ctype;

    public void setCtype(int ctype) { this.ctype = ctype; }
    public int getCtype() { return ctype; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }
}

