import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdWorker {
    private static final Logger LOG = LoggerFactory.getLogger(IdWorker.class);
    private static final long TWEPOCH = 1288834974657L;
    private static final long WORKERID_BITS = 5L;
    private static final long DATACENTERID_BITS = 5L;
    private static final long MAX_WORKERID = 31L;
    private static final long MAX_DATACENTERID = 31L;
    private static final long SEQUENCE_BITS = 12L;
    private static final long WORKERID_SHIFT = 12L;
    private static final long DATACENTERID_SHIFT = 17L;
    private static final long TIMESTAMP_LEFTSHIFT = 22L;
    private static final long SEQUENCE_MASK = 4095L;
    private static long lastTimestamp = -1L;
    private long sequence = 0L;
    private final long workerId;
    private final long datacenterId;

    public IdWorker(long workerId, long datacenterId) {
        if (workerId <= 31L && workerId >= 0L) {
            if (datacenterId <= 31L && datacenterId >= 0L) {
                this.workerId = workerId;
                this.datacenterId = datacenterId;
            } else {
                throw new IllegalArgumentException("datacenter Id can't be greater than %d or less than 0");
            }
        } else {
            throw new IllegalArgumentException("worker Id can't be greater than %d or less than 0");
        }
    }

    public synchronized long nextId() {
        long timestamp = this.timeGen();
        if (timestamp < lastTimestamp) {
            try {
                throw new IllegalArgumentException("Clock moved backwards.  Refusing to generate id for " + (lastTimestamp - timestamp) + " milliseconds");
            } catch (Exception var5) {
                LOG.error("Error!", var5);
            }
        }

        if (lastTimestamp == timestamp) {
            this.sequence = this.sequence + 1L & 4095L;
            if (this.sequence == 0L) {
                timestamp = this.tilNextMillis(lastTimestamp);
            }
        } else {
            this.sequence = 0L;
        }

        lastTimestamp = timestamp;
        long nextId = timestamp - 1288834974657L << 22 | this.datacenterId << 17 | this.workerId << 12 | this.sequence;
        return nextId;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp;
        for(timestamp = this.timeGen(); timestamp <= lastTimestamp; timestamp = this.timeGen()) {
            ;
        }

        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }
}
