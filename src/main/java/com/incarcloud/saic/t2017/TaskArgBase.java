package com.incarcloud.saic.t2017;

import com.incarcloud.saic.heliosphere.Hourglass;

class TaskArgBase {
    private final Hourglass hourglass;

    TaskArgBase(Hourglass hourglass){
        this.hourglass = hourglass;
    }

    protected void increaseFinishedVin(){
        hourglass.increaseFinishedVin();
    }

    public void increasePerfCount(){ hourglass.increasePerfCount(); }
}
