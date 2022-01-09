package com.moucan.customnetwork.httpUtils.callback;

import com.cmcc.utilsmodule.http.response.BasicResponse;
import com.cmcc.utilsmodule.http.utils.CommonUtil;
import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;

public abstract class CallClazzProxy<T extends BasicResponse<R>, R> implements IType<T> {
    private Type type;

    public CallClazzProxy(Type type) {
        this.type = type;
    }

    public Type getCallType() {
        return type;
    }

    //获取泛型中的最外层的参数类型
    @Override
    public Type getType() {
        Type typeArguments = null;
        if (type != null) {
            typeArguments = type;
        }
        if (typeArguments == null) {
            typeArguments = ResponseBody.class;
        }
        Type rawType = CommonUtil.findNeedType(getClass());
        if (rawType instanceof ParameterizedType) {
            rawType = ((ParameterizedType) rawType).getRawType();
        }
        return $Gson$Types.newParameterizedTypeWithOwner(null, rawType, typeArguments);
    }
}
