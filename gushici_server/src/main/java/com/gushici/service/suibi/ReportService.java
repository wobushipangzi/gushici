package com.gushici.service.suibi;

import com.gushici.bean.user.Report;
import com.gushici.common.result.GlobalResult;

import java.util.Date;

public interface ReportService {


    /**
     * 保存举报的内容
     * @param jsonData
     */
    GlobalResult saveReport(String jsonData);

    /**
     * 查询举报内容
     * @param fromOpenId   举报人
     * @param toOpenId  被举报人
     * @param date   举报日期
     * @return
     */
    Report getReportOne(String fromOpenId, String toOpenId, Date date);


    /**
     * 用户举报截图地址入库
     * @param reportId   举报id
     * @param uploadUrl   截图url
     * @return
     */
    Integer updateReport(String reportId, String uploadUrl);
}
