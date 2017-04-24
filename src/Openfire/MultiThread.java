package Openfire;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Masachi on 2017/3/7.
 */
public class MultiThread {
    public static String host = "127.0.0.1";
    public static int port = 5222;
    //public static String serviceName = "f491f1b3cf7c";
    public static ArrayList<String> usernameArray = new ArrayList<>();
    public static ArrayList<String> passwordArray = new ArrayList<>();
    public static ArrayList<UserModel> userModels = new ArrayList<>();
    public static ArrayList<UserModel> availUsers = new ArrayList<>();
    public static ArrayList<MessageModel> messageModels = new ArrayList<>();
    public static ArrayList<String> ua = new ArrayList<>();
    public static ArrayList<ResultModel> resultModels = new ArrayList<>();
    public static ArrayList<RatioModel> ratioModels = new ArrayList<>();
    public static int i = 0;
    public static int j1 = 0;
    public static long sendNumber = 0;
    public static long receiveNumber = 0;
    public static XMPPConnection connection;

    public static void main(String[] args) {
        try {
//            for(int j = 0; j<20;j++){
//                new Thread(new RegisterMethod(i)).start();
//                i++;
//            }
            setConnection();
            //createRoom();
            getUserAndPass();
            getMessages();
            ReadUA();
            new Thread(new Runnable() {
                private long second = 0;
                private int time = 0;

                @Override
                public void run() {
                    try {
                        while (true) {
                            RatioModel ratio = new RatioModel();
                            second++;
                            time++;
                            System.out.println(sendNumber + "------" + receiveNumber);
                            double ratioTmp = ((double) receiveNumber) / ((double) sendNumber);
                            System.out.println(ratioTmp);
                            if (sendNumber != 0) {
                                ratio.setTime(second);
                                ratio.setRatio(ratioTmp);
                                ratioModels.add(ratio);
                            }
                            if (time == 10) {
                                objectToJsonFile(ratioModels, "file/output.json");
                                time = 0;
                            }
                            Thread.sleep(1000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            for (int j = 0; j < 60; j++) {
                new Thread(new LoginMethod()).start();
                //new Thread(new RegisterMethod()).start();
                //new Thread(new MultiChat()).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setConnection() {
        ConnectionConfiguration config = new ConnectionConfiguration(host, port);
        //config.setSASLAuthenticationEnabled(false);
        config.setSASLAuthenticationEnabled(true);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setDebuggerEnabled(false);
        config.setReconnectionAllowed(true);
        connection = new XMPPConnection(config);
        try {
            connection.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getUserAndPass() {
        try {
            userModels = new Gson().fromJson(new JsonReader(new FileReader("file/user.json")), new TypeToken<List<UserModel>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getMessages() {
        try {
            messageModels = new Gson().fromJson(new JsonReader(new FileReader("file/data.json")), new TypeToken<List<MessageModel>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void ReadUA() {
        File file = new File("file/xmpp_ua");

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

    public static void createRoom() {
        MultiUserChat muc = null;
        try {
            connection.login("admin", "openfire");
            //muc = new MultiUserChat(connection, "test@conference." + serviceName);
            muc.create("test");
            Form form = muc.getConfigurationForm();
            Form submitForm = form.createAnswerForm();
            for (Iterator<FormField> fields = form.getFields(); fields
                    .hasNext(); ) {
                FormField field = (FormField) fields.next();
                if (!FormField.TYPE_HIDDEN.equals(field.getType())
                        && field.getVariable() != null) {
                    // 设置默认值作为答复
                    submitForm.setDefaultAnswer(field.getVariable());
                }
            }
            // 设置聊天室是持久聊天室，即将要被保存下来
            submitForm.setAnswer("muc#roomconfig_persistentroom", true);
            // 房间仅对成员开放
            submitForm.setAnswer("muc#roomconfig_membersonly", false);
            // 允许占有者邀请其他人
            submitForm.setAnswer("muc#roomconfig_allowinvites", true);
            submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", false);
            // 登录房间对话
            submitForm.setAnswer("muc#roomconfig_enablelogging", true);
            // 仅允许注册的昵称登录
            submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
            // 发送已完成的表单（有默认值）到服务器来配置聊天室
            muc.sendConfigurationForm(submitForm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void send() {
        sendNumber++;
    }

    public static synchronized void receive() {
        receiveNumber++;
    }

    public static void objectToJsonFile(Object element, String outName) {
        InputStream is = new ByteArrayInputStream(new Gson().toJson(element).getBytes());

        File file = new File(outName);
        if (!file.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
        }

        try {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();

            FileOutputStream outputStream = new FileOutputStream(outName);

            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = is.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
