package com.liangmayong.netbox;

import android.content.Context;

import com.liangmayong.netbox.concretes.Method;
import com.liangmayong.netbox.concretes.Parameter;
import com.liangmayong.netbox.interfaces.NetboxCache;
import com.liangmayong.netbox.interfaces.OnNetboxListener;
import com.liangmayong.netbox.response.Response;
import com.liangmayong.netbox.throwables.NetboxError;
import com.liangmayong.netbox.utils.NetboxUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LiangMaYong on 2016/9/13.
 */
public final class NetboxPath {

    // server type
    private Class<? extends NetboxServer> mServerType = null;
    // method
    private Method mMethod = Method.GET;
    // path
    private String mPath = "";
    // params
    private final Map<String, String> mParams;
    // headers
    private final Map<String, String> mHeaders;

    /**
     * NetboxPath
     *
     * @param actionType actionType
     * @param path       path
     */
    public NetboxPath(Class<? extends NetboxServer> actionType, String path) {
        this.mServerType = actionType;
        this.mPath = path;
        NetboxConfig boxConfig = NetboxConfig.getInstance(actionType);
        this.mParams = boxConfig.getCommonParams();
        this.mHeaders = boxConfig.getCommonHeaders();
    }

    /**
     * method
     *
     * @param method method
     * @return path
     */
    public NetboxPath method(Method method) {
        this.mMethod = method;
        return this;
    }

    /**
     * param
     *
     * @param key   key
     * @param param param
     * @return path
     */
    public NetboxPath param(String key, String param) {
        if (key != null) {
            if (param != null) {
                mParams.put(key, param);
            } else {
                mParams.remove(key);
            }
        }
        return this;
    }

    /***
     * header
     *
     * @param key    key
     * @param header header
     * @return path
     */
    public NetboxPath header(String key, String header) {
        if (key != null) {
            if (header != null) {
                mHeaders.put(key, header);
            } else {
                mHeaders.remove(key);
            }
        }
        return this;
    }


    /**
     * exec
     *
     * @param context  context
     * @param listener listener
     */
    public void exec(Context context, final OnNetboxListener listener) {
        if (context == null) {
            throw new IllegalArgumentException("The calling method exec must have context parameters,and context not null");
        }
        String requestUrl = NetboxUtils.parseUrl(Netbox.server(mServerType).generateBaseUrl(), mPath);
        Parameter parameter = new Parameter(mMethod, requestUrl, mParams, mHeaders);
        Netbox.generateInterceptor(Netbox.server(mServerType).generateInterceptorType()).execRequest(context, parameter, new OnNetboxListener() {
            @Override
            public void onResponse(Response response) {
                response.setConverter(Netbox.server(mServerType).generateConverterType());
                String cacheKey = NetboxUtils.generateCacheKey(response.getMethod(), response.getUrl(), response.getParams(), response.getHeaders());
                Netbox.generateCache(Netbox.server(mServerType).generateCacheType()).saveCache(cacheKey, response.getBody());
                handleResponse(response);
                listener.onResponse(response);
            }

            @Override
            public void onFailure(NetboxError error) {
                handleFailure(error);
                listener.onFailure(error);
            }
        });
    }

    /**
     * sync
     *
     * @param context context
     * @return response
     */
    public Response sync(Context context) throws NetboxError {
        if (context == null) {
            throw new IllegalArgumentException("The calling method exec must have context parameters,and context not null");
        }
        try {
            String requestUrl = NetboxUtils.parseUrl(Netbox.server(mServerType).generateBaseUrl(), mPath);
            Parameter parameter = new Parameter(mMethod, requestUrl, mParams, mHeaders);
            Response response = Netbox.generateInterceptor(Netbox.server(mServerType).generateInterceptorType()).syncRequest(context, parameter);
            String cacheKey = NetboxUtils.generateCacheKey(response.getMethod(), response.getUrl(), response.getParams(), response.getHeaders());
            Netbox.generateCache(Netbox.server(mServerType).generateCacheType()).saveCache(cacheKey, response.getBody());
            handleResponse(response);
            return response;
        } catch (NetboxError error) {
            handleFailure(error);
            throw error;
        }
    }

    /**
     * cache
     *
     * @return response
     */
    public Response cache() {
        String requestUrl = NetboxUtils.parseUrl(Netbox.server(mServerType).generateBaseUrl(), mPath);
        String cacheKey = NetboxUtils.generateCacheKey(mMethod, requestUrl, mParams, mHeaders);
        NetboxCache cache = Netbox.generateCache(Netbox.server(mServerType).generateCacheType());
        String body = cache.getCache(cacheKey);
        if (body != null && !"".equals(body)) {
            Response response = new Response();
            response.setUrl(requestUrl);
            response.setConverter(Netbox.server(mServerType).generateConverterType());
            response.setParams(new HashMap<String, String>(mParams));
            response.setHeaders(new HashMap<String, String>(mHeaders));
            return response;
        }
        return null;
    }


    /**
     * handleResponse
     *
     * @param response response
     */
    private void handleResponse(Response response) {
        if (!Netbox.server(mServerType).handleResponse(response)) {
            if (NetboxUtils.isDebugable()) {
                NetboxUtils.debugLog("+=====================================+", null);
                NetboxUtils.debugLog("url = " + response.getUrl(), null);
                NetboxUtils.debugLog("method = " + response.getMethod().name(), null);
                NetboxUtils.debugLog("body = " + response.getBody(), null);
                if (response.getParams() != null && !response.getParams().isEmpty()) {
                    NetboxUtils.debugLog("params = " + response.getParams(), null);
                }
                if (response.getHeaders() != null && !response.getHeaders().isEmpty()) {
                    NetboxUtils.debugLog("headers = " + response.getHeaders(), null);
                }
            }
        }
    }

    /**
     * handleFailure
     *
     * @param error error
     */
    private void handleFailure(NetboxError error) {
        if (!Netbox.server(mServerType).handleFailure(error)) {
            if (NetboxUtils.isDebugable()) {
                NetboxUtils.debugLog("onFailure:", error);
            }
        }
    }
}
