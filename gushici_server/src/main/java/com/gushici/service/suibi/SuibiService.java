package com.gushici.service.suibi;

import com.gushici.bean.forum.Suibi;
import com.gushici.common.result.GlobalResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

public interface SuibiService {


    /**
     * 随笔文字配图内容入库
     * @param openId   用户openId
     * @param content   随笔的文字内容
     * @return
     */
    GlobalResult saveSuibiContent(String openId, String content, List<MultipartFile> imgList);


    /**
     * 查询十条随笔数据
     * @param lastSuibiId   前端返回的最后一条数据的id
     */
    GlobalResult getFifSuibis(String lastSuibiId);


    /**
     * 更新随笔表的点赞数
     * @param suibiId    随笔id
     * @return
     */
    Integer updatePraise(String suibiId);

    /**
     * 查询随笔
     * @param openId  用户openID
     * @param date   日期
     * @return
     */
    Suibi getSuibiOne(String openId, Date date);


    /**
     * 通过随笔id查询随笔
     * @param suibiId  随笔id
     * @return
     */
    Suibi getSuibiBySuibiId(String suibiId);


    /**
     * 通过随笔id删除随笔记录
     * @param suibiId  随笔id
     * @return
     */
    GlobalResult delSuibiBySuibiId(String suibiId, List files);

    /**
     * 相关随笔的评论数加1
     * @param topicId  随笔id
     */
    Integer addCommentCount(String topicId);
}
