package com.gushici.bean.user;

import java.io.Serializable;

public class ReplyAndUser extends Reply implements Serializable {

    private static final long serialVersionUID = -1458134044436265089L;

    private String fromUserName;   //评论人的名字

    private String fromHeadPhoto;  //评论人的头像

    private String toUserName;   //被评论人的名字

    private String toHeadPhoto;  //被评论人的头像

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getFromHeadPhoto() {
        return fromHeadPhoto;
    }

    public void setFromHeadPhoto(String fromHeadPhoto) {
        this.fromHeadPhoto = fromHeadPhoto;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getToHeadPhoto() {
        return toHeadPhoto;
    }

    public void setToHeadPhoto(String toHeadPhoto) {
        this.toHeadPhoto = toHeadPhoto;
    }

    @Override
    public String toString() {
        return "ReplyAndUser{" +
                "fromUserName='" + fromUserName + '\'' +
                ", fromHeadPhoto='" + fromHeadPhoto + '\'' +
                ", toUserName='" + toUserName + '\'' +
                ", toHeadPhoto='" + toHeadPhoto + '\'' +
                '}';
    }
}
