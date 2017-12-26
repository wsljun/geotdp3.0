package com.geotdb.compile.vo;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;

/**
 * 所有的字典走在这
 * 暂时保留，对词典库的增删改成操作
 */
@DatabaseTable(tableName = "dictionary")
public class Dictionary implements Serializable {
    private static final long serialVersionUID = 1L;
    @DatabaseField
    private String sortNo;
    @DatabaseField
    private String relateID;
    @DatabaseField
    private String name;
    @DatabaseField
    private String sort;
    @DatabaseField
    private String type;//0.原本  1.用户数据  2.企业数据。。。。。。。
    @DatabaseField
    private String form;//区分岩土，回次等，存放类型
    public boolean isSelect;//字典库管理，全选需要的字段

    public Dictionary() {
    }

    public Dictionary(String type, String sort, String name, String sortNo, String relateID, String form) {
        this.type = type;
        this.sort = sort;
        this.name = name;
        this.sortNo = sortNo;
        this.relateID = relateID;
        this.form = form;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getSortNo() {
        return sortNo;
    }

    public void setSortNo(String sortNo) {
        this.sortNo = sortNo;
    }

    public String getRelateID() {
        return relateID;
    }

    public void setRelateID(String relateID) {
        this.relateID = relateID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
