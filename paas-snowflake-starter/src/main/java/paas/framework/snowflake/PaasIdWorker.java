package paas.framework.snowflake;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * 雪花算法ID生成器
 */
public class PaasIdWorker {
    private static final long twepoch = 1288834974657L;
    private static final long workerIdBits = 5L;
    private static final long processIdBits = 5L;
    private static final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private static final long maxProcessId = -1L ^ (-1L << processIdBits);
    private static final long sequenceBits = 12L;

    private static final long workerIdShift = sequenceBits;
    private static final long processIdShift = sequenceBits + workerIdBits;
    private static final long timestampLeftShift = sequenceBits + workerIdBits + processIdBits;
    private static final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long workerId;
    private long processId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    private static PaasIdWorker keyWorker;

    public static synchronized long nextId() {
        return keyWorker.getNextId();
    }

    public static synchronized List<Long> getBatchIds(int size) {
        List<Long> ids = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ids.add(keyWorker.getNextId());
        }
        return ids;
    }

    private PaasIdWorker() {
        this.workerId = getCombinedWorkerId();
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        this.processId = (Long.parseLong(runtimeMXBean.getName().split("@")[0]) & maxProcessId);

        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (processId > maxProcessId || processId < 0) {
            throw new IllegalArgumentException(String.format("process Id can't be greater than %d or less than 0", maxProcessId));
        }
    }

    public synchronized long getNextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + (lastTimestamp - timestamp) + " milliseconds");
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << timestampLeftShift)
                | (processId << processIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    private long getMachineNum() {
        StringBuilder sb = new StringBuilder();
        try {
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface ni = e.nextElement();
                sb.append(ni.toString());
            }
        } catch (SocketException var6) {
            throw new RuntimeException("Failed to get machine number", var6);
        }

        return sb.toString().hashCode();
    }

    private long getCombinedWorkerId() {
        long machineNum = getMachineNum() & maxWorkerId;
        long randomNum = (new Random().nextLong()) & maxWorkerId;
        return (machineNum ^ randomNum) & maxWorkerId;
    }

    static {
        keyWorker = new PaasIdWorker();
    }
}
