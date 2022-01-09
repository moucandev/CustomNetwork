package com.moucan.customnetwork.httpUtils.utils;

import com.cmcc.utilsmodule.http.exception.BasicException;
import com.cmcc.utilsmodule.http.exception.SMSVerificationException;
import com.cmcc.utilsmodule.http.exception.TokenExpireException;
import com.cmcc.utilsmodule.http.response.BasicResponse;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 简单的请求接口的线程切换
 * @param <T>
 */
public class NormalFlowableTransformer<T> implements FlowableTransformer<BasicResponse<T>,BasicResponse> {


    @Override
    public Flowable<BasicResponse> apply(Flowable<BasicResponse<T>> upstream) {
        return upstream.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap((Function<BasicResponse<T>, Flowable<BasicResponse>>) tBaseResponse -> {
                    if (tBaseResponse.isSuccess()) {
                        BasicResponse basicResponse = tBaseResponse;
                        return Flowable.create(emitter -> {
                            emitter.onNext(basicResponse);
                            emitter.onComplete();
                        }, BackpressureStrategy.BUFFER);
                    } else if (tBaseResponse.isTokenExpire()) {
                        return Flowable.error(new TokenExpireException(tBaseResponse.resultCode));
                    } else if (tBaseResponse.isNeedSMSVerification()) {
                        return Flowable.error(new SMSVerificationException());
                    } else {
                        return Flowable.error(new BasicException(tBaseResponse.resultCode, tBaseResponse.getResultMsg()));
                    }
                });
    }
}
