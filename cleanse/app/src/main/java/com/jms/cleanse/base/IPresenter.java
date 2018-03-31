package com.jms.cleanse.base;

/**
 * Created by WangJun on 2018/3/19.
 */

public interface IPresenter<V extends IView>{

    /**
     * @param view 绑定
     */
    void attachView(V view);

    /**
     * 防止内存的泄漏,清楚presenter与activity之间的绑定
     */
    void detachView();

    /**
     *
     * @return 获取View
     */
    V getView();

    void onCreate();

    void onDestory();
}
