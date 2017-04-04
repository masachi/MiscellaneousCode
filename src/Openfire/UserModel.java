package Openfire;

/**
 * Created by Masachi on 2017/3/8.
 */
public class UserModel {
    private String jid;
    private String password;

    public void setPassword(String password) {
        this.password = password;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getPassword() {
        return password;
    }

    public String getJid() {
        return jid;
    }
}
