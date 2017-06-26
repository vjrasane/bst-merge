package com.comptel.bst.tools.mergetool.utils;

public class Timer {

    private long time = 0;

    public static boolean PRINT = false;

    private Timer(long time){
        this.time = time;
    }

    public static Timer start() {
        return new Timer(System.currentTimeMillis());
    }

    public long stop() {
        return System.currentTimeMillis() - this.time;
    }

    public void printDuration(String msg) {
        if(PRINT)
            System.out.println("(" + msg + " in " + this.stop() + " milliseconds)");
    }

}
