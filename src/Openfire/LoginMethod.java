package Openfire;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

import java.util.Random;

import static Openfire.MultiThread.*;


/**
 * Created by Masachi on 2017/3/7.
 */
public class LoginMethod implements Runnable {
    private static int number;
    private static Random random = new Random();
    private static Message message = new Message();
    private static MyMessageListener myMessageListener = new MyMessageListener();
    private static UserModel userModel;
    private static MessageListener messageListener = new MessageListener() {
        @Override
        public void processMessage(Chat chat, Message message) {
            receive();
        }
    };
    private static ConnectionConfiguration config;


    private static void fail(Object o) {
        if (o != null) {
            System.out.println(o);
        }
    }

    private static void fail(Object o, Object... args) {
        if (o != null && args != null && args.length > 0) {
            String s = o.toString();
            for (int i = 0; i < args.length; i++) {
                String item = args[i] == null ? "" : args[i].toString();
                if (s.contains("{" + i + "}")) {
                    s = s.replace("{" + i + "}", item);
                } else {
                    s += " " + item;
                }
            }
            System.out.println(s);
            //System.out.println(receiveNumber+"++++++");
        }
    }

    @Override
    public void run() {
        while (i < 60) {
            number = i;
            i++;
            userModel = userModels.get(number);
            login(userModel.getJid(),userModel.getPassword(),userModel);
        }
    }

    public static void login(String username, String password, UserModel user) {
        try {
            config = new ConnectionConfiguration(host, port);
            //config.setSASLAuthenticationEnabled(false);
            config.setSASLAuthenticationEnabled(true);
            //config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            config.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
            //config.setDebuggerEnabled(true);
            config.setDebuggerEnabled(false);
            config.setReconnectionAllowed(true);
            XMPPConnection connection = new XMPPConnection(config);
            connection.connect();
            System.out.println(username + " Login");
            try {
                connection.login(username, password, ua.get(random.nextInt(67)));
                availUsers.add(user);
            }
            catch (XMPPException e){
                e.printStackTrace();
            }
//            connection.getRoster().createEntry(username,null, new String[]{"test"});
            //Thread.sleep(200000000);
            //connection.disconnect();
            //System.out.println(username + " Logout");
            while (true){
                chat(connection);
                Thread.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static XMPPConnection login_only(String username, String password) {
        try {
            ConnectionConfiguration config = new ConnectionConfiguration(host, port);
            config.setSASLAuthenticationEnabled(false);
            //config.setSASLAuthenticationEnabled(true);
            config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            config.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
            config.setDebuggerEnabled(true);
            config.setReconnectionAllowed(true);
            XMPPConnection connection = new XMPPConnection(config);
            connection.connect();
            System.out.println(username + " Login");
            connection.login(username, password,ua.get(random.nextInt(67)));
            //Thread.sleep(200000000);
            //connection.disconnect();
            //System.out.println(username + " Logout");
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void chat(XMPPConnection connection){
        ChatManager chatManager = connection.getChatManager();
        chatManager.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean b) {
                chat.addMessageListener(myMessageListener);
            }
        });
        Chat chat = chatManager.createChat(availUsers.get(random.nextInt(availUsers.size())).getJid()+"@"+host,myMessageListener);
        try{
            //chat.sendMessage(messageModels.get(random.nextInt(11)).getMessage());
            message.setBody(messageModels.get(random.nextInt(10)).getMessage());
            //message.setProperty("color", "red");
            send();
            try{
                chat.sendMessage(message);
            }
            catch (IllegalStateException e){
                e.printStackTrace();
                connection.connect();
                connection.login(userModel.getJid(),userModel.getPassword());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    static class MyMessageListener implements MessageListener{

        @Override
        public void processMessage(Chat chat, Message message) {
            try {
                /** 发送消息 */
                //send();
                //chat.sendMessage("dingding……" + message.getBody());
                //chat.sendMessage(message.getBody());
                //receive();
            } catch (Exception e) {
                e.printStackTrace();
            }
            /** 接收消息 */
            receive();
            //fail("From: {0}, To: {1}, Type: {2}, Sub: {3}", message.getFrom(), message.getTo(), message.getType(), message.toXML());
            /*Collection<Body> bodys =  message.getBodies();
            for (Body body : bodys) {
                fail("bodies[{0}]", body.getMessage());
            }
            //fail(message.getLanguage());
            //fail(message.getThread());
            //fail(message.getXmlns());*/
            //fail("body: ", message.getBody());
        }
    }

    static class MyChatListener implements ChatManagerListener{

        @Override
        public void chatCreated(Chat chat, boolean b) {
            chat.addMessageListener(new MessageListener() {
                @Override
                public void processMessage(Chat chat, Message message) {
                    receive();
                }
            });
        }
    }
}
