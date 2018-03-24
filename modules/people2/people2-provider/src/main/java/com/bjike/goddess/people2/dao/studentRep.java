package com.bjike.goddess.people2.dao;

import com.bjike.goddess.common.jpa.dao.JpaRep;
import com.bjike.goddess.people2.dto.studentDTO;
import com.bjike.goddess.people2.entity.student;

/**
* 测试学生类持久化接口, 继承基类可使用ｊｐａ命名查询
* @Author:			[ Jianyangfeng ]
* @Date:			[  2018-03-05 05:18 ]
* @Description:	[ 测试学生类持久化接口, 继承基类可使用ｊｐａ命名查询 ]
* @Version:		[ v1.0.0 ]
* @Copy:   		[ com.bjike ]
*/
public interface studentRep extends JpaRep<student ,studentDTO> { 

 }