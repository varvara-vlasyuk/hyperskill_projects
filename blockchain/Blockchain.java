package blockchain;

import blockchain.message.Message;
import blockchain.message.Transaction;
import blockchain.thread.BlockchainThread;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Blockchain {
    private final ArrayDeque<Block> chain = new ArrayDeque<>();
    private volatile int numberOfZeros = 1;
    private final List<Integer> zerosLog = new ArrayList<>();
    private List<Message> data = new ArrayList<>();
    private final AtomicReference<BigInteger> msgNumber = new AtomicReference<>();

    public Blockchain() {
        chain.add(new Block(1, "0", 0));
        zerosLog.add(numberOfZeros);
        msgNumber.set(BigInteger.ONE);
    }

    public Boolean addBlock() {
        final var newBlock = new Block(chain.size() + 1, getLastHash(), numberOfZeros);
        synchronized (chain) {
            if (isBlockValid(newBlock)) {
                validateData();

                try {
                    BlockchainThread currentThread = (BlockchainThread) Thread.currentThread();
                    currentThread.addBlockchainMessage(this, new Transaction("blockchain", currentThread.getName(), 100));
                } catch (InterruptedException e) {
                    return false;
                }

                newBlock.setData(data);
                data.clear();

                chain.add(newBlock);

                final long creationTime = newBlock.getCreationTime();
                if (creationTime > 1 && numberOfZeros > 0) {
                    numberOfZeros--;
                } else if (creationTime < 1) {
                    numberOfZeros++;
                }

                zerosLog.add(numberOfZeros);
//                System.out.println(Thread.currentThread().getName()
//                        + " added block " + newBlock.getId());
                return true;
            }
        }
        return false;
    }

    public BigInteger getMsgNumber() {
        return msgNumber.getAndUpdate(x -> x.add(BigInteger.ONE));
    }

    public boolean addMessage(Message message) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        synchronized (chain) {
            BigInteger maxMsgId = getMaxMsgIdentifier();
            if (message.verifyMessage() && !message.isEarlier(maxMsgId)) {
                data.add(message);
//                System.out.println(message.getContent().toString());
                return true;
            }/* else {
                System.out.println(Thread.currentThread().getName() + " -- maxID:" + maxMsgId + " -- msgID:" + message.getId() + " -- " + message.getContent().toString());
            }*/
            return false;
        }
    }

    private BigInteger getMaxMsgIdentifier() {
        return data.stream()
                .map(Message::getId)
                .max(Comparator.naturalOrder())
                .orElseGet(()-> BigInteger.ZERO);
    }

    private String getLastHash() {
        return chain.getLast().getCurrentHash();
    }

    private boolean isBlockValid(Block block) {
        return getLastHash().equals(block.getPreviousHash()) &&
                Block.hasEnoughZeros(block.getCurrentHash(), numberOfZeros);
    }

    private int calcBalance(Message msg, List<Message> blockData, int currentBalance) {
        Transaction transaction = msg.getContent();
        return blockData.stream()
                .filter(m -> m.isEarlier(msg.getId()))
                .map(Message::getContent)
                .reduce(currentBalance, (balance, tr) -> balance + tr.toBalance(transaction), Integer::sum);
    }

    private boolean checkContent(Message msg, List<Message> validData) {
        synchronized (chain) {

            int balance = 100;
            for (Block block : chain) {
                final List<Message> blockData = block.getData();
                if (blockData == null) {
                    continue;
                }

                balance = calcBalance(msg, blockData, balance);

                if (balance < 0) {
//                    System.out.println("rejected " + msg.getContent().toString() + " with balance " + balance);
                    return false;
                }
            }

            balance = calcBalance(msg, validData, balance);

            Transaction transaction = msg.getContent();

//            if (balance < transaction.getValue()) {
//                System.out.println("rejected " + transaction.toString() + " with balance " + balance);
//            }
            return balance >= transaction.getValue();
        }
    }

    private void validateData() {
        final ArrayList<Message> validData = new ArrayList<>(data);
        for (Message msg : data) {
            if (!checkContent(msg, validData)) {
                validData.remove(msg);
            }
        }

        data = validData;
    }

    synchronized public int getSize() {
        return chain.size();
    }

    public void printBlocks(int numberOfBlocks) {
        int blocksToPrint = numberOfBlocks;
        if (numberOfBlocks > 0 && chain.size() > 0) {
            if (numberOfBlocks > chain.size()) {
                blocksToPrint = chain.size();
            }

            int logIndex = 0;
            int prevNumberOfZeros = 0;
            for (Block block : chain) {
                if (blocksToPrint == 0) {
                    break;
                }

                System.out.print(block.toString());

                int currentNumberOfZeros = zerosLog.get(logIndex);
                int zerosDifference = prevNumberOfZeros - currentNumberOfZeros;
                if (zerosDifference < 0) {
                    System.out.printf("N was increased to %d%n%n", currentNumberOfZeros);
                } else if (zerosDifference > 0) {
                    System.out.printf("N was decreased to %d%n%n", currentNumberOfZeros);
                } else {
                    System.out.printf("N stays the same%n%n");
                }

                prevNumberOfZeros = currentNumberOfZeros;
                logIndex++;
                blocksToPrint--;
            }
        }
    }
}
