package com.moucan.customnetwork.httpUtils.utils;

import android.text.TextUtils;

import com.cmcc.utilsmodule.http.response.BasicResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

public class ResponseResultFunction<T> implements Function<ResponseBody, BasicResponse<T>> {

    protected Type type;
    protected Gson gson;

    public ResponseResultFunction(Type type) {
        this.type = type;
        gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();
    }

    @Override
    public BasicResponse<T> apply(ResponseBody responseBody) throws Exception {
        BasicResponse<T> apiResult = new BasicResponse<>();
        String json = responseBody.string();
        if (type instanceof ParameterizedType) {
            Class<T> cls = (Class) ((ParameterizedType) type).getRawType();
            if (BasicResponse.class.isAssignableFrom(cls)) {
                final Type[] params = ((ParameterizedType) type).getActualTypeArguments();
                final Class clazz = CommonUtil.getClass(params[0], 0);
                final Class rawType = CommonUtil.getClass(type, 0);
                try {
                    if (!List.class.isAssignableFrom(rawType) && clazz.equals(String.class)) {
                        final Type type = CommonUtil.getType(cls, 0);
                        BasicResponse result = gson.fromJson(json, type);
                        if (result != null) {
                            apiResult = result;
                            apiResult.setData((T) json);
                        } else {
                            apiResult.setResultMsg("json is null");
                        }
                    } else {
                        BasicResponse result = gson.fromJson(json, type);
                        if (result != null) {
                            apiResult = result;
                        } else {
                            apiResult.setResultMsg("json is null");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    apiResult.setResultMsg(e.getMessage());
                } finally {
                    responseBody.close();
                }
            }

        } else {
            Class<T> clazz = CommonUtil.getClass(type, 0);
            try {
                BasicResponse baseResponse = parseApiResult(json, apiResult);
                if (baseResponse == null) {
                    apiResult.setResultMsg("json is null");
                } else {
                    apiResult = baseResponse;
                    if (apiResult.getData() == null) {
                        apiResult.setResultMsg("ApiResult's data is null");
                    } else {
                        T data = gson.fromJson(baseResponse.getData().toString(), clazz);
                        apiResult.setData(data);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                apiResult.setResultMsg(e.getMessage());
            }finally {
                responseBody.close();
            }
        }
        return apiResult;
    }


    private BasicResponse parseApiResult(String json, BasicResponse apiResult) throws JSONException {

        if (TextUtils.isEmpty(json)) {
            return null;
        }
        JSONObject jsonObject = new JSONObject(json);
        if (jsonObject.has("resultCode")) {
            apiResult.setResultCode(jsonObject.getString("resultCode"));
        }
        if (jsonObject.has("data")) {
            apiResult.setData(jsonObject.getString("data"));
        }
        if (jsonObject.has("resultMsg")) {
            apiResult.setResultMsg(jsonObject.getString("resultMsg"));
        }
        if (jsonObject.has("total")) {
            apiResult.setTotal(jsonObject.getInt("total"));
        }
        if (jsonObject.has("page")) {
            apiResult.setPage(jsonObject.getInt("page"));
        }
        if (jsonObject.has("pageSize")) {
            apiResult.setPageSize(jsonObject.getInt("pageSize"));
        }
        return apiResult;
    }
}
