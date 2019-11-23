package com.gushici.service.daodejing.impl;

import com.gushici.bean.daodejing.ApplyUser;
import com.gushici.common.result.GlobalResult;
import com.gushici.common.result.ResultCode;
import com.gushici.mapper.daodejing.ApplyUserMapper;
import com.gushici.service.daodejing.ApplyUserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ApplyUserServiceImpl implements ApplyUserService {

    private static Logger logger = Logger.getLogger(ApplyUserServiceImpl.class);

    @Autowired
    private ApplyUserMapper applyUserMapper;


    /**
     * 保存报名人员
     * @param applyUser   报名人员信息对象
     * @return
     */
    @Override
    public GlobalResult insertApplyUser(ApplyUser applyUser) {
        GlobalResult globalResult = GlobalResult.success();
        try {
            int count = applyUserMapper.insert(applyUser);
            if(0 < count){
                return globalResult;
            }else {
                globalResult.setCode(ResultCode.数据库新增数据失败.getCode());
                globalResult.setCode(ResultCode.数据库新增数据失败.getMsg());
            }
        }catch (Exception e){
            logger.error("保存参赛人员失败{}", e);
            return GlobalResult.error();
        }
        return globalResult;
    }
}
