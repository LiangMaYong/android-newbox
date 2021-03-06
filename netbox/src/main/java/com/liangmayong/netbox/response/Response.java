package com.liangmayong.netbox.response;

import com.liangmayong.netbox.Netbox;
import com.liangmayong.netbox.defaults.abstracts.AbstractDefaultNetboxConverter;
import com.liangmayong.netbox.interfaces.NetboxConverter;
import com.liangmayong.netbox.interfaces.NetboxResponse;
import com.liangmayong.netbox.params.FileParam;
import com.liangmayong.netbox.params.Method;
import com.liangmayong.netbox.params.Request;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liangmayong on 2016/9/12.
 */
public final class Response implements NetboxResponse {

    // body
    private String mBody = "";
    // url
    private String mUrl = "";
    // method
    private Method mMethod = Method.GET;
    // defualtKey
    private String mDefualtKey = null;
    // request time
    private long requestTime = 0;
    // response time
    private long responseTime = 0;
    // converterTypes
    private Class<? extends NetboxConverter> mConverterType = AbstractDefaultNetboxConverter.class;
    // mParams
    private final Map<String, String> mParams = new HashMap<String, String>();
    // mHeaders
    private final Map<String, String> mHeaders = new HashMap<String, String>();
    // mFiles
    private final Map<String, FileParam> mFiles = new HashMap<String, FileParam>();
    // Object
    private Object mTag = null;
    // isConverter
    private boolean isConverter = false;

    public Response(Method method, String url, Map<String, String> params, Map<String, String> headers) {
        setUrl(url);
        setParams(params);
        setHeaders(headers);
        setMethod(method);
    }

    public Response(Request request) {
        if (request != null) {
            setUrl(request.getUrl());
            setParams(request.getParams());
            setFiles(request.getFiles());
            setHeaders(request.getHeaders());
            setMethod(request.getMethod());
        }
    }

    public Response() {
    }

    /**
     * getBody
     *
     * @return body
     */
    public String getBody() {
        if (!isConverter) {
            mBody = getConverter().converterBody(mBody);
            isConverter = true;
        }
        return mBody;
    }

    /**
     * getHeaders
     *
     * @return mHeaders
     */
    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    /**
     * getParams
     *
     * @return mParams
     */
    public Map<String, String> getParams() {
        return mParams;
    }

    /**
     * getParams
     *
     * @return mParams
     */
    public Map<String, FileParam> getFiles() {
        return mFiles;
    }

    /**
     * getUrl
     *
     * @return url
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * getTag
     *
     * @return extra
     */
    public Object getTag() {
        return mTag;
    }

    /**
     * setTag
     *
     * @param tag tag
     */
    public void setTag(Object tag) {
        this.mTag = tag;
    }

    /**
     * setMethod
     *
     * @param method method
     */
    private void setMethod(Method method) {
        this.mMethod = method;
    }

    /**
     * getMethod
     *
     * @return method
     */
    public Method getMethod() {
        return mMethod;
    }

    /**
     * setUrl
     *
     * @param url url
     */
    private void setUrl(String url) {
        this.mUrl = url;
    }

    /**
     * setBody
     *
     * @param body body
     */
    public void setBody(String body) {
        this.mBody = body;
    }

    /**
     * setHeaders
     *
     * @param headers headers
     */
    private void setHeaders(Map<String, String> headers) {
        this.mHeaders.clear();
        this.mHeaders.putAll(headers);
    }

    /**
     * setFiles
     *
     * @param files files
     */
    private void setFiles(Map<String, FileParam> files) {
        this.mFiles.clear();
        this.mFiles.putAll(files);
    }

    /**
     * setParams
     *
     * @param params params
     */
    private void setParams(Map<String, String> params) {
        this.mParams.clear();
        this.mParams.putAll(params);
    }

    /**
     * setDefualtKey
     *
     * @param defualtKey defualtKey
     */
    public void setDefualtKey(String defualtKey) {
        this.mDefualtKey = defualtKey;
    }

    /**
     * getDefualtKey
     *
     * @return defualtKey
     */
    public String getDefualtKey() {
        if (mDefualtKey == null || "".equals(mDefualtKey)) {
            mDefualtKey = getConverter().converterDefualtKey();
        }
        return mDefualtKey;
    }

    /**
     * getConverter
     *
     * @return converter
     */
    private NetboxConverter getConverter() {
        return Netbox.generateConverter(mConverterType);
    }

    /**
     * setConverter
     *
     * @param converterType converterType
     */
    public void setConverter(Class<? extends NetboxConverter> converterType) {
        if (converterType == null) {
            converterType = AbstractDefaultNetboxConverter.class;
        }
        this.mConverterType = converterType;
    }

    @Override
    public boolean isSuccess() {
        return getConverter().isSuccess(getBody());
    }

    @Override
    public boolean isExist(String key, String value) {
        return getConverter().isExist(key, value, getBody());
    }

    @Override
    public long getTimestamp() {
        return getConverter().converterTimestamp(getBody());
    }

    @Override
    public String getErrorMessage() {
        return getConverter().converterErrorMessage(getBody());
    }

    @Override
    public String getErrorCode() {
        return getConverter().converterErrorCode(getBody());
    }

    @Override
    public <T> T getData(Type type) {
        return getConverter().converterData(getData(getDefualtKey()), type);
    }

    @Override
    public String getData(String key) {
        if (key == null || "".equals(key)) {
            return getBody();
        }
        return getConverter().converterKey(key, getBody());
    }

    @Override
    public <T> T getData(String key, Type type) {
        if (key == null) {
            key = getDefualtKey();
        }
        return getConverter().converterData(getData(key), type);
    }

    /**
     * getConsumingTime
     *
     * @return consumingTime
     */
    public long getConsumingTime() {
        return responseTime - requestTime;
    }

    @Override
    public String toString() {
        return "Response{" +
                "Body='" + mBody + '\'' +
                ", Url='" + mUrl + '\'' +
                ", Timestamp='" + getTimestamp() + '\'' +
                ", Mod=" + mMethod +
                ", Params=" + mParams +
                ", Headers=" + mHeaders +
                ", Files=" + mFiles +
                ", Tag=" + mTag +
                '}';
    }
}
