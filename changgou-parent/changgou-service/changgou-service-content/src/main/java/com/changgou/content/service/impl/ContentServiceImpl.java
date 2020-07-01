package com.changgou.content.service.impl;

import com.changgou.content.dao.ContentMapper;
import com.changgou.content.pojo.Content;
import com.changgou.content.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentMapper contentMapper;

    /**
     * 根据分类id查询
     * @param id
     * @return
     */
    @Override
    public List<Content> findByCategory(Long id) {
        Content content = new Content();
        content.setCategoryId(id);
        content.setStatus("1");
        return contentMapper.select(content);
    }
}
