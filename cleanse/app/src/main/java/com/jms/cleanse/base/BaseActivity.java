package com.jms.cleanse.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by WangJun on 2018/3/19.
 */

public abstract class BaseActivity<P extends IPresenter> extends RxAppCompatActivity implements IView{

    protected View contentView;
    protected P mPresenter;
    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getContentView());
        unbinder = ButterKnife.bind(this);
        mPresenter = loadPresenter();
        initCommonData();
        initData();
        initListeners();

        if (mPresenter != null){
            mPresenter.onCreate();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null){
            mPresenter.onDestory();
            mPresenter.detachView();
        }
        unbinder.unbind();
    }

    private void initCommonData(){
        if (mPresenter != null){
            mPresenter.attachView(this);
        }
    }

    public View getContentView(){
        if (contentView == null){
            contentView = View.inflate(this, getLayoutId(),null);
        }
        return contentView;
    }

    public String getStringByRes(@StringRes int resId) {
        return getResources().getString(resId);
    }

    protected abstract P loadPresenter();

    protected abstract int getLayoutId();

    // 实例化数据
    protected abstract void initData();

    // 实例化监听
    protected abstract void initListeners();

}
