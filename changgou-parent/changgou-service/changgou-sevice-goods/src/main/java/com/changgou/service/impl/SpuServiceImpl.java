package com.changgou.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.dao.BrandMapper;
import com.changgou.dao.CategoryMapper;
import com.changgou.dao.SkuMapper;
import com.changgou.dao.SpuMapper;
import com.changgou.goods.pojo.*;
import com.changgou.service.SpuService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/****
 * @Author:shenkunlin
 * @Description:Spu业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SkuMapper skuMapper;

    private final static String IS_DELETE_YES="1";

    private final static String IS_DELETE_NO="0";

    private final static String IS_MARKETABLE_YES="1";

    private final static String IS_MARKETABLE_NO="0";

    private final static String SPU_STATUS_YES="1";

    private final static String SPU_STATUS_NO="0";


    /**
     * Spu条件+分页查询
     * @param spu 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Spu> findPage(Spu spu, int page, int size){
        //分页
        PageHelper.startPage(page,size);
        //搜索条件构建
        Example example = createExample(spu);
        //执行搜索
        return new PageInfo<Spu>(spuMapper.selectByExample(example));
    }

    /**
     * Spu分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Spu> findPage(int page, int size){
        //静态分页
        PageHelper.startPage(page,size);
        //分页查询
        return new PageInfo<Spu>(spuMapper.selectAll());
    }

    /**
     * Spu条件查询
     * @param spu
     * @return
     */
    @Override
    public List<Spu> findList(Spu spu){
        //构建查询条件
        Example example = createExample(spu);
        //根据构建的条件查询数据
        return spuMapper.selectByExample(example);
    }


    /**
     * Spu构建查询对象
     * @param spu
     * @return
     */
    public Example createExample(Spu spu){
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(spu!=null){
            // 主键
            if(!StringUtils.isEmpty(spu.getId())){
                    criteria.andEqualTo("id",spu.getId());
            }
            // 货号
            if(!StringUtils.isEmpty(spu.getSn())){
                    criteria.andEqualTo("sn",spu.getSn());
            }
            // SPU名
            if(!StringUtils.isEmpty(spu.getName())){
                    criteria.andLike("name","%"+spu.getName()+"%");
            }
            // 副标题
            if(!StringUtils.isEmpty(spu.getCaption())){
                    criteria.andEqualTo("caption",spu.getCaption());
            }
            // 品牌ID
            if(!StringUtils.isEmpty(spu.getBrandId())){
                    criteria.andEqualTo("brandId",spu.getBrandId());
            }
            // 一级分类
            if(!StringUtils.isEmpty(spu.getCategory1Id())){
                    criteria.andEqualTo("category1Id",spu.getCategory1Id());
            }
            // 二级分类
            if(!StringUtils.isEmpty(spu.getCategory2Id())){
                    criteria.andEqualTo("category2Id",spu.getCategory2Id());
            }
            // 三级分类
            if(!StringUtils.isEmpty(spu.getCategory3Id())){
                    criteria.andEqualTo("category3Id",spu.getCategory3Id());
            }
            // 模板ID
            if(!StringUtils.isEmpty(spu.getTemplateId())){
                    criteria.andEqualTo("templateId",spu.getTemplateId());
            }
            // 运费模板id
            if(!StringUtils.isEmpty(spu.getFreightId())){
                    criteria.andEqualTo("freightId",spu.getFreightId());
            }
            // 图片
            if(!StringUtils.isEmpty(spu.getImage())){
                    criteria.andEqualTo("image",spu.getImage());
            }
            // 图片列表
            if(!StringUtils.isEmpty(spu.getImages())){
                    criteria.andEqualTo("images",spu.getImages());
            }
            // 售后服务
            if(!StringUtils.isEmpty(spu.getSaleService())){
                    criteria.andEqualTo("saleService",spu.getSaleService());
            }
            // 介绍
            if(!StringUtils.isEmpty(spu.getIntroduction())){
                    criteria.andEqualTo("introduction",spu.getIntroduction());
            }
            // 规格列表
            if(!StringUtils.isEmpty(spu.getSpecItems())){
                    criteria.andEqualTo("specItems",spu.getSpecItems());
            }
            // 参数列表
            if(!StringUtils.isEmpty(spu.getParaItems())){
                    criteria.andEqualTo("paraItems",spu.getParaItems());
            }
            // 销量
            if(!StringUtils.isEmpty(spu.getSaleNum())){
                    criteria.andEqualTo("saleNum",spu.getSaleNum());
            }
            // 评论数
            if(!StringUtils.isEmpty(spu.getCommentNum())){
                    criteria.andEqualTo("commentNum",spu.getCommentNum());
            }
            // 是否上架
            if(!StringUtils.isEmpty(spu.getIsMarketable())){
                    criteria.andEqualTo("isMarketable",spu.getIsMarketable());
            }
            // 是否启用规格
            if(!StringUtils.isEmpty(spu.getIsEnableSpec())){
                    criteria.andEqualTo("isEnableSpec",spu.getIsEnableSpec());
            }
            // 是否删除
            if(!StringUtils.isEmpty(spu.getIsDelete())){
                    criteria.andEqualTo("isDelete",spu.getIsDelete());
            }
            // 审核状态
            if(!StringUtils.isEmpty(spu.getStatus())){
                    criteria.andEqualTo("status",spu.getStatus());
            }
        }
        return example;
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(Long id){
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //检查是否被逻辑删除，必须先逻辑删除后才能物理删除
        if (IS_DELETE_NO.equals(spu.getIsDelete())){
            throw new RuntimeException("此商品不能删除");
        }
        spuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改Spu
     * @param spu
     */
    @Override
    public void update(Spu spu){
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 增加Spu
     * @param spu
     */
    @Override
    public void add(Spu spu){
        spuMapper.insert(spu);
    }

    /**
     * 根据ID查询Spu
     * @param id
     * @return
     */
    @Override
    public Spu findById(Long id){
        return  spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Spu全部数据
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    @Override
    public void saveGoods(Goods goods) {
        /** 新增或修改spu **/
        Spu spu = goods.getSpu();
        if (spu.getId()==null){
            //新增
            spu.setId(idWorker.nextId());
            spuMapper.insertSelective(spu);
        }else {
            //修改
            spuMapper.updateByPrimaryKeySelective(spu);
            //删除该spu的sku
            Sku sku=new Sku();
            sku.setSpuId(spu.getId());
            skuMapper.delete(sku);
        }

        /** 新增sku **/
        Date date=new Date();
        Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());
        Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());

        List<Sku> skuList = goods.getSkuList();
        //循环将数据加入到数据库
        for (Sku sku:skuList){
            //构建sku名称，采用spu+规格值组装
            if (StringUtils.isEmpty(sku.getSpec())){
                sku.setSpec("{}");
            }
            //获取spu的名字
            String name = spu.getName();
            //将规格转换成map
            Map<String,String> specMap = JSON.parseObject(sku.getSpec(), Map.class);
            //循环组装sku的名字
            for (Map.Entry<String,String> entry: specMap.entrySet()){
                name+=" "+entry.getValue();
            }
            sku.setName(name);
            sku.setId(idWorker.nextId());
            sku.setSpuId(spu.getId());
            sku.setCreateTime(date);
            sku.setUpdateTime(date);
            sku.setCategoryId(spu.getCategory3Id());
            sku.setCategoryName(category.getName());
            sku.setBrandName(brand.getName());

            skuMapper.insertSelective(sku);
        }
    }

    @Override
    public Goods findGoodsById(Long spuId) {
        //查询spu
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        //查询List<Sku>
        Sku sku=new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(sku);
        //封装goods
        Goods goods=new Goods();
        goods.setSkuList(skus);
        return goods;
    }

    @Override
    public void audit(Long spuId) {
        //查询商品
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        //判断商品是否删除
        if (IS_DELETE_YES.equalsIgnoreCase(spu.getIsDelete())){
            throw new RuntimeException("该商品已经删除了");
        }
        //审核通过
        spu.setStatus(SPU_STATUS_YES);
        //上架
        spu.setIsMarketable(IS_MARKETABLE_YES);
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    public void pull(Long spuId) {
        //查询商品
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        //判断商品是否删除
        if (IS_DELETE_YES.equalsIgnoreCase(spu.getIsDelete())){
            throw new RuntimeException("该商品已经删除了");
        }
        //下架
        spu.setIsMarketable(IS_MARKETABLE_NO);
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    public void put(Long spuId) {
        //查询商品
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        //判断商品是否删除
        if (IS_DELETE_YES.equalsIgnoreCase(spu.getIsDelete())){
            throw new RuntimeException("该商品已经删除了");
        }
        if (SPU_STATUS_NO.equals(spu.getStatus())){
            throw new RuntimeException("未通过审核的商品不能上架");
        }
        //上架
        spu.setIsMarketable(IS_MARKETABLE_YES);
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    public int putMany(Long[] ids) {
        Spu spu=new Spu();
        spu.setIsMarketable(IS_MARKETABLE_YES);
        //批量修改
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));
        //下架的
        criteria.andEqualTo("isMarketable",IS_MARKETABLE_NO);
        //审核通过
        criteria.andEqualTo("status",SPU_STATUS_YES);
        //非删除
        criteria.andEqualTo("isDelete",IS_DELETE_NO);
        return spuMapper.updateByExampleSelective(spu,example);
    }
    @Override
    public int pullMany(Long[] ids) {
        Spu spu=new Spu();
        spu.setIsMarketable(IS_MARKETABLE_NO);
        //批量修改
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));
        //上架的
        criteria.andEqualTo("isMarketable",IS_MARKETABLE_YES);
        //审核通过
        criteria.andEqualTo("status",SPU_STATUS_YES);
        //非删除
        criteria.andEqualTo("isDelete",IS_DELETE_NO);
        return spuMapper.updateByExampleSelective(spu,example);
    }

    @Override
    public void logicDelete(Long spuId) {
        Spu spu=spuMapper.selectByPrimaryKey(spuId);
        //检查商品是否下架
        if (IS_MARKETABLE_YES.equals(spu.getIsMarketable())){
            throw new RuntimeException("必须先下架再删除");
        }
        //删除
        spu.setIsDelete(IS_DELETE_YES);
        //设置为未审核
        spu.setStatus(SPU_STATUS_NO);

        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    public void restore(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        //检查是否删除的商品
        if (IS_DELETE_NO.equals(spu.getIsDelete())){
            throw new RuntimeException("此商品未删除");
        }
        //未删除
        spu.setIsDelete(IS_DELETE_NO);
        //设置未审核
        spu.setIsMarketable(IS_MARKETABLE_YES);

        spuMapper.updateByPrimaryKeySelective(spu);
    }
}
