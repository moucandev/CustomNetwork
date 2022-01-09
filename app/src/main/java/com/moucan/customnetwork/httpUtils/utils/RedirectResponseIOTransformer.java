package com.moucan.customnetwork.httpUtils.utils;

import com.cmcc.utilsmodule.http.exception.BasicException;
import com.cmcc.utilsmodule.http.exception.TokenExpireException;
import com.cmcc.utilsmodule.http.response.BasicResponse;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableTransformer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 将请求和接收的线程都放在子线程中
 * @param <T>
 */
public class RedirectResponseIOTransformer<T> implements FlowableTransformer<BasicResponse<T>,T> {
    @Override
    public Flowable<T> apply(Flowable<BasicResponse<T>> upstream) {

        return upstream.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap((Function<BasicResponse<T>, Flowable<T>>) tBaseResponse -> {
                    if (tBaseResponse.isSuccess()) {
                        return createData(tBaseResponse.getData());
                    } else if (tBaseResponse.isTokenExpire()) {
                        return Flowable.error(new TokenExpireException(tBaseResponse.resultCode));
                    } else {
                        return Flowable.error(new BasicException(tBaseResponse.resultCode, tBaseResponse.getResultMsg()));
                    }
                });
    }

    /**
     * 生成Flowable
     *
     * @param <T>
     * @return
     */
    public static <T> Flowable<T> createData(final T t) {
        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(FlowableEmitter<T> emitter) throws Exception {
                try {
                    emitter.onNext(t);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }, BackpressureStrategy.BUFFER);
    }
}
