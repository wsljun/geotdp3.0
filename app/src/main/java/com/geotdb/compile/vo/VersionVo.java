package com.geotdb.compile.vo;

import java.io.Serializable;

/**
 * 版本信息
 *
 * @author XuFeng
 */

public class VersionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    // 设置为自增长的主键
    private String id; // id
    String type = "";        //0.优化更新(不以弹出框)1.建议更新(以弹出框提示).2.必须更新(弹出框,取消按钮不能点.)
    String name = "";
    String code = "";
    String size = "";
    String downloadLink = "";
    String description = "";


    public VersionVo() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }


}
