package com.changgou.controller;

import com.changgou.goods.pojo.Sku;
import com.changgou.service.SkuService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("sku")
@CrossOrigin
public class SkuController {

    @Autowired
    private SkuService skuService;

    /**
     * 根据审核状态查询sku列表
     * @param status
     * @return
     */
    @GetMapping("/status/{status}")
    public Result<List<Sku>> findByStatus(@PathVariable("status")String status){
        List<Sku> list= skuService.findByStatus(status);
        return new Result<>(true, StatusCode.OK,"查询成功",list);
    }
}
