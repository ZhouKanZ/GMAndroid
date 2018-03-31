package com.jms.cleanse.base;

import java.lang.ref.WeakReference;

/**
 * Created by WangJun on 2018/3/19.
 */

public abstract class BasePresenter<V extends IView> implements IPresenter {

    private WeakReference mViewRef;
    private V iview;
    @Override
    public void attachView(IView view) {
        mViewRef = new WeakReference(view);
        iview = (V) view;
    }

    public boolean isAttached(){
        if (mViewRef != null && mViewRef.get() != null){
            return true;
        }
        return false;
    }

    @Override
    public void detachView() {
        if (mViewRef != null){
            mViewRef.clear();
            mViewRef = null;
        }
    }

    @Override
    public V getView() {
        return (V) mViewRef.get();
    }

}
