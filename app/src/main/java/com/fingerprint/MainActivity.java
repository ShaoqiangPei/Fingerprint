package com.fingerprint;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.p.fingerprintlib.function.Fingerprint;
import com.p.fingerprintlib.ui.FingerprintActivity;
import com.p.fingerprintlib.util.LogUtil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

public class MainActivity extends AppCompatActivity {

    private Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogUtil.setDebug(true);

        initView();
        initData();
        setListener();
    }

    private void initView(){
        mBtn=findViewById(R.id.button);
    }

    private void initData(){

    }

    private void setListener(){
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              //指纹验证
//              verifyprint();


//                //跳转设置指纹识别界面
//                FingerprintActivity.setFingerActivity(MainActivity.this);
                //跳转验证界面
                FingerprintActivity.verifyFingerActivity(MainActivity.this);
            }
        });
    }

    /**验证指纹登录**/
    private void verifyprint(){
        Fingerprint.getInstance().init(MainActivity.this)
                .callFingerPrint(new Fingerprint.OnCallBackListenr() {
                    @Override
                    public void onSupportFailed(int type, String message) {
                        LogUtil.i("=====onSupportFailed======="+message);
                    }

                    @Override
                    public void onInsecurity(int type, String message) {
                        LogUtil.i("=====onInsecurity======="+message);
                    }

                    @Override
                    public void onEnrollFailed(int type, String message) {
                        LogUtil.i("=====onEnrollFailed======="+message);
                    }

                    @Override
                    public void onAuthenticationStart() {
                        LogUtil.i("=======onAuthenticationStart===开始识别==");
                    }

                    @Override
                    public void onAuthenticationError(int errMsgId, CharSequence errString) {
                        LogUtil.i("======onAuthenticationError======"+errString.toString());
                    }

                    @Override
                    public void onAuthenticationFailed(String message) {
                        LogUtil.i("=======onAuthenticationFailed===指纹识别失败=="+message);
                    }

                    @Override
                    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                        LogUtil.i("=====onAuthenticationHelp======="+helpString);
                    }

                    @Override
                    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                        LogUtil.i("=====onAuthenticationSucceeded====指纹识别成功===");
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==FingerprintActivity.SET_TAG){//设置界面
            switch (resultCode) {
                case Activity.RESULT_CANCELED://返回键
                    LogUtil.i("======我是设置界面返回键======");
                    break;
                case Activity.RESULT_OK://设置成功
                    LogUtil.i("======设置成功======");
                    break;
                default:
                    break;
            }
        }else if(requestCode==FingerprintActivity.VERIFY_TAG){//验证界面
            switch (resultCode) {
                case Activity.RESULT_CANCELED://返回键
                    LogUtil.i("======我是验证界面返回键======");
                    break;
                case Activity.RESULT_OK://验证成功
                    LogUtil.i("======验证成功======");
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i("=====销毁指纹识别=======");
        Fingerprint.getInstance().cancel();
    }
}
