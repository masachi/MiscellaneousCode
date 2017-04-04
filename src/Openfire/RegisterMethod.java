package Openfire;


import com.google.gson.Gson;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;

import java.io.*;
import java.util.ArrayList;

import static Openfire.MultiThread.*;

/**
 * Created by Masachi on 2017/3/7.
 */
public class RegisterMethod implements Runnable {
    private static String username;
    private static String password;
    private static int number;
    private static ArrayList<UserModel> userPass = new ArrayList<>();

    @Override
    public void run() {
        while(i < 1) {
            UserModel userModel = new UserModel();
            ResultModel resultModel = new ResultModel();
            number = i;
            i++;
            String cmd_head = "http https://dev.hylaa.net/messaging-hub/im-auth x-hylaa-userid:";
            String cmd_tail = " x-hylaa-username:\"test user\" x-hylaa-appid:test";
            username = "test_user"+ number;
            password = "test_user"+ number;
            if (register(username, password)) {
                //usernameArray.add(username);
                //passwordArray.add(password);
                userModel.setJid(username);
                userModel.setPassword(password);
                userPass.add(userModel);
                System.out.println(username + " Created");
            } else {
                System.out.println(username + " Not Created");
            }
            //String result = exeCmd(cmd_head+username+ cmd_tail);
            userPass.add(userModel);
            //resultModel.setResult(result);
            //resultModels.add(resultModel);
            System.out.println(username + " Created");
        }

        objectToJsonFile(userPass,"file/files4.json");
    }

    public static boolean register(String username, String password) {
        try {
            ConnectionConfiguration config = new ConnectionConfiguration(host, 5222);
            Connection connection = new XMPPConnection(config);
            connection.connect();
            AccountManager amgr = connection.getAccountManager();
            amgr.createAccount(username, password);
//            HashMap<String,String> map = new HashMap<>();
//            map.put("Groups","test");
//            for(String attr : amgr.getAccountAttributes()){
//                System.out.println(attr);
//            }
            amgr.createAccount(username, password);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static String exeCmd(String commandStr) {
        BufferedReader br = null;
        System.out.println(commandStr);
        try {
            Process p = Runtime.getRuntime().exec(commandStr);
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
////                if(line.contains("HTTP/1.1")){
////                    line.replace("HTTP/1.1 ","");
////                    if(!line.equals("200")){
////                        continue;
////                    }
////                }
//                if(line.contains("jid"))
//                {
//                    line = line.replace("\"jid\": ","").replace(",","").replace("\"","");
//                    sb.append(line + "\n");
//                }
//                if(line.contains("password")){
//                    line = line.replace("\"password\": ","").replace(",","").replace("\"","");
//                    sb.append(line + "\n");
//                }
                sb.append(line + "\n");
            }
            System.out.println(sb.toString());
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            if (br != null)
            {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "fail";
    }


    public static void objectToJsonFile(Object element, String outName) {
        InputStream is = new ByteArrayInputStream(new Gson().toJson(element).getBytes());

        File file = new File(outName);
        if (!file.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
        }

        try
        {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();

            FileOutputStream outputStream = new FileOutputStream(outName);

            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = is.read(buffer)) != -1)
            {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
