package blockchain.message;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.NoSuchAlgorithmException;
//import java.io.File;
//import java.io.FileOutputStream;
import java.io.IOException;

public class KeyGenerator {
    private final KeyPairGenerator keyGen;

    public KeyGenerator(int keyLength) throws NoSuchAlgorithmException {
        this.keyGen = KeyPairGenerator.getInstance("RSA");
        this.keyGen.initialize(keyLength);
    }

    public PublicKey createKeyPair(String filePath) {
        KeyPair pair = keyGen.generateKeyPair();
        boolean wasWritten = false;
        try {
            wasWritten = writeToFile(filePath, pair.getPrivate().getEncoded());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (wasWritten) {
            return pair.getPublic();
        }

        return null;
    }

    public boolean writeToFile(String path, byte[] key) throws IOException {
        File file = new File(path);
        try {
            if (!file.createNewFile()) {
                file.delete();
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false);
            fos.write(key);
            fos.flush();
        } catch (IOException e) {
            return false;
        } finally {
            if (fos != null) {
                fos.close();
            }
        }

        file.setReadOnly();
        file.deleteOnExit();

        return true;
//        Files.write(Path.of(path), key);
    }

}
