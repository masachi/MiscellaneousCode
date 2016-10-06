package Encrypt;

import Utils.RSAUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * Created by sdlds on 2016/9/
 */
public class RSAEncrypt {
    public static void main(String[] args) throws Exception {
        HashMap<String, Object> rsa_map = new HashMap<>();
        rsa_map = RSAUtils.getKeys();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) rsa_map.get("public");
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) rsa_map.get("private");

        String module = rsaPublicKey.getModulus().toString();
        String public_exponent = rsaPublicKey.getPublicExponent().toString();
        String private_exponent = rsaPrivateKey.getPrivateExponent().toString();

        RSAPublicKey public_key = RSAUtils.PublicKeyGenerator(module, public_exponent);
        RSAPrivateKey private_key = RSAUtils.PrivateKeyGenerator(module, private_exponent);

        String ming = "1234567890";
        String mi = RSAUtils.EncryptByPublicKey(ming, rsaPublicKey);
        System.out.println(mi);
        //解密后的明文
        ming = RSAUtils.DecryptByPrivateKey(mi, rsaPrivateKey);
        System.out.println(ming);
    }

}

class RSAInputStream extends InputStream {

    @Override
    public int read() throws IOException {
        return 0;
    }


}

class RSAOutputStream extends OutputStream {

    @Override
    public void write(int b) throws IOException {

    }
}
