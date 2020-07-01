package com.changgou.search.service;

import java.util.Map;

public interface SkuService {

    /**
     * 导入sku数据到elasticsearch
     */
    void importSku();

    /**
     * 搜索
     * @param searchMap
     * @return
     */
    Map<String,Object> search(Map<String,String> searchMap);
}
