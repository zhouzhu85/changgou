package com.changgou.controller;

import com.changgou.goods.pojo.Category;
import com.changgou.service.CategoryService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhouzhu
 * @Description
 * @create 2020-03-17 10:59
 */
@RestController
@RequestMapping("category")
@CrossOrigin
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据节点id查询所有字节点分类集合
     * @param pid
     * @return
     */
    @GetMapping("list/{pid}")
    public Result<List<Category>> findByParentId(@PathVariable Integer pid){
        List<Category> categories = categoryService.findByParentId(pid);
        return new Result<>(true, StatusCode.OK,"查询成功",categories);
    }
}
