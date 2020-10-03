package blockchain.executor;

import blockchain.Blockchain;
import blockchain.thread.UserThread;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class UserCommunity implements IExecutor {
    private List<UserThread> threads;

    @Override
    public void create(int threadNumber, Blockchain blockchain) {
        threads = new ArrayList<>(threadNumber);
        for (int i = 0; i < threadNumber; i++) {
            try {
                UserThread newThread = new UserThread(blockchain, "Client" + (i + 1));
                newThread.setDaemon(true);
                threads.add(newThread);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start() {
        threads.forEach(Thread::start);
    }

    @Override
    public void stop() {
        threads.forEach(Thread::interrupt);
    }

}
