package com.gushici.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gushici.bean.user.User;
import com.gushici.mapper.user.AttentionMapper;
import com.gushici.mapper.user.UserMapper;
import com.gushici.service.user.AttentionService;
import com.gushici.bean.user.Attention;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class AttentionServiceImpl implements AttentionService {

    private final AttentionMapper attentionMapper;

    private final UserMapper userMapper;

    @Autowired
    public AttentionServiceImpl(AttentionMapper attentionMapper, UserMapper userMapper) {
        this.attentionMapper = attentionMapper;
        this.userMapper = userMapper;
    }

    /**
     * 关注用户接口
     * @param attentionOpenId
     * @param fansOpenId
     */
    @Override
    public Integer addAttention(String attentionOpenId, String fansOpenId) {
        Attention attention = new Attention();
        attention.setAttentionOpenId(attentionOpenId);
        attention.setFansOpenId(fansOpenId);
        attention.setAttentionTime(new Date());
        return attentionMapper.insert(attention);
    }


    /**
     * 获取粉丝数
     * @param openId
     */
    @Override
    public Integer getFansCount(String openId) {
        QueryWrapper<Attention> attentionQueryWrapper = new QueryWrapper<>();
        attentionQueryWrapper.eq("attention_open_id",openId);
        return attentionMapper.selectCount(attentionQueryWrapper);
    }


    /**
     * 获取总关注数
     * @param openId
     * @return
     */
    @Override
    public Integer getAttentionCount(String openId) {
        QueryWrapper<Attention> attentionQueryWrapper = new QueryWrapper<>();
        attentionQueryWrapper.eq("fans_open_id",openId);
        return attentionMapper.selectCount(attentionQueryWrapper);
    }


    /**
     * 我的关注列表
     * @param openId
     * @return
     */
    @Override
    public List<Map<String,String>> getAttentions(String openId) {
        QueryWrapper<Attention> attentionQueryWrapper = new QueryWrapper<>();
        attentionQueryWrapper.eq("fans_open_id",openId);
        List<Attention> attentions = attentionMapper.selectList(attentionQueryWrapper);

        ArrayList<Map<String,String>> attentionList = new ArrayList<>();
        //获取用户的昵称  头像  attention_open_id   个性签名
        for (Attention attention : attentions) {
            HashMap<String, String> attentionMap = new HashMap<>();
            String attentionOpenId = attention.getAttentionOpenId();
            attentionMap.put("attentionOpenId",attentionOpenId);

            //获取用户昵称  头像 个性签名
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("open_id",attentionOpenId);
            User user = userMapper.selectOne(userQueryWrapper);
            attentionMap.put("name",user.getName());
            attentionMap.put("headPhotoUrl",user.getHeadPhotoUrl());
            attentionMap.put("signature",user.getSignature());
            attentionList.add(attentionMap);
        }
        return attentionList;
    }


    /**
     * 我的粉丝列表
     * @param openId
     * @return
     */
    @Override
    public List<Map<String, String>> getFans(String openId) {
        QueryWrapper<Attention> attentionQueryWrapper = new QueryWrapper<>();
        attentionQueryWrapper.eq("attention_open_id",openId);
        List<Attention> fanss = attentionMapper.selectList(attentionQueryWrapper);

        ArrayList<Map<String,String>> fansList = new ArrayList<>();
        //获取用户的昵称  头像  attention_open_id   个性签名
        for (Attention fans : fanss) {
            HashMap<String, String> fansMap = new HashMap<>();
            String fansOpenId = fans.getFansOpenId();
            fansMap.put("fansOpenId",fansOpenId);

            //获取用户昵称  头像 个性签名
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("open_id",fansOpenId);
            User user = userMapper.selectOne(userQueryWrapper);
            fansMap.put("nickName",user.getNickName());
            fansMap.put("headPhotoUrl",user.getHeadPhotoUrl());
            fansMap.put("signature",user.getSignature());
            fansList.add(fansMap);
        }
        return fansList;
    }


}
