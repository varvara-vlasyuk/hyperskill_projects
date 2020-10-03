package blockchain.thread;

import blockchain.Blockchain;
import blockchain.message.Transaction;

import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class UserThread extends BlockchainThread {

    Blockchain blockchain;

    public UserThread(Blockchain blockchain, String name) throws NoSuchAlgorithmException {
        super(name);
        this.blockchain = blockchain;
    }

    @Override
    public void run() {
        do {
            Random random = new Random();
            Transaction transaction = new Transaction(this.name, "somebody", random.nextInt(99) + 1);
            try {
                addBlockchainMessage(blockchain, transaction);
            } catch (InterruptedException e) {
                return;
            }
        } while (!Thread.currentThread().isInterrupted());
    }
}
