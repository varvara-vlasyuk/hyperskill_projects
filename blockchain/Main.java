package blockchain;

import blockchain.executor.IExecutor;
import blockchain.executor.MinerCommunity;
import blockchain.executor.UserCommunity;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();
        final Blockchain blockchain = new Blockchain();
        int poolSize = Runtime.getRuntime().availableProcessors();
        IExecutor miners = new MinerCommunity();
        IExecutor clients = new UserCommunity();

        miners.create(poolSize, blockchain);
        clients.create(2, blockchain);

        clients.start();
        miners.start();

        clients.stop();
        miners.stop();

        blockchain.printBlocks(20);
        final long endTime = System.currentTimeMillis();
        System.out.println(TimeUnit.MILLISECONDS.toSeconds(endTime - startTime));
    }
}
