package com.comptel.bst.tools.mergetool.cli;



public class Options {

    private final Strategy strat;

    public Options(boolean interactive, Force force) {
        this.strat = new Strategy(interactive, force);
    }

    public Strategy getStrat() {
        return strat;
    }

}
