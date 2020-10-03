package blockchain.executor;

//import org.jetbrains.annotations.NotNull;

import blockchain.thread.MinerThread;

import java.security.NoSuchAlgorithmException;
//import java.security.NoSuchProviderException;
import java.util.Random;
import java.util.concurrent.ThreadFactory;

public class MinerFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {
//        Thread thread = new Thread(r);
//        thread.setName("Miner" + (int) (Math.random() * 100));
//        thread.setDaemon(true);

        MinerThread thread = null;
        try {
            Random random = new Random();
            thread = new MinerThread(r, "Miner" + random.nextInt(100));
            thread.setDaemon(true);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return thread;
    }
}
