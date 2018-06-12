package com.jms.cleanse.presenter.status;

/**
 * Created by zhoukan on 2018/6/8.
 *
 * @desc:
 */

public class AutomaticState extends RunTimeState {
    @Override
    public void reset() {
        // do nothing
    }

    @Override
    public void switchWithIndex(int i) {
        // do nothing
    }

    @Override
    public void switchMotorState(boolean flag) {
        // do nothing
    }

    @Override
    public void cancelGoal() {
        // cancel goal
        context.cancelGoal();
    }
}
