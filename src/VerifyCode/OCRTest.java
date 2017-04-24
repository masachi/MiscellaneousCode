package VerifyCode;

/**
 * Created by Masachi on 2017/4/4.
 */
public class OCRTest {
    private String URL = "http://202.118.201.228";

    public static void main(String[] args){
        AutoLogin autoLogin = new AutoLogin();
        autoLogin.loginToRemoteServer("1","2","3");
    }
}
