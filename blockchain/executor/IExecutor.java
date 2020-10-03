package blockchain.executor;

import blockchain.Blockchain;

public interface IExecutor {

    void create(int threadNumber, Blockchain blockchain);
    void start();
    void stop();

}
