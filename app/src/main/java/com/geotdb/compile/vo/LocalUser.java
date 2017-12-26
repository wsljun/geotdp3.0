package com.geotdb.compile.vo;

import java.io.Serializable;

import android.database.Cursor;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


/**
 * 本地用户
 *
 * @author XuFeng
 */
@DatabaseTable(tableName = "company_user")
public class LocalUser implements Serializable {
    private static final long serialVersionUID = 1L;
    // 设置为自增长的主键
    @DatabaseField(columnName = "id", id = true)
    String id = "";
    @DatabaseField
    String realName = "";
    @DatabaseField
    String loginName = "";
    @DatabaseField
    String pwd = "";
    @DatabaseField
    String code = "";
    @DatabaseField
    String addTime = "";
    @DatabaseField
    String companyID = "";
    @DatabaseField
    String deptID = ""; //获取数据列表中的deptID 实际上是userID，根据这个查看是否是本人
    @DatabaseField
    String roleID = "";
    @DatabaseField
    String sex = "";
    @DatabaseField
    String birthday = "";
    @DatabaseField
    String officePhone = "";
    @DatabaseField
    String mobilePhone = "";
    @DatabaseField
    String email = "";
    @DatabaseField
    String idCard = "";
    @DatabaseField
    String isDelete = "";
    @DatabaseField
    String remarks = "";
    @DatabaseField
    String role1 = "";
    @DatabaseField
    String role2 = "";
    @DatabaseField
    String role3 = "";
    @DatabaseField
    String certificate1 = "";
    @DatabaseField
    String authorities1 = "";
    @DatabaseField
    String certificateNumber1 = "";
    @DatabaseField
    String certificate2 = "";
    @DatabaseField
    String authorities2 = "";
    @DatabaseField
    String certificateNumber2 = "";
    @DatabaseField
    String certificate3 = "";
    @DatabaseField
    String authorities3 = "";
    @DatabaseField
    String certificateNumber3 = "";
    @DatabaseField
    String authorities4 = "";
    @DatabaseField
    String certificateNumber4 = "";
    @DatabaseField
    String regType = "";
    //客户端需要添加
    @DatabaseField
    String isRemembar;//是否记住密码
    @DatabaseField
    String isAutoLogin;//是否自动登陆
    @DatabaseField
    String pwdLength;//记录密码长度

    public boolean isSelect;//关联列表都选


    /**
     * 获取列表时，有的一些字段，不用存
     * code之前就有不知道干啥的
     */
    String relateTime;//获取关联勘察点列表时的时间，不用存
    String relateID;
    String uploadTime;

    public String getRelateID() {
        return relateID;
    }

    public void setRelateID(String relateID) {
        this.relateID = relateID;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }



    public String getRelateTime() {
        return relateTime;
    }

    public void setRelateTime(String relateTime) {
        this.relateTime = relateTime;
    }

    public String getPwdLength() {
        return pwdLength;
    }

    public void setPwdLength(String pwdLength) {
        this.pwdLength = pwdLength;
    }

    public String getIsRemembar() {
        return isRemembar;
    }

    public void setIsRemembar(String isRemembar) {
        this.isRemembar = isRemembar;
    }

    public String getIsAutoLogin() {
        return isAutoLogin;
    }

    public void setIsAutoLogin(String isAutoLogin) {
        this.isAutoLogin = isAutoLogin;
    }

    public LocalUser() {

    }

    public LocalUser(String loginName, String pwd) {
        this.loginName = loginName;
        this.pwd = pwd;
    }

    public LocalUser(String realName) {
        this.id = "2222";
        this.realName = "测试用户";
        this.loginName = "xjjzsjy@qq.com";
        this.pwd = "qweasd";
    }

    public LocalUser(Cursor cursor) {
        this.id = cursor.getString(cursor.getColumnIndex("id"));
        this.realName = cursor.getString(cursor.getColumnIndex("realName"));
        this.loginName = cursor.getString(cursor.getColumnIndex("loginName"));
        this.pwd = cursor.getString(cursor.getColumnIndex("pwd"));

    }

    public String getInsertSQL() {
        return "insert into local_user(ormId,id,realName,loginName,pwd) values(?,?,?,?,?)";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public String getDeptID() {
        return deptID;
    }

    public void setDeptID(String deptID) {
        this.deptID = deptID;
    }

    public String getRoleID() {
        return roleID;
    }

    public void setRoleID(String roleID) {
        this.roleID = roleID;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRole1() {
        return role1;
    }

    public void setRole1(String role1) {
        this.role1 = role1;
    }

    public String getRole2() {
        return role2;
    }

    public void setRole2(String role2) {
        this.role2 = role2;
    }

    public String getRole3() {
        return role3;
    }

    public void setRole3(String role3) {
        this.role3 = role3;
    }

    public String getCertificate1() {
        return certificate1;
    }

    public void setCertificate1(String certificate1) {
        this.certificate1 = certificate1;
    }

    public String getAuthorities1() {
        return authorities1;
    }

    public void setAuthorities1(String authorities1) {
        this.authorities1 = authorities1;
    }

    public String getCertificateNumber1() {
        return certificateNumber1;
    }

    public void setCertificateNumber1(String certificateNumber1) {
        this.certificateNumber1 = certificateNumber1;
    }

    public String getCertificate2() {
        return certificate2;
    }

    public void setCertificate2(String certificate2) {
        this.certificate2 = certificate2;
    }

    public String getAuthorities2() {
        return authorities2;
    }

    public void setAuthorities2(String authorities2) {
        this.authorities2 = authorities2;
    }

    public String getCertificateNumber2() {
        return certificateNumber2;
    }

    public void setCertificateNumber2(String certificateNumber2) {
        this.certificateNumber2 = certificateNumber2;
    }

    public String getCertificate3() {
        return certificate3;
    }

    public void setCertificate3(String certificate3) {
        this.certificate3 = certificate3;
    }

    public String getAuthorities3() {
        return authorities3;
    }

    public void setAuthorities3(String authorities3) {
        this.authorities3 = authorities3;
    }

    public String getCertificateNumber3() {
        return certificateNumber3;
    }

    public void setCertificateNumber3(String certificateNumber3) {
        this.certificateNumber3 = certificateNumber3;
    }

    public String getAuthorities4() {
        return authorities4;
    }

    public void setAuthorities4(String authorities4) {
        this.authorities4 = authorities4;
    }

    public String getCertificateNumber4() {
        return certificateNumber4;
    }

    public void setCertificateNumber4(String certificateNumber4) {
        this.certificateNumber4 = certificateNumber4;
    }

    public String getRegType() {
        return regType;
    }

    public void setRegType(String regType) {
        this.regType = regType;
    }
}
