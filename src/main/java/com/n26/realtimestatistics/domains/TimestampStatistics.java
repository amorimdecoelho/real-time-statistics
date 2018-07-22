package com.n26.realtimestatistics.domains;

public class TimestampStatistics extends Statistics {

    public final long timestamp;

    public TimestampStatistics(double sum, long count, double min, double max, long timestamp) {
        super(sum, count, min, max);
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        TimestampStatistics that = (TimestampStatistics) o;

        return timestamp == that.timestamp;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }
}