package com.bjike.goddess.people3.dao;

import com.bjike.goddess.common.jpa.dao.JpaRep;
import com.bjike.goddess.people3.dto.ClassDTO;
import com.bjike.goddess.people3.entity.Class;

/**
* 测试班级类持久化接口, 继承基类可使用ｊｐａ命名查询
* @Author:			[ Jianyangfeng ]
* @Date:			[  2018-03-09 11:20 ]
* @Description:	[ 测试班级类持久化接口, 继承基类可使用ｊｐａ命名查询 ]
* @Version:		[ v1.0.0 ]
* @Copy:   		[ com.bjike ]
*/
public interface ClassRep extends JpaRep<Class ,ClassDTO> { 

 }