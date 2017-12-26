package com.geotdb.compile.vo;

import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.vo.Dictionary;
import com.google.gson.annotations.Expose;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Json结果
 * 定义该对象,是希望所有的请求返回的格式能正规一点.
 * @author XuFeng
 * @version 1.0
 */
public class JsonResult{

    // 请求是否成功
    private boolean status;
    // 服务器的消息(这里面的话是直接提供给用户看的)
    private String message;
    // 结果
    private String result;
    // 结果2
    private String result2;
    // 返回结果数
    private String resultCount;
    // 总结果数
    private String totalCount;
    // 总页数
    private int totalPage=0;

    public JsonResult(){

    }

    public JsonResult(JSONObject json){
        try {
            this.status =json.getBoolean("status");
            this.message = json.getString("message");
            this.result = json.getString("result");
            this.result2 = json.getString("result2");
            this.resultCount = json.getString("resultCount");
            this.totalCount = json.getString("totalCount");
            this.totalPage = json.getInt("totalPage");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JsonResult(boolean status,String message,String result){
        this.status = status;
        this.message = message;
        this.result = result;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }

    public String getResult2() {
        return result2;
    }

    public void setResult2(String result2) {
        this.result2 = result2;
    }

    public Integer getResultCount() {
        return Integer.valueOf(resultCount);
    }

    public void setResultCount(String resultCount) {
        this.resultCount = resultCount;
    }

    public Integer getTotalCount() {
        return Integer.valueOf(totalCount);
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }



}
