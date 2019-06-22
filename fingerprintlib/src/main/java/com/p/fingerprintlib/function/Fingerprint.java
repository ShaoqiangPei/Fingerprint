package com.p.fingerprintlib.function;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;

import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;

/**
 * Description:指纹密码工具类
 * <p>
 * Author:pei
 * Date: 2019/4/30
 */
public class Fingerprint {

    public static final int SUPORT_TYPE=1;//支持指纹识别
    public static final int UNSUPPORT_FINGER_PRINT_TYPE=0;//设备不支持指纹识别
    public static final int UNPROTECTED_TYPE=-1;//设备未处于安全保护中
    public static final int UNREGISTED_TYPE=-2;//设备未注册指纹

    private CancellationSignal mCancellationSignal;
    private FingerprintManagerCompat managerCompat;
    private Context mContext;

    private Fingerprint(){}

    private static class Holder {
        private static Fingerprint instance = new Fingerprint();
    }

    public static Fingerprint getInstance() {
        return Holder.instance;
    }

    /**初始化**/
    public Fingerprint init(Context context){
        this.mContext=context;
        managerCompat= FingerprintManagerCompat.from(mContext);
        return Fingerprint.this;
    }

    /**
     * 是否能指纹识别
     * @return SUPORT_TYPE 支持指纹识别，否则不支持
     */
    public int isSuport(){
        if(mContext==null){
            throw new NullPointerException("====mContext为null,请先调用init(Context context)初始化===");
        }
        if(!isSupportFingerprint()){
            //设备不支持指纹识别
            return Fingerprint.UNSUPPORT_FINGER_PRINT_TYPE;
        }else if(!isProtected()){
            //设备未处于安全保护中
            return Fingerprint.UNPROTECTED_TYPE;
        }else if(!hasRegistedFingerprint()){
            //设备未注册指纹
            return Fingerprint.UNREGISTED_TYPE;
        }
        return Fingerprint.SUPORT_TYPE;
    }

    /**开始识别,第四步**/
    public void callFingerPrint(final OnCallBackListenr listener){
        if(listener==null){
            throw new NullPointerException("====OnCallBackListenr为null===");
        }
        String message=null;
        int type=isSuport();
        if(type==Fingerprint.UNSUPPORT_FINGER_PRINT_TYPE){
            message="此设备不支持指纹解锁";
            listener.onSupportFailed(type,message);
            return;
        }
        if (type==Fingerprint.UNPROTECTED_TYPE) {
            message="请开启锁屏密码";
            listener.onInsecurity(type,message);
            return;
        }
        if (type==Fingerprint.UNREGISTED_TYPE) {
            message="请到设置中设置指纹";
            listener.onEnrollFailed(type,message);//未注册
            return;
        }
        //开始指纹识别
        listener.onAuthenticationStart();
        mCancellationSignal = new CancellationSignal(); //必须重新实例化，否则cancel 过一次就不能再使用了
        managerCompat.authenticate(null, 0, mCancellationSignal, new FingerprintManagerCompat.AuthenticationCallback() {
            // 当出现错误的时候回调此函数，比如多次尝试都失败了的时候，errString是错误信息，比如华为的提示就是：尝试次数过多，请稍后再试。
            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                listener.onAuthenticationError(errMsgId, errString);
            }

            // 当指纹验证失败的时候会回调此函数，失败之后允许多次尝试，失败次数过多会停止响应一段时间然后再停止sensor的工作
            @Override
            public void onAuthenticationFailed() {
                listener.onAuthenticationFailed("验证失败");
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                listener.onAuthenticationHelp(helpMsgId, helpString);
            }

            // 当验证的指纹成功时会回调此函数，然后不再监听指纹sensor
            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                listener.onAuthenticationSucceeded(result);
            }
        }, null);
    }

    /**取消识别**/
    public void cancel() {
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
        }
    }

    /**判断设备是否支持指纹识别，第一步判断**/
    private boolean isSupportFingerprint() {
        return managerCompat.isHardwareDetected();
    }

    /**判断设备是否处于安全保护中，第二步判断**/
    private boolean isProtected() {
        KeyguardManager keyguardManager = (KeyguardManager)mContext.getSystemService(Context.KEYGUARD_SERVICE);
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && keyguardManager.isKeyguardSecure();
    }

    /**判断设备是否已经注册过指纹，第三步判断**/
    private boolean hasRegistedFingerprint() {
        return managerCompat.hasEnrolledFingerprints();
    }

    public interface OnCallBackListenr {
        void onSupportFailed(int type, String message);

        void onInsecurity(int type, String message);

        void onEnrollFailed(int type, String message);

        void onAuthenticationStart();

        void onAuthenticationError(int errMsgId, CharSequence errString);

        void onAuthenticationFailed(String message);

        void onAuthenticationHelp(int helpMsgId, CharSequence helpString);

        void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result);
    }

}
