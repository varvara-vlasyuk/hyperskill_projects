package blockchain.thread;

import blockchain.Blockchain;
import blockchain.message.Transaction;

import java.util.Random;
import java.util.concurrent.Callable;

public class MessageSendTask implements Callable<Boolean> {

    Blockchain blockchain;

    public MessageSendTask(Blockchain blockchain) {
        this.blockchain = blockchain;
    }

    @Override
    public Boolean call() throws Exception {
        Random rand = new Random();
        MinerThread currentThread = (MinerThread) Thread.currentThread();
        Transaction transaction = new Transaction(currentThread.getName(), "Client" + rand.nextInt(6), rand.nextInt(99) + 1);
        currentThread.addBlockchainMessage(this.blockchain, transaction);
        return true;
    }
}
