package org.xufuja;

public class Counter {
    private long count;
    public Counter(long count) {
        this.count = count;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long incrementCountPre() {
        ++this.count;
        return this.count;
    }

}
