package blockchain;

import blockchain.message.Message;
import blockchain.message.Transaction;
import blockchain.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

class Block {
    private final int id;
    private final String previousHash;
    private final String currentHash;
    private int magicNumber;
    private final long timeStamp;
    private final long creationTime;
    private final long miner;
    private List<Message> data;

    public Block(int id, String hash, int zeros) {
        final long startTime = System.currentTimeMillis();
        this.id = id;
        this.previousHash = hash;
        this.timeStamp = new Date().getTime();
        this.currentHash = calculateHash(zeros);
        final long endTime = System.currentTimeMillis();
        creationTime = TimeUnit.MILLISECONDS.toSeconds(endTime - startTime);
        this.miner = Thread.currentThread().getId();
        System.out.println("id : " + id + ", time : " + creationTime);
    }

    void setData(List<Message> messages) {
        if (data == null) {
            data = new ArrayList<>(messages);
        } else {
            data.addAll(messages);
        }
    }

    public int getId() {
        return id;
    }

    protected String getPreviousHash() {
        return previousHash;
    }

    private long getTimeStamp() {
        return timeStamp;
    }

    protected String getCurrentHash() {
        return currentHash;
    }

    private int getMagicNumber() {
        return magicNumber;
    }

    long getCreationTime() {
        return creationTime;
    }

    private long getMiner() {
        return miner;
    }

    List<Message> getData() {
        return data;
    }

    private String getDataString() {
        if (data == null || data.isEmpty()) {
            return "no messages";
        }
        return data.stream()
                .map(Message::getContent)
                .map(Transaction::toString)
                .reduce("", (text, s) -> text.concat("\n" + s));
    }

    private String calculateHash(int zeros) {
        String hash;
        final Random rand = new Random(timeStamp);
        do {
            magicNumber = rand.nextInt(Integer.MAX_VALUE);
            hash = StringUtil.applySha256(getStringData());
            if (Thread.currentThread().isInterrupted()) {
                return "";
            }
        } while (!hasEnoughZeros(hash, zeros));

        return hash;
    }

    private String getStringData() {
        return String.format("%d%d%d", id, timeStamp, magicNumber);
    }

    public static boolean hasEnoughZeros(String hash, int zeros) {
        for (int i = 0; i < zeros; i++) {
            if (hash.charAt(i) != '0') {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return String.format("Block:%n" +
                "Created by miner # %d%n" +
                "Id: %d%n" +
                "Timestamp: %d%n" +
                "Magic number: %d%n" +
                "Hash of the previous block:%n%s%n" +
                "Hash of the block:%n%s%n" +
                "Block data:%s%n" +
                "Block was generating for %d seconds%n",
                getMiner(),
                getId(),
                getTimeStamp(),
                getMagicNumber(),
                getPreviousHash(),
                getCurrentHash(),
                getDataString(),
                getCreationTime());
    }
}

