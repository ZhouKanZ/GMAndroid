package com.jms.cleanse.presenter.status;

/**
 * Created by zhoukan on 2018/6/8.
 *
 * @desc:
 */

public class RunContext {

    public static final ManuallyState MANUALLY_STATE = new ManuallyState();
    public static final AutomaticState AUTOMATIC_STATE = new AutomaticState();

    private RunTimeState state;

    public RunTimeState getState() {
        return state;
    }

    public void setState(RunTimeState state) {
        this.state = state;
    }

    public void reset(){
        this.state.reset();
    }

    public void switchWithIndex(int i){
        this.state.switchWithIndex(i);
    }

    public void switchMotorState(boolean flag){
        this.state.switchMotorState(flag);
    }

    public void cancelGoal(){
        this.state.cancelGoal();
    }
}
