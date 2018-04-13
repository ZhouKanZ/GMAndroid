package com.jms.cleanse.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

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

        //去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        hideStatusBar(true);

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

    private void hideStatusBar(boolean isHide) {
        if (isHide)
        {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else
        {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            //如果不注释下面这句话，状态栏将把界面挤下去
            /*getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);*/
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
