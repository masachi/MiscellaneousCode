package Openfire;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.Random;

import static Openfire.LoginMethod.login_only;
import static Openfire.MultiThread.*;


/**
 * Created by Masachi on 2017/3/20.
 */
public class MultiChat implements Runnable{
    private static Random random = new Random();
    @Override
    public void run() {
        while(i<20) {
            int number = i;
            i++;
            try {
                XMPPConnection connection = login_only(userModels.get(number).getJid(),userModels.get(number).getPassword());
                MultiUserChat muc = new MultiUserChat(connection, "test@conference." + host);
                // 聊天室服务将会决定要接受的历史记录数量
                DiscussionHistory history = new DiscussionHistory();
                history.setMaxChars(1000000000);
                // history.setSince(new Date());
                // 用户加入聊天室
                muc.join(userModels.get(number).getJid(),"",history,SmackConfiguration.getPacketReplyTimeout());
                while(true){
                    String message = messageModels.get(random.nextInt(10)).getMessage();
                    muc.sendMessage(message);
                    System.out.println("From:  " + userModels.get(number).getJid() + "      Message:  " + message);
                    Thread.sleep(random.nextInt(3000));
                }
                //muc.addMessageListener(new TaxiMultiListener());
                //Message message = new Message();
//                while(true){
//                    message.setBody(messageModels.get(random.nextInt(10)).getMessage());
//                    muc.sendMessage(message);
//                    Thread.sleep(3000);
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class TaxiMultiListener implements PacketListener {

        @Override
        public void processPacket(Packet packet) {
            Message message = (Message) packet;
            String body = message.getBody();
        }
    }
}
