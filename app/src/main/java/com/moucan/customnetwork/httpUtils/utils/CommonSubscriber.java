package com.moucan.customnetwork.httpUtils.utils;

import android.text.TextUtils;

import com.cmcc.utilsmodule.http.exception.BasicException;
import com.cmcc.utilsmodule.http.exception.SMSVerificationException;
import com.cmcc.utilsmodule.http.exception.TokenExpireException;
import com.cmcc.utilsmodule.manager.RxManager;
import com.cmcc.utilsmodule.utils.NetworkUtils;
import com.cmcc.utilsmodule.utils.ToastUtils;

import java.net.UnknownHostException;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * 请求结果状态回调
 *
 */

public abstract class CommonSubscriber<T> extends ResourceSubscriber<T> {

    private NormalCallBack mCallBack;
    private Object tag;
    private static long preShowTime;

    protected CommonSubscriber(NormalCallBack callback) {
        this.mCallBack = callback;
    }

    protected CommonSubscriber(Object tag, NormalCallBack callBack){
        this.tag = tag;
        this.mCallBack = callBack;
    }


    @Override
    public void onComplete() {
        if (mCallBack == null) {
            return;
        }
        mCallBack.onCompleted();
    }

    @Override
    public void onError(Throwable e) {
        if (mCallBack == null) {
            return;
        }
        String errorMsg = ErrorCodeTable.getMsg("000001");
        if (e instanceof BasicException) {
            String code = ((BasicException) e).getErrorCode();
            if (TextUtils.isEmpty(ErrorCodeTable.getMsg(code))) {
                errorMsg = e.getMessage();
            } else {
                errorMsg = ErrorCodeTable.getMsg(code) + "(" + code + ")";
            }
        } else if (e instanceof TokenExpireException) {
            errorMsg =  ErrorCodeTable.getMsg(((TokenExpireException) e).errorCode);
        } else if(e instanceof SMSVerificationException){
            errorMsg = "04500";
        }else if (e instanceof UnknownHostException) {
            errorMsg = "网络连接失败，请检查您的网络";
        }
        mCallBack.onError(errorMsg);

    }

    @Override
    public void onNext(T result) {
        if (mCallBack == null){
            return;
        }
        mCallBack.onNext(result);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!NetworkUtils.isConnected()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - preShowTime > 5000) {//***让无网络显示间隔不小于5s
                ToastUtils.showShort("当前网络不可用，请检查网络情况");
                preShowTime = currentTime;
            }
            if (isDisposed()) {
                dispose();
            }
        } else {
            if (tag != null) {
                RxManager.getInstance().addRx(tag, this);
            }
        }
    }
}
