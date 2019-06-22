package com.p.fingerprintlib.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.p.fingerprintlib.R;
import com.p.fingerprintlib.R2;
import com.p.fingerprintlib.function.Fingerprint;
import com.p.fingerprintlib.util.DoubleClickUtil;
import com.p.fingerprintlib.util.LogUtil;
import com.p.fingerprintlib.util.StatusBarUtil;
import com.p.fingerprintlib.util.ToastUtil;

import java.lang.ref.WeakReference;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Description:
 * <p>
 * Author:pei
 * Date: 2019/5/21
 */
public class FingerprintActivity extends AppCompatActivity implements View.OnClickListener {

    /**设置指纹密码**/
    public static void setFingerActivity(Context context){
        Intent intent=new Intent(context, FingerprintActivity.class);
        intent.putExtra(FingerprintActivity.TAG,FingerprintActivity.SET_TAG);
        if(context instanceof FragmentActivity) {
            ((FragmentActivity) context).startActivityForResult(intent, FingerprintActivity.SET_TAG);
        }else if(context instanceof Activity){
            ((Activity) context).startActivityForResult(intent, FingerprintActivity.SET_TAG);
        }
    }

    /**验证指纹密码**/
    public static void verifyFingerActivity(Context context){
        Intent intent=new Intent(context, FingerprintActivity.class);
        intent.putExtra(FingerprintActivity.TAG,FingerprintActivity.VERIFY_TAG);
        if(context instanceof FragmentActivity) {
            ((FragmentActivity) context).startActivityForResult(intent, FingerprintActivity.VERIFY_TAG);
        }else if(context instanceof Activity){
            ((Activity) context).startActivityForResult(intent, FingerprintActivity.VERIFY_TAG);
        }
    }

    @BindView(R2.id.tv_title)
    TextView mTvTitle;
    @BindView(R2.id.imv_title_back)
    ImageView mImvTitleBack;
    @BindView(R2.id.imv_finger)
    ImageView mImvFinger;

    private Unbinder mUnbinder;
    private static String TAG = "Fingerprint_tag";
    public static final int SET_TAG = 0x1;//go to set finger
    public static final int VERIFY_TAG = 0x2;//go to verify finger

    private Context mContext;
    private int mTag;
    private TextView[] mTvArray = new TextView[5];
    private AlertDialog mDialog;
    private int mPostion = 0;
    private Handler mHandler=new CustomerHandler(FingerprintActivity.this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_print);
        mContext=FingerprintActivity.this;
        mUnbinder = ButterKnife.bind(this);

        initTitle();
        initData();
        setListener();
    }

    private void initTitle(){
        //设置状态栏背景透明
        StatusBarUtil.immersive((FragmentActivity) mContext);
        //设置状态栏文字变黑
        StatusBarUtil.darkMode((FragmentActivity) mContext);
        //智能设置标题栏间距
        StatusBarUtil.setPaddingSmart((FragmentActivity) mContext,mTvTitle);
    }

    private void initData() {
        mTag = this.getIntent().getIntExtra(FingerprintActivity.TAG, 0);
        if (mTag == SET_TAG) {
            LogUtil.i("=======mImvTitleBack======="+mImvTitleBack);
            mImvTitleBack.setVisibility(View.VISIBLE);
            mImvTitleBack.setEnabled(true);
            mTvTitle.setText("设置指纹");
        } else if (mTag == VERIFY_TAG) {
            mImvTitleBack.setVisibility(View.INVISIBLE);
            mImvTitleBack.setEnabled(false);
            mTvTitle.setText("验证指纹");
        }
    }

    private void setListener(){
        mImvTitleBack.setOnClickListener(this);
        mImvFinger.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        DoubleClickUtil.shakeClick(v);
        if (v.getId() == R.id.imv_title_back) {//返回
            if (mImvTitleBack.getVisibility() == View.VISIBLE) {
                 setResult(Activity.RESULT_CANCELED,null);
                 FingerprintActivity.this.finish();
            }
        } else if (v.getId() == R.id.imv_finger) {//指纹识别
            fingerFunction();
        }
    }

    /**指纹识别**/
    private void fingerFunction() {
        Fingerprint.getInstance().init(mContext)
                .callFingerPrint(new Fingerprint.OnCallBackListenr() {
                    @Override
                    public void onSupportFailed(int type, String message) {
                        ToastUtil.shortShow(message,mContext);
                    }

                    @Override
                    public void onInsecurity(int type, String message) {
                        ToastUtil.shortShow(message,mContext);
                    }

                    @Override
                    public void onEnrollFailed(int type, String message) {
                        ToastUtil.shortShow(message,mContext);
                    }

                    @Override
                    public void onAuthenticationStart() {
                        //开始识别
                        showFingerDialog();
                    }

                    @Override
                    public void onAuthenticationError(int errMsgId, CharSequence errString) {
                        //当出现错误的时候回调此函数，比如多次尝试都失败了的时候，errString是错误信息
                        ToastUtil.shortShow(errString.toString(),mContext);
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                            mHandler.removeMessages(0);
                            Fingerprint.getInstance().cancel();
                        }
                    }

                    @Override
                    public void onAuthenticationFailed(String message) {
                        //验证失败
                        if (mTag == SET_TAG) {//设置
                            ToastUtil.shortShow("指纹识别开启失败",mContext);
                        } else if (mTag == VERIFY_TAG) {//验证
                            ToastUtil.shortShow("指纹识别验证失败",mContext);
                        }
                    }

                    @Override
                    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                        ToastUtil.shortShow(helpString.toString(),mContext);
                    }

                    @Override
                    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                            mHandler.removeMessages(0);
                        }
                        //继续业务逻辑
                        if (mTag == SET_TAG) {//设置
                            ToastUtil.shortShow("指纹识别开启成功",mContext);
                            //关闭当前界面
                            setResult(Activity.RESULT_OK);
                            FingerprintActivity.this.finish();
                        } else if (mTag == VERIFY_TAG) {//验证
                            ToastUtil.shortShow("指纹识别验证成功",mContext);
                            //关闭当前界面
                            setResult(Activity.RESULT_OK);
                            FingerprintActivity.this.finish();
                        }
                    }
                });
    }

    private void showFingerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_fingerprint, null);
        mPostion = 0;
        mTvArray[0] = view.findViewById(R.id.tv_1);
        mTvArray[1] = view.findViewById(R.id.tv_2);
        mTvArray[2] = view.findViewById(R.id.tv_3);
        mTvArray[3] = view.findViewById(R.id.tv_4);
        mTvArray[4] = view.findViewById(R.id.tv_5);
        mHandler.sendEmptyMessageDelayed(0, 100);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setNegativeButton("取消", (dialog, which) -> {
            mHandler.removeMessages(0);
            Fingerprint.getInstance().cancel();
        });
        mDialog = builder.create();
        mDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        //退出时销毁识别
        Fingerprint.getInstance().cancel();
    }

    //自定义handler类
    class CustomerHandler extends Handler {
        //弱引用(引用外部类)
        WeakReference<FingerprintActivity> mCls;

        CustomerHandler(FingerprintActivity cls){
            //构造弱引用
            mCls = new WeakReference<FingerprintActivity>(cls);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //通过弱引用获取外部类.
            FingerprintActivity cls = mCls.get();
            //进行非空再操作
            if (cls != null) {
                if (msg.what == 0) {
                    int i = mPostion % 5;
                    if (i == 0) {
                        mTvArray[4].setBackgroundResource(0);
                        mTvArray[i].setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                    } else {
                        mTvArray[i].setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                        mTvArray[i - 1].setBackgroundResource(0);
                    }
                    mPostion++;
                    mHandler.sendEmptyMessageDelayed(0, 100);
                }
            }
        }
    }
}
