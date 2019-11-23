package com.gushici.bean.user;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Component
@TableName("tb_user")
public class User implements Serializable{

    private static final long serialVersionUID = -2919534301198231931L;

    private Integer userId;   //用户id

    private String name;   //用户名称

    private Date birthday;  //用户生日

    private String openId;  //此小程序的用户id

    private String unionId;  //此微信公众平台的用户id

    private String nickName;  //用户昵称

    private String avatarUrl;  //用户微信头像地址

    private Integer gender;  //性别 1:男 0:女 2:未知

    private String country;  //国家

    private String province;  //省

    private String city;  //城市

    private String signature;  //个性签名

    private Integer level;  //用户等级

    private String headPhotoUrl;  //用户自定义头像地址

    private String mobile;  //用户手机号

    private Integer blacklist;  //是否黑名单 1:黑 0:白

    private Integer isDelete;  //是否注销 1:是 0:否

    private Date registerTime;  //注册时间

    private Date updateTime;  //修改时间

    private Date blackTime;  //加入黑名单时间

    private Date loginTime;  //最近一次登录时间

    private Date deleteTime;  //注销时间


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getHeadPhotoUrl() {
        return headPhotoUrl;
    }

    public void setHeadPhotoUrl(String headPhotoUrl) {
        this.headPhotoUrl = headPhotoUrl;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(Integer blacklist) {
        this.blacklist = blacklist;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getBlackTime() {
        return blackTime;
    }

    public void setBlackTime(Date blackTime) {
        this.blackTime = blackTime;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                ", openId='" + openId + '\'' +
                ", unionId='" + unionId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", gender=" + gender +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", signature='" + signature + '\'' +
                ", level=" + level +
                ", headPhotoUrl='" + headPhotoUrl + '\'' +
                ", mobile='" + mobile + '\'' +
                ", blacklist=" + blacklist +
                ", isDelete=" + isDelete +
                ", registerTime=" + registerTime +
                ", updateTime=" + updateTime +
                ", blackTime=" + blackTime +
                ", loginTime=" + loginTime +
                ", deleteTime=" + deleteTime +
                '}';
    }
}
