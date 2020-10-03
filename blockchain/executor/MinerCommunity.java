package blockchain.executor;

import blockchain.Blockchain;
import blockchain.thread.BlockCreationTask;
import blockchain.thread.MessageSendTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MinerCommunity implements IExecutor {
    private Blockchain blockchain;
    private ExecutorService service;
    private int numberOfBlocks;
    private int poolSize;

    @Override
    public void create(int threadNumber, Blockchain blockchain) {
        setNumberOfBlocks(14);
        this.blockchain = blockchain;
        this.poolSize = threadNumber;
        service = Executors.newFixedThreadPool(threadNumber, new MinerFactory());
    }

    public void setNumberOfBlocks(int numberOfBlocks) {
        this.numberOfBlocks = numberOfBlocks;
    }

    @Override
    public void start() {
        List<BlockCreationTask> taskList = new ArrayList<>();
        List<MessageSendTask> msgList = new ArrayList<>();
        for (int i = 0; i < poolSize; i++) {
            taskList.add(new BlockCreationTask(blockchain));
            msgList.add(new MessageSendTask(blockchain));
        }

        for (int i = 0; i < numberOfBlocks; i++) {
            try {
                final long startTime = System.currentTimeMillis();
                if(!service.invokeAny(taskList)) {
                    i--;
                }
//                System.out.println("iteration " + i +
//                        ", " + result +
//                        ", size of chain = " + blockchain.getSize());
                service.invokeAll(msgList);
                final long endTime = System.currentTimeMillis();
                System.out.println(i + " cycle time: " + TimeUnit.MILLISECONDS.toSeconds(endTime - startTime));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        service.shutdownNow();
    }
}

