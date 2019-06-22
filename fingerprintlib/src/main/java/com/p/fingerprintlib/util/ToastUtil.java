package com.p.fingerprintlib.util;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.p.fingerprintlib.R;


/**
 * Description:
 * <p>
 * Author:pei
 * Date: 2019/5/23
 */
public class ToastUtil {

    private static ToastUtil TD;

    private Toast mToast;
    private String msg;

    public static void show(int resId,Context context){
        show(context.getString(resId),context);
    }

    public static void show(String msg,Context context) {
        if (TD == null) {
            TD = new ToastUtil();
        }
        TD.setText(msg);
        TD.create(context).show();
    }

    public static void shortShow(String msg,Context context) {
        if (TD == null) {
            TD = new ToastUtil();
        }
        TD.setText(msg);
        TD.createShort(context).show();
    }

    private Toast create(Context context) {
        View contentView = View.inflate(context, R.layout.dialog_toast, null);
        TextView tvMsg = (TextView) contentView.findViewById(R.id.tv_toast_msg);
        mToast = new Toast(context);
        mToast.setView(contentView);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setDuration(Toast.LENGTH_LONG);
        tvMsg.setText(msg);
        return mToast;
    }

    private Toast createShort(Context context) {
        View contentView = View.inflate(context, R.layout.dialog_toast, null);
        TextView tvMsg = (TextView) contentView.findViewById(R.id.tv_toast_msg);
        mToast = new Toast(context);
        mToast.setView(contentView);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setDuration(Toast.LENGTH_SHORT);
        tvMsg.setText(msg);
        return mToast;
    }

    private void show() {
        if (mToast != null){
            mToast.show();
        }
    }

    private void setText(String text) {
        msg = text;
    }

}
