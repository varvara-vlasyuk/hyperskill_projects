package blockchain.message;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.Signature;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;

public class Message {

    BigInteger id;
    final Transaction content;
    byte[] signature;
    final PublicKey key;

    public Message(Transaction obj, BigInteger id, PublicKey key, String privateKeyFilePath) throws Exception {
        this.id = id;
        this.content = obj;
        this.key = key;
        this.signature = createSignature(privateKeyFilePath);
    }

    private byte[] createSignature(String filePath) throws Exception {
        Signature rsa = Signature.getInstance("SHA1withRSA");
        rsa.initSign(retrievePrivateKey(filePath));
        rsa.update(content.toByteArray());
        rsa.update(id.toByteArray());
        return rsa.sign();
    }

    private PrivateKey retrievePrivateKey(String filePath) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filePath).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public void sign(String privateKeyFilePath) throws Exception {
        this.signature = createSignature(privateKeyFilePath);
    }

    public Transaction getContent() {
        return content;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public boolean isEarlier(BigInteger num) {
        return id.compareTo(num) < 0;
    }

    public boolean verifyMessage() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sign = Signature.getInstance("SHA1withRSA");
        sign.initVerify(key);
        sign.update(content.toByteArray());
        sign.update(id.toByteArray());
        return sign.verify(signature);

    }
}
