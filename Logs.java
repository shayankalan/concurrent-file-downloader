public class Logs {
    private long WorkerId;
    private int TicketNumber;
    private int BlockIndex;
    private long try_to_get_lock;
    private String EventType = "validLogs ✅";
    private long DurationLock;

    public Logs(long workerId, int blockIndex,int ticketNumber, long try_to_get_lock,long durationLock) {
        WorkerId = workerId;
        BlockIndex = blockIndex;
        TicketNumber = ticketNumber;
        this.try_to_get_lock = try_to_get_lock;
        DurationLock = durationLock;
    }

    public Logs(){}

    public int getTicketNumber() {
        return TicketNumber;
    }

    public void setTicketNumber(int ticketNumber) {
        this.TicketNumber = ticketNumber;
    }

    @Override
    public String toString() {
        return "Logs{" +
                "WorkerId=" + WorkerId +
                ", BlockIndex=" + BlockIndex +
                ", TicketNumber=" + TicketNumber +
                ", try_to_get_lock=" + try_to_get_lock + "(ns)" +
                ", EventType='" + EventType + '\'' +
                ", DurationLock=" + DurationLock + "(ns)" +
                '}';
    }

    public long getWorkerId() {
        return WorkerId;
    }

    public void setWorkerId(long workerId) {
        WorkerId = workerId;
    }

    public int getBlockIndex() {
        return BlockIndex;
    }

    public void setBlockIndex(int blockIndex) {
        BlockIndex = blockIndex;
    }

    public long getTry_to_get_lock() {
        return try_to_get_lock;
    }

    public void setTry_to_get_lock(long try_to_get_lock) {
        this.try_to_get_lock = try_to_get_lock;
    }

    public String getEventType() {
        return EventType;
    }

    public void setEventType(String eventType) {
        EventType = eventType;
    }

    public long getDurationLock() {
        return DurationLock;
    }

    public void setDurationLock(long durationLock) {
        DurationLock = durationLock;
    }
}
