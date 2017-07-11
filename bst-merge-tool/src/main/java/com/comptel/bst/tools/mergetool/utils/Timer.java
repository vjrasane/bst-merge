package com.comptel.bst.tools.mergetool.utils;

public class Timer {

    private long time = 0;

    public static boolean PRINT = false;

    public Timer(){
        this(System.currentTimeMillis());
    }

    private Timer(long time){
        this.time = time;
    }

    public void start() {
        this.time = System.currentTimeMillis();
    }

    public long stop() {
        return System.currentTimeMillis() - this.time;
    }

    public void printDuration(String msg) {
        long duration = this.stop();
        if(PRINT)
            System.out.println("(" + msg + " in " + duration  + " milliseconds)");
    }

}
