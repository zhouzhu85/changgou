package com.changgou.controller;

import com.changgou.goods.pojo.Brand;
import com.changgou.service.BrandService;
import com.github.pagehelper.PageInfo;
import entity.Result;
import entity.StatusCode;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhouzhu
 * @Description
 * @create 2020-03-09 17:00
 */
@RestController
@RequestMapping(value = "brand")
@CrossOrigin  //跨域
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     * 查询所有品牌
     * @return
     */
    @GetMapping
    public Result<List<Brand>> findAll(){
        List<Brand> brands = brandService.findAll();
        return new Result<List<Brand>>(true, StatusCode.OK,"查询品牌集合成功！",brands);
    }

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<Brand> findById(@PathVariable("id") Integer id){
        Brand brand = brandService.findById(id);
        return new Result<>(true,StatusCode.OK,"查询成功",brand);
    }

    /**
     * 新增品牌
     * @param brand
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Brand brand){
        brandService.add(brand);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    /**
     * 修改品牌
     * @param id
     * @param brand
     * @return
     */
    @PutMapping("{id}")
    public Result update(@PathVariable("id") Integer id,@RequestBody Brand brand){
        brand.setId(id);
        brandService.update(brand);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /**
     * 根据id删除品牌
     * @param id
     * @return
     */
    @DeleteMapping("{id}")
    public Result delete(@PathVariable("id") Integer id){
        brandService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /**
     * 多条件搜索品牌数据
     * @param brand
     * @return
     */
    @PostMapping("search")
    public Result<List<Brand>> findList(@RequestBody(required = false) Brand brand){
        List<Brand> brands=brandService.findList(brand);
        return new Result<>(true,StatusCode.OK,"查询成功",brands);
    }

    /**
     * 分页搜索
     * @param page
     * @param size
     * @return
     */
    @GetMapping("search/{page}/{size}")
    public Result<PageInfo> findPage(@PathVariable int page,@PathVariable int size){
        PageInfo<Brand> brandPageInfo = brandService.findPage(page, size);
        return new Result<>(true,StatusCode.OK,"查询成功",brandPageInfo);
    }
    /**
     * 条件分页搜索
     * @param brand
     * @param page
     * @param size
     * @return
     */
    @PostMapping("search/{page}/{size}")
    public Result<PageInfo> findPage(@RequestBody(required = false) Brand brand,@PathVariable int page,@PathVariable int size){
        PageInfo<Brand> brandPageInfo = brandService.findPage(brand,page, size);
        return new Result<>(true,StatusCode.OK,"查询成功",brandPageInfo);
    }

    /**
     * 根据分类实现品牌列表查询
     * @param categoryId
     * @return
     */
    @GetMapping("category/{id}")
    public Result<List<Brand>> findBrandByCategory(@PathVariable(value = "id")Integer categoryId){
        List<Brand> brandList = brandService.findByCategory(categoryId);
        return new Result<>(true,StatusCode.OK,"查询成功",brandList);
    }
}
