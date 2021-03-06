package com.liangmayong.netbox.callbacks;

import com.liangmayong.netbox.response.Response;
import com.liangmayong.netbox.throwables.NetboxError;
import com.liangmayong.netbox.utils.NetboxUtils;

/**
 * Created by LiangMaYong on 2016/9/13.
 */
public abstract class OnNetboxCallback<T> extends OnNetboxListener {

    /**
     * handleResponseSuccess
     *
     * @param data data
     */
    public abstract void handleResponseSuccess(T data);

    /**
     * handleResponseHistory
     *
     * @param data data
     */
    public void handleResponseHistory(T data) {
    }

    /**
     * handleResponseError
     *
     * @param code     code
     * @param message  message
     * @param response response
     */
    public abstract void handleResponseError(String code, String message, Response response);

    /**
     * generateDefaultKey
     */
    public String generateDefaultKey() {
        return null;
    }

    @Override
    public void onResponse(Response response) {
        if (response.isSuccess()) {
            handleResponseSuccess(responseToT(response));
        } else {
            handleResponseError(response.getErrorCode(), response.getErrorMessage(), response);
        }
    }

    @Override
    public void onResponseHistory(Response response) {
        if (response.isSuccess()) {
            handleResponseHistory(responseToT(response));
        }
    }

    private T responseToT(Response response) {
        T data = null;
        if (response.isSuccess()) {
            try {
                data = response.getData(generateDefaultKey(), NetboxUtils.getGenericType(this, 0));
            } catch (Exception e) {
            }
        }
        return data;
    }

    @Override
    public abstract void onFailure(NetboxError error);

}
