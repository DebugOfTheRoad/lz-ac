package com.lckjsoft.common.base;

import com.lckjsoft.common.constant.ConstantUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")

public class JsonResult<T> implements Serializable {

    private static final long serialVersionUID = -2410539351914134704L;
    private int code;
    private String message;
    private T data;
    private T[] datas;

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setDatas(T[] datas) {
        this.datas = datas;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public T[] getDatas() {
        return datas;
    }

    public static <T> JsonResult<T> success() {
        JsonResult<T> jsonResult = new JsonResult<T>();
        jsonResult.setCode(ConstantUtil.SUCCESS);
        jsonResult.setMessage("操作成功");
        return jsonResult;
    }

    public static <T> JsonResult<T> success(String message) {
        JsonResult<T> jsonResult = new JsonResult<T>();
        jsonResult.setCode(ConstantUtil.SUCCESS);
        jsonResult.setMessage(message);
        return jsonResult;
    }

    public static <T> JsonResult<T> success(String message, T data) {
        JsonResult<T> jsonResult = new JsonResult<T>();
        jsonResult.setCode(ConstantUtil.SUCCESS);
        jsonResult.setMessage(message);
        jsonResult.setData(data);
        return jsonResult;
    }

    public static <T> JsonResult<T> success(String message, T... datas) {
        JsonResult<T> jsonResult = new JsonResult<T>();
        jsonResult.setCode(ConstantUtil.SUCCESS);
        jsonResult.setMessage(message);
        jsonResult.setDatas(datas);
        return jsonResult;
    }

    public static <T> JsonResult<T> success(T data) {
        JsonResult<T> jsonResult = new JsonResult<T>();
        jsonResult.setCode(ConstantUtil.SUCCESS);
        jsonResult.setMessage("操作成功");
        jsonResult.setData(data);
        return jsonResult;
    }

    public static <T> JsonResult<T> success(T... datas) {
        JsonResult<T> jsonResult = new JsonResult<T>();
        jsonResult.setCode(ConstantUtil.SUCCESS);
        jsonResult.setMessage("操作成功");
        jsonResult.setDatas(datas);
        return jsonResult;
    }

    public static <T> JsonResult<T> fail() {
        JsonResult<T> jsonResult = new JsonResult<T>();
        jsonResult.setCode(ConstantUtil.FAIL);
        jsonResult.setMessage("操作失败");
        return jsonResult;
    }

    public static <T> JsonResult<T> fail(String message) {
        JsonResult<T> jsonResult = new JsonResult<T>();
        jsonResult.setCode(ConstantUtil.FAIL);
        jsonResult.setMessage(message);
        return jsonResult;
    }

    public static <T> JsonResult<T> fail(String message, Throwable e) {
        JsonResult<T> jsonResult = new JsonResult<T>();
        jsonResult.setCode(ConstantUtil.FAIL);
        jsonResult.setMessage(e.getMessage());
        return jsonResult;
    }

    public static <T> JsonResult<T> fail(String message, Exception e) {
        JsonResult<T> jsonResult = new JsonResult<T>();
        jsonResult.setCode(ConstantUtil.FAIL);
        jsonResult.setMessage(e.getMessage());
        return jsonResult;
    }

    public static <T> JsonResult<T> error() {
        JsonResult<T> jsonResult = new JsonResult<T>();
        jsonResult.setCode(ConstantUtil.ERROR);
        jsonResult.setMessage("发生错误");
        return jsonResult;
    }

    public static <T> JsonResult<T> error(String message) {
        JsonResult<T> jsonResult = new JsonResult<T>();
        jsonResult.setCode(ConstantUtil.ERROR);
        jsonResult.setMessage(message);
        return jsonResult;
    }

    public static <T> JsonResult<T> error(Throwable e) {
        JsonResult<T> jsonResult = new JsonResult<T>();
        jsonResult.setCode(ConstantUtil.ERROR);
        jsonResult.setMessage(e.getMessage());
        return jsonResult;
    }

    public static <T> JsonResult<T> error(Exception e) {
        JsonResult<T> jsonResult = new JsonResult<T>();
        jsonResult.setCode(ConstantUtil.ERROR);
        jsonResult.setMessage(e.getMessage());
        return jsonResult;
    }

    public static <T> JsonResult<T> result(int code) {
        JsonResult<T> jsonResult = new JsonResult<T>();
        jsonResult.setCode(code);
        return jsonResult;
    }

    public static <T> JsonResult<T> result(int code, String message) {
        JsonResult<T> jsonResult = new JsonResult<T>();
        jsonResult.setCode(code);
        jsonResult.setMessage(message);
        return jsonResult;
    }

    public static <T> JsonResult<T> result(int code, T data) {
        JsonResult<T> jsonResult = new JsonResult<T>();
        jsonResult.setCode(code);
        jsonResult.setData(data);
        return jsonResult;
    }

    public static <T> JsonResult<T> result(int code, T... datas) {
        JsonResult<T> jsonResult = new JsonResult<T>();
        jsonResult.setCode(code);
        jsonResult.setDatas(datas);
        return jsonResult;
    }

    public static <T> JsonResult<T> result(int code, String message, T data) {
        JsonResult<T> jsonResult = new JsonResult<T>();
        jsonResult.setCode(code);
        jsonResult.setMessage(message);
        jsonResult.setData(data);
        return jsonResult;
    }

    public static <T> JsonResult<T> result(int code, String message, T... datas) {
        JsonResult<T> jsonResult = new JsonResult<T>();
        jsonResult.setCode(code);
        jsonResult.setMessage(message);
        jsonResult.setDatas(datas);
        return jsonResult;
    }

    public static <T> Map<String, Object> successMap(String message) {
        Map<String, Object> resultMap = new HashMap<String, Object>(2);
        resultMap.put("code", ConstantUtil.SUCCESS);
        resultMap.put("message", message);
        return resultMap;
    }

    public static <T> Map<String, Object> successMap(T data) {
        Map<String, Object> resultMap = new HashMap<String, Object>(2);
        resultMap.put("code", ConstantUtil.SUCCESS);
        resultMap.put("data", data);
        return resultMap;
    }

    public static <T> Map<String, Object> failMap(String message) {
        Map<String, Object> resultMap = new HashMap<String, Object>(2);
        resultMap.put("code", ConstantUtil.FAIL);
        resultMap.put("message", message);
        return resultMap;
    }

    public static <T> Map<String, Object> errorMap(String message) {
        Map<String, Object> resultMap = new HashMap<String, Object>(2);
        resultMap.put("code", ConstantUtil.ERROR);
        resultMap.put("message", message);
        return resultMap;
    }

    public static <T> Map<String, Object> resultMap(int code) {
        Map<String, Object> resultMap = new HashMap<String, Object>(1);
        resultMap.put("code", code);
        return resultMap;
    }

    public static <T> Map<String, Object> resultMap(int code, String message) {
        Map<String, Object> resultMap = new HashMap<String, Object>(2);
        resultMap.put("code", code);
        resultMap.put("message", message);
        return resultMap;
    }

    public static <T> Map<String, Object> resultMap(int code, T data) {
        Map<String, Object> resultMap = new HashMap<String, Object>(2);
        resultMap.put("code", code);
        resultMap.put("data", data);
        return resultMap;
    }

    public static <T> Map<String, Object> resultMap(int code, T... datas) {
        Map<String, Object> resultMap = new HashMap<String, Object>(2);
        resultMap.put("code", code);
        resultMap.put("datas", datas);
        return resultMap;
    }

    public static <T> Map<String, Object> resultMap(int code, String message, T data) {
        Map<String, Object> resultMap = new HashMap<String, Object>(3);
        resultMap.put("code", code);
        resultMap.put("message", message);
        resultMap.put("data", data);
        return resultMap;
    }

    public static <T> Map<String, Object> resultMap(int code, String message, T... datas) {
        Map<String, Object> resultMap = new HashMap<String, Object>(3);
        resultMap.put("code", code);
        resultMap.put("message", message);
        resultMap.put("datas", datas);
        return resultMap;
    }

}
