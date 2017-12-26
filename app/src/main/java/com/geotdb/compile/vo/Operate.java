package com.geotdb.compile.vo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 操作痕迹表，关联到记录，修改前和修改后
 * hole修改后数据是当前数据，修改前的原数据改变id再保存
 *
 * 暂时不用
 */
@DatabaseTable(tableName = "operate")
public class Operate implements Serializable {
    private static final long serialVersionUID = 1L;
    @DatabaseField(columnName = "id", id = true)
    private String id;
    @DatabaseField
    private String createTime;
    @DatabaseField
    private String updateTime;
    @DatabaseField
    private String reocrdId;//记录的id作为外键
    @DatabaseField
    private String recordType;//记录的类型（回次、岩土）
    @DatabaseField
    private String operateType;//操作的类型（修改、删除）
    @DatabaseField
    private String recordUpdateId;//记录的修改id，原纪录的id作为修改id，并吧原纪录id清空（或者做个标记）

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecordUpdateId() {
        return recordUpdateId;
    }

    public void setRecordUpdateId(String recordUpdateId) {
        this.recordUpdateId = recordUpdateId;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getReocrdId() {
        return reocrdId;
    }

    public void setReocrdId(String reocrdId) {
        this.reocrdId = reocrdId;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
