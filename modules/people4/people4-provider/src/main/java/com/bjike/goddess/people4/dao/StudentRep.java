package com.bjike.goddess.people4.dao;

import com.bjike.goddess.common.jpa.dao.JpaRep;
import com.bjike.goddess.people4.dto.StudentDTO;
import com.bjike.goddess.people4.entity.Student;

/**
* 测试学生持久化接口, 继承基类可使用ｊｐａ命名查询
* @Author:			[ Jianyangfeng ]
* @Date:			[  2018-03-19 10:07 ]
* @Description:	[ 测试学生持久化接口, 继承基类可使用ｊｐａ命名查询 ]
* @Version:		[ v1.0.0 ]
* @Copy:   		[ com.bjike ]
*/
public interface StudentRep extends JpaRep<Student ,StudentDTO> { 

 }