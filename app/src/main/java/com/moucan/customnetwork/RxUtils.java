package com.moucan.customnetwork;

import android.view.View;

import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.trello.rxlifecycle3.android.FragmentEvent;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle3.components.support.RxFragment;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class RxUtils {

    /**
     * 绑定 Activity/Fragment 的生命周期
     *
     * @param view
     * @param <T>
     * @return
     */
    public static <T> LifecycleTransformer<T> bindToLifecycle(@NonNull IView view) {
        //如果是XRecyclerView,使用getContext获得上下文，再进行下一步的判断
        if (view instanceof LifecycleProvider) {
            return bindToLifecycle((LifecycleProvider) view);
        } else {
            throw new IllegalArgumentException("view isn't Lifecycleable");
        }
    }

    public static <T> LifecycleTransformer<T> bindToLifecycle(@NonNull LifecycleProvider lifecycleable) {
        if (lifecycleable instanceof RxAppCompatActivity) {
            return ((RxAppCompatActivity) lifecycleable).bindUntilEvent(ActivityEvent.DESTROY);
        } else if (lifecycleable instanceof RxFragment) {
            return ((RxFragment) lifecycleable).bindUntilEvent(FragmentEvent.DESTROY_VIEW);
        } else {
            throw new IllegalArgumentException("Lifecycleable not match");
        }
    }

    /**
     * 防止view重复点击
     *
     * @param view
     * @param duration 该时间段里只响应第一次点击
     * @param listener
     */
    public static void clickView(View view, int duration, View.OnClickListener listener) {
        Observable.create(new ViewClickOnSubscribe(view))
                .throttleFirst(duration, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        if (listener != null && view != null && view.getContext() != null) {
                            listener.onClick(view);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private static class ViewClickOnSubscribe implements ObservableOnSubscribe {
        private View view;

        public ViewClickOnSubscribe(View view) {
            this.view = view;
        }

        @Override
        public void subscribe(ObservableEmitter e) throws Exception {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!e.isDisposed()) {
                        e.onNext(view);
                    }
                }
            });
        }
    }


}