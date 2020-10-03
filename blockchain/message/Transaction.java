package blockchain.message;

public class Transaction {

    private final int value;
    private final String from;
    private final String to;

    public Transaction(String from, String to, int value) {
        this.value = value;
        this.from = from;
        this.to = to;
    }

    public byte[] toByteArray() {
        return new byte[0];
    }

    @Override
    public String toString() {
        return String.format("%s sent %d to %s", from, value, to);
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getValue() {
        return value;
    }

    public int toBalance(Transaction trans) {
        String fromName = trans.getFrom();
        if (from.equals(fromName)) {
            return (-1) * getValue();
        }

        if (to.equals(fromName)) {
            return getValue();
        }

        return 0;
    }
}
