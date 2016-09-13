package com.liangmayong.netbox.defualts;

import com.liangmayong.netbox.interfaces.OnNetBoxListener;
import com.liangmayong.netbox.response.Response;
import com.liangmayong.netbox.throwables.NetBoxError;
import com.liangmayong.netbox.utils.NetBoxUtils;

import java.util.List;

/**
 * Created by LiangMaYong on 2016/9/13.
 */
public abstract class OnDefualtListNetBoxListener<T> implements OnNetBoxListener<Response> {

    /**
     * handleResponseSuccess
     *
     * @param data data
     */
    public abstract void handleResponseSuccess(List<T> data);

    /**
     * handleResponseError
     *
     * @param code    code
     * @param message message
     */
    public abstract void handleResponseError(String code, String message);

    /**
     * generateDefualtKey
     *
     * @param response response
     */
    public String generateDefualtKey(Response response) {
        return response.getDefualtKey();
    }

    @Override
    public void onResponse(Response response) {
        if (response.isSuccess()) {
            Class<T> clazz = null;
            List<T> data = null;
            try {
                clazz = (Class<T>) NetBoxUtils.getGenericClass(this, 0);
                data = response.getList(generateDefualtKey(response), clazz);
            } catch (Exception e) {
            }
            handleResponseSuccess(data);
        } else {
            handleResponseError(response.getErrorCode(), response.getErrorMessage());
        }
    }

    @Override
    public abstract void onFailure(NetBoxError error);
}
