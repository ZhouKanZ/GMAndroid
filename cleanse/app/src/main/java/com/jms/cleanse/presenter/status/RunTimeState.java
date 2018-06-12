package com.jms.cleanse.presenter.status;

import com.jms.cleanse.contract.RobotMasterContract;

/**
 * Created by zhoukan on 2018/6/8.
 * @desc:
 */

public abstract class RunTimeState {

    protected RunContext context;
    public RobotMasterContract.Presenter presenter;

    public void setPresenter(RobotMasterContract.Presenter presenter) {
        this.presenter = presenter;
    }

    public void setContext(RunContext context) {
        this.context = context;
    }


    public abstract void reset();

    public abstract void switchWithIndex(int i);

    public abstract void switchMotorState(boolean flag);

    public abstract void cancelGoal();
}
