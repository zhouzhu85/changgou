package com.changgou.dao;

import com.changgou.goods.pojo.Brand;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author zhouzhu
 * @Description
 * @create 2020-03-09 16:59
 */
public interface BrandMapper extends Mapper<Brand> {

    @Select("SELECT tb.* FROM tb_brand tb RIGHT JOIN tb_category_brand tcb ON tb.id=tcb.brand_id WHERE tcb.category_id=#{categoryId}")
    List<Brand> findByCategory(Integer categoryId);
}
