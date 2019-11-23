package com.gushici.bean.user;

import java.io.Serializable;

public class CommentAndUser extends Comment implements Serializable {

    private static final long serialVersionUID = 4222952895382282763L;

    private String headPhotoUrl;   //用户头像

    private String name;  //用户名字

    public String getHeadPhotoUrl() {
        return headPhotoUrl;
    }

    public void setHeadPhotoUrl(String headPhotoUrl) {
        this.headPhotoUrl = headPhotoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "CommentAndUser{" +
                "headPhotoUrl='" + headPhotoUrl + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
