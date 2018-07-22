package com.n26.realtimestatistics.domains;

import com.fasterxml.jackson.annotation.JsonGetter;

public class Statistics {

    public final double sum;
    public final long count;
    public final double min;
    public final double max;

    public Statistics(double sum, long count, double min, double max) {
        this.sum = sum;
        this.count = count;
        this.min = min;
        this.max = max;
    }

    @JsonGetter
    public final double avg() {
        if (this.count == 0) {
            return 0.0;
        } else {
            return this.sum / this.count;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Statistics that = (Statistics) o;

        if (Double.compare(that.sum, sum) != 0) return false;
        if (count != that.count) return false;
        if (Double.compare(that.min, min) != 0) return false;
        return Double.compare(that.max, max) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(sum);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (count ^ (count >>> 32));
        temp = Double.doubleToLongBits(min);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(max);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

}
