package com.gushici.mapper.suibi;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gushici.bean.forum.Suibi;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;

@Mapper
public interface SuibiMapper extends BaseMapper<Suibi> {


    /**
     * 保存随笔   返回新增主键
     */
    Integer insertSuibi(Suibi suibi);
}
