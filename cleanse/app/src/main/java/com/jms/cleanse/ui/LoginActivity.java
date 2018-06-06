package com.jms.cleanse.ui;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jms.cleanse.R;
import com.jms.cleanse.base.BaseActivity;
import com.jms.cleanse.base.BasePresenter;
import com.jms.cleanse.config.UserConfig;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by WangJun on 2018/3/19.
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_pwd)
    EditText etPwd;

    @Override
    protected BasePresenter loadPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initData() {

        // 获取屏幕的宽高
        WindowManager manager = getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

    }

    @Override
    protected void initListeners() {
    }

    @OnClick(R.id.btn_login)
    public void onViewClicked() {
        // 满足登陆条件后 不再需要登陆

        Log.d("click", "onViewClicked: " + judgeUserInfo());

        if (judgeUserInfo()) {
            startActivity(new Intent(LoginActivity.this, ServerListActivity.class));
            this.finish();
        }
    }

    /**
     * 判断用户信息
     */
    private boolean judgeUserInfo() {
        String user_name = etAccount.getEditableText().toString().trim();
        String user_pwd = etPwd.getEditableText().toString().trim();

        if (TextUtils.isEmpty(user_name) || TextUtils.isEmpty(user_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.empty_notice), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (user_name.equals(UserConfig.user_name) && user_pwd.equals(UserConfig.user_pwd)) {
            return true;
        } else {
            Toast.makeText(this, getResources().getString(R.string.error_login), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}
