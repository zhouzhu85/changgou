package com.changgou.controller;

import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import com.changgou.service.SpuService;
import com.github.pagehelper.PageInfo;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhouzhu
 * @Description
 * @create 2020-03-19 11:37
 */
@RestController
@RequestMapping("spu")
@CrossOrigin
public class SpuController {

    @Autowired
    private SpuService spuService;

    /**
     * 添加商品
     * @param goods
     * @return
     */
    @PostMapping("save")
    public Result save(@RequestBody Goods goods){
        spuService.saveGoods(goods);
        return new Result(true, StatusCode.OK,"保存成功");
    }

    /**
     * 根据spu的id查询商品信息
     * @param id
     * @return
     */
    @GetMapping("goods/{id}")
    public Result<Goods> findGoodsById(@PathVariable Long id){
        Goods goods = spuService.findGoodsById(id);
        return new Result<>(true,StatusCode.OK,"查询成功",goods);
    }

    /**
     * 商品审核
     * @param id
     * @return
     */
    @PutMapping("audit/{id}")
    public Result audit(@PathVariable Long id){
        spuService.audit(id);
        return new Result(true,StatusCode.OK,"审核成功");
    }

    /**
     * 商品下架
     * @param id
     * @return
     */
    @PutMapping("pull/{id}")
    public Result pull(@PathVariable Long id){
        spuService.pull(id);
        return new Result(true,StatusCode.OK,"下架成功");
    }

    /**
     * 商品上架
     * @param id
     * @return
     */
    @PutMapping("put/{id}")
    public Result put(@PathVariable Long id){
        spuService.put(id);
        return new Result(true,StatusCode.OK,"上架成功");
    }

    /**
     * 批量上架
     * @param ids
     * @return
     */
    @PutMapping("put/many")
    public Result putMany(@RequestBody Long[] ids){
        int count = spuService.putMany(ids);
        return new Result(true,StatusCode.OK,"上架"+count+"个商品");
    }
    /**
     * 批量下架
     * @param ids
     * @return
     */
    @PutMapping("pull/many")
    public Result pullMany(@RequestBody Long[] ids){
        int count = spuService.pullMany(ids);
        return new Result(true,StatusCode.OK,"下架"+count+"个商品");
    }

    /**
     * 逻辑删除
     * @param id
     * @return
     */
    @DeleteMapping("logic/delete/{id}")
    public Result logicDelete(@PathVariable Long id){
        spuService.logicDelete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /**
     * 恢复数据
     * @param id
     * @return
     */
    @PutMapping("restore/{id}")
    public Result restore(@PathVariable Long id){
        spuService.restore(id);
        return new Result(true,StatusCode.OK,"数据恢复成功");
    }

    /**
     * spu分页条件搜索
     * @param spu
     * @param page
     * @param size
     * @return
     */
    @PostMapping("search/{page}/{size}")
    public Result<PageInfo> findPage(@RequestBody(required = false)Spu spu,@PathVariable int page,@PathVariable int size){
        PageInfo<Spu> pageInfo = spuService.findPage(spu, page, size);
        return new Result<>(true,StatusCode.OK,"查询成功",pageInfo);
    }
}
