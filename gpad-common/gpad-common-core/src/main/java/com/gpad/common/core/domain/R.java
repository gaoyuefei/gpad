package com.gpad.common.core.domain;

import java.io.Serializable;
import com.gpad.common.core.constant.Constants;
import com.gpad.common.core.utils.uuid.TraceIdUtil;

/**
 * 响应信息主体
 *
 * @author by
 */
public class R<T> implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 成功 */
    public static final int SUCCESS = Constants.SUCCESS;

    /** 失败 */
    public static final int FAIL = Constants.FAIL;

    private int code;

    private String msg;

    private T data;

    private String traceId;


    public static <T> R<T> ok()
    {
        return restResult(null, SUCCESS, null,TraceIdUtil.generateHeaderTraceId());
    }

    public static <T> R<T> ok(T data)
    {
        return restResult(data, SUCCESS, null,TraceIdUtil.generateHeaderTraceId());
    }

    public static <T> R<T> ok(T data, String msg)
    {
        return restResult(data, SUCCESS, msg,TraceIdUtil.generateHeaderTraceId());
    }

    public static <T> R<T> ok(T data,int code ,String msg)
    {
        return restResult(data, code, msg,TraceIdUtil.generateHeaderTraceId());
    }

    public static <T> R<T> ok(T data,int code ,String msg,String traceId)
    {
        return restResult(data, code, msg,TraceIdUtil.generateHeaderTraceId());
    }

    public static <T> R<T> fail()
    {
        return restResult(null, FAIL, null,TraceIdUtil.generateHeaderTraceId());
    }

    public static <T> R<T> fail(String msg)
    {
        return restResult(null, FAIL, msg,TraceIdUtil.generateHeaderTraceId());
    }

    public static <T> R<T> fail(T data)
    {
        return restResult(data, FAIL, null,TraceIdUtil.generateHeaderTraceId());
    }

    public static <T> R<T> fail(T data, String msg)
    {
        return restResult(data, FAIL, msg,TraceIdUtil.generateHeaderTraceId());
    }

    public static <T> R<T> fail(int code, String msg)
    {
        return restResult(null, code, msg,TraceIdUtil.generateHeaderTraceId());
    }

    public static <T> R<T> fail(T data, int code ,String msg)
    {
        return restResult(data, code, msg,TraceIdUtil.generateHeaderTraceId());
    }

    private static <T> R<T> restResult(T data, int code, String msg)
    {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

    private static <T> R<T> restResult(T data, int code, String msg,String traceId)
    {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        apiResult.setTraceId(traceId);
        return apiResult;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }

    public static <T> Boolean isError(R<T> ret)
    {
        return !isSuccess(ret);
    }

    public static <T> Boolean isSuccess(R<T> ret)
    {
        return R.SUCCESS == ret.getCode();
    }
}
