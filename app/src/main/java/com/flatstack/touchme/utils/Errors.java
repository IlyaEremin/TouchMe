package com.flatstack.touchme.utils;

import rx.functions.Action1;

/**
 * Created by Ilya Eremin on 10/30/15.
 */
public class Errors {
    public static Action1<Throwable> onError() {
        return new Action1<Throwable>() {
            @Override public void call(Throwable throwable) {
                Logger.d(throwable.getMessage());
            }
        };
    }
}
