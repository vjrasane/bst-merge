package com.comptel.bst.tools.mergetool.cli;

/*
 * Wrapper class for any non-mandatory options
 */
public class Options {

    private final Strategy strat;

    public Options(boolean interactive, Force force) {
        this.strat = new Strategy(interactive, force);
    }

    public Strategy getStrat() {
        return strat;
    }

}
