package com.gushici.service.product;

import com.gushici.common.result.GlobalResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public interface AuthorService {

    /**
     * 查询所有的朝代以及对应的作者
     * @return
     */
    GlobalResult getReignAndAuthor();

    /**
     * 通过作者id查询作者详情以及对应作品
     * @param authorId
     * @return
     */
    HashMap<Object, Object> getAuthAndProsByAuthId(String authorId, String page);


    /**
     * 搜索作者
     */
    GlobalResult searchAuthor(String authorName, String page);


    /**
     * 根据朝代id获取对应的所有作者集合
     * @param reignId  朝代id
     * @return
     */
    GlobalResult getAuthorList(String reignId);
}
