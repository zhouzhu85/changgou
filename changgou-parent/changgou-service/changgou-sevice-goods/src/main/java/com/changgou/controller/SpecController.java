package com.changgou.controller;

import com.changgou.dao.SpecMapper;
import com.changgou.goods.pojo.Spec;
import com.changgou.service.SpecService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhouzhu
 * @Description
 * @create 2020-03-17 16:39
 */
@RestController
@CrossOrigin
@RequestMapping("spec")
public class SpecController {

    @Autowired
    private SpecService specService;

    /**
     * 根据分类id查询对应的规格列表
     * @param categoryId
     * @return
     */
    @GetMapping("category/{id}")
    public Result<List<Spec>> findByCategoryId(@PathVariable(value = "id") Integer categoryId){
        List<Spec> specs = specService.findByCategoryId(categoryId);
        return new Result<>(true, StatusCode.OK,"查询成功",specs);
    }

}
