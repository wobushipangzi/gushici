package com.gushici.service.suibi.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gushici.bean.user.Report;
import com.gushici.common.result.GlobalException;
import com.gushici.common.result.GlobalResult;
import com.gushici.common.result.ResultCode;
import com.gushici.mapper.suibi.ReportMapper;
import com.gushici.service.suibi.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;

@Service
public class ReportServiceImpl implements ReportService {

    private static Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private ReportService reportService;

    /**
     * 保存举报的内容
     * @param jsonData
     */
    @Override
    @Transactional
    public GlobalResult saveReport(String jsonData) {
        GlobalResult globalResult = GlobalResult.success();
        try {
            Report report = JSONObject.parseObject(jsonData, Report.class);
            Date date = new Date();
            report.setReportTime(date);
            int insert = reportMapper.insert(report);
            if(insert <= 0){
                logger.error("举报接口保存数据库失败");
                throw new GlobalException(ResultCode.数据库新增数据失败.getCode(), ResultCode.数据库新增数据失败.getMsg());
            }

            //查询刚插入的举报id
            Report reportOne = reportService.getReportOne(report.getFromOpenId(), report.getToOpenId(), date);
            if(null != reportOne){
                HashMap<String, String> dataMap = new HashMap<>();
                dataMap.put("reportId",String.valueOf(reportOne.getReportId()));
                globalResult.setData(dataMap);
            }else {
                logger.error("查询刚插入的举报id失败");
                throw new GlobalException(ResultCode.查询数据库失败.getCode(), ResultCode.查询数据库失败.getMsg());
            }
        }catch (Exception e){
            logger.error("保存举报的内容接口异常", e);
            throw new GlobalException(ResultCode.系统错误.getCode(), ResultCode.系统错误.getMsg());
        }
        return globalResult;
    }


    /**
     * 查询举报内容
     * @param fromOpenId   举报人
     * @param toOpenId  被举报人
     * @param date   举报日期
     * @return
     */
    @Override
    public Report getReportOne(String fromOpenId, String toOpenId, Date date) {
        QueryWrapper<Report> reportQueryWrapper = new QueryWrapper<>();
        reportQueryWrapper.eq("from_open_id",fromOpenId);
        reportQueryWrapper.eq("to_open_id",toOpenId);
        reportQueryWrapper.eq("report_time", date);
        return reportMapper.selectOne(reportQueryWrapper);
    }


    /**
     * 用户举报截图地址入库
     * @param reportId   举报id
     * @param uploadUrl   截图url
     * @return
     */
    @Override
    public Integer updateReport(String reportId, String uploadUrl) {
        QueryWrapper<Report> reportQueryWrapper = new QueryWrapper<>();
        reportQueryWrapper.eq("report_id",reportId);
        Report report = new Report();
        report.setPicture(uploadUrl);
        return reportMapper.update(report,reportQueryWrapper);

    }
}
