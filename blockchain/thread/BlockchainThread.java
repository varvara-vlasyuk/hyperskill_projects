package blockchain.thread;

import blockchain.Blockchain;
import blockchain.message.KeyGenerator;
import blockchain.message.Message;
import blockchain.message.Transaction;

import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public class BlockchainThread extends Thread {
    protected final String name;
    protected PublicKey publicKey;
    protected String privatePath;

    public BlockchainThread(Runnable r, String name) throws NoSuchAlgorithmException {
        super(r, name);
        this.name = name;
        createKeys();
    }

    public BlockchainThread(String name) throws NoSuchAlgorithmException {
        this(null, name);
    }

    private void createKeys() throws NoSuchAlgorithmException {
        privatePath = Paths.get("").toAbsolutePath() + "\\_private_key_" + name;
        KeyGenerator keyGen = new KeyGenerator(1024);
        publicKey = keyGen.createKeyPair(privatePath);
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public String getPrivatePath() {
        return this.privatePath;
    }

    public void addBlockchainMessage(Blockchain blockchain, Transaction transaction) throws InterruptedException {
        try {
            Message msg = new Message(transaction, blockchain.getMsgNumber(), publicKey, privatePath);
            while (!blockchain.addMessage(msg) && !Thread.currentThread().isInterrupted()) {
                msg.setId(blockchain.getMsgNumber());
                msg.sign(privatePath);
            }
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
