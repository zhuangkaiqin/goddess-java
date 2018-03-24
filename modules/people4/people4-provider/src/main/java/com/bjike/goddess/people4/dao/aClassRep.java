package com.bjike.goddess.people4.dao;

import com.bjike.goddess.common.jpa.dao.JpaRep;
import com.bjike.goddess.people4.dto.aClassDTO;
import com.bjike.goddess.people4.entity.aClass;

import java.util.List;

/**
 * 测试班级持久化接口, 继承基类可使用ｊｐａ命名查询
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-19 10:22 ]
 * @Description: [ 测试班级持久化接口, 继承基类可使用ｊｐａ命名查询 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public interface aClassRep extends JpaRep<aClass, aClassDTO> {

    @Override
    List<aClass> findAll();

    
}