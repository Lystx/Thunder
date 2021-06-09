package io.thunder.utils.objects;

public class ThunderTimer {

    /**
     * the start time
     */
    private long start;

    /**
     * the stop time
     */
    private long stop;

    public ThunderTimer() {
        this.start = -1;
    }

    public ThunderTimer(boolean start) {
        this();
        this.start();
    }


    /**
     * Starts the timer
     */
    public void start() {
        this.start = System.currentTimeMillis();
    }

    /**
     * Stops the timer
     */
    public void stop() {
        this.stop = System.currentTimeMillis();
    }

    /**
     * Gets the difference
     * @return long
     */
    public long getDifference() {
        return (stop - start);
    }
}
