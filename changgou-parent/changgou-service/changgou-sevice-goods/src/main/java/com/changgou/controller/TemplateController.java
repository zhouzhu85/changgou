package com.changgou.controller;

import com.changgou.goods.pojo.Template;
import com.changgou.service.TemplateService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhouzhu
 * @Description
 * @create 2020-03-17 11:12
 */
@RestController
@RequestMapping("template")
@CrossOrigin
public class TemplateController {
    @Autowired
    TemplateService templateService;

    /**
     * 根据分类查询模板数据
     * @param id
     * @return
     */
    @GetMapping("category/{id}")
    public Result<Template> findByCategoryId(@PathVariable Integer id){
        Template template=templateService.findByCategoryId(id);
        return new Result<>(true, StatusCode.OK,"查询成功",template);
    }
}
