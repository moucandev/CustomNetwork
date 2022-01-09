package com.moucan.customnetwork.httpUtils.utils;

/**
 * Created by lengqi on 2017/4/26.
 */
public abstract class NormalCallBack<T> {

    public abstract void onNext(T t);

    public abstract void onError(String errorMsg);

    public void onCompleted() { }

}
