package com.changgou.controller;

import com.changgou.goods.pojo.Para;
import com.changgou.service.ParaService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhouzhu
 * @Description
 * @create 2020-03-17 16:49
 */
@RestController
@CrossOrigin
@RequestMapping("para")
public class ParaController {

    @Autowired
    private ParaService paraService;

    /**
     * 根据分类id查询参数列表
     * @param categoryId
     * @return
     */
    @GetMapping("category/{id}")
    public Result<List<Para>> findByCategoryId(@PathVariable(value = "id")Integer categoryId){
        List<Para> paras = paraService.findByCategoryId(categoryId);
        return new Result<>(true, StatusCode.OK,"查询成功",paras);
    }

}
