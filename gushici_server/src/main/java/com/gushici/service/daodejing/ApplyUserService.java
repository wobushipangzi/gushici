package com.gushici.service.daodejing;

import com.gushici.bean.daodejing.ApplyUser;
import com.gushici.common.result.GlobalResult;
import org.springframework.stereotype.Service;

@Service
public interface ApplyUserService {

    /**
     * 保存报名人员
     */
    GlobalResult insertApplyUser(ApplyUser applyUser);

}
