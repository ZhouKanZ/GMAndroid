package com.jms.cleanse.presenter.status;

/**
 * Created by zhoukan on 2018/6/8.
 *
 * @desc: 手动模式
 */

public class ManuallyState extends RunTimeState {
    @Override
    public void reset() {
        context.reset();
    }

    @Override
    public void switchWithIndex(int i) {
        context.switchWithIndex(i);
    }

    @Override
    public void switchMotorState(boolean flag) {
        // do nothing
        context.switchMotorState(flag);
    }

    @Override
    public void cancelGoal() {
        // cancel goal
    }
}
