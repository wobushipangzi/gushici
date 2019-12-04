package com.gushici.mapper.suibi;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gushici.bean.forum.Suibi;
import com.gushici.bean.user.Report;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReportMapper extends BaseMapper<Report>{

    /**
     * 保存举报信息   返回新增主键
     */
    Integer insertReport(Report report);
}
