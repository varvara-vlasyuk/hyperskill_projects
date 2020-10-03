package blockchain.thread;

import java.security.NoSuchAlgorithmException;

public class MinerThread extends BlockchainThread {

    public MinerThread(Runnable r, String name) throws NoSuchAlgorithmException {
        super(r, name);
//        System.out.println(Thread.currentThread().getName() + " was created");
    }


//    @Override
//    public void run() {
//        super.run();
//        System.out.println(Thread.currentThread().getName() + " was created");
//    }
}
