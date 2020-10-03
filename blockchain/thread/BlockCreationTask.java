package blockchain.thread;

import blockchain.Blockchain;

import java.util.concurrent.Callable;

public class BlockCreationTask implements Callable<Boolean> {
    Blockchain blockchain;

    public BlockCreationTask(Blockchain blockchain) {
        this.blockchain = blockchain;
    }

    @Override
    public Boolean call() {
//        System.out.println(Thread.currentThread().getName() + " started mining");
        return blockchain.addBlock();
    }
}
