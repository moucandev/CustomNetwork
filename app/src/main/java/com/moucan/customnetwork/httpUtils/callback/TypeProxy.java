package com.moucan.customnetwork.httpUtils.callback;

import com.cmcc.utilsmodule.http.utils.CommonUtil;

import java.lang.reflect.Type;

public abstract class TypeProxy<T> implements IType<T> {

    @Override
    public Type getType() {
        return CommonUtil.findNeedClass(getClass());
    }
}
