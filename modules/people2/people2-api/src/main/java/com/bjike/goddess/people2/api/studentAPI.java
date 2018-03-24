package com.bjike.goddess.people2.api;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.people2.bo.studentBO;
import com.bjike.goddess.people2.to.studentTO;

import java.util.List;

/**
* 测试学生类业务接口
* @Author:			[ Jianyangfeng ]
* @Date:			[  2018-03-05 05:18 ]
* @Description:	[ 测试学生类业务接口 ]
* @Version:		[ v1.0.0 ]
* @Copy:   		[ com.bjike ]
*/
public interface studentAPI  {

 /**
  * 获取学生表中的信息
  *
  * @return
  * @throws SerException
  */
 default List<studentBO> findStuAll() throws SerException{
   return null;
 }

 default studentBO findStuByNum(String id) throws SerException{
  return null;
 }

 /**
  * 添加一个学生
  *
  * @return
  * @throws SerException
  */
 default studentBO addStu(studentTO stuTO) throws SerException{
  return null;
 }

 /**
  * 更新一个学生
  *
  * @return
  * @throws SerException
  */
 default studentBO updateStu(studentTO stuTO) throws SerException{
  return null;
 }

 /**
  * 删除一个学生
  *
  * @throws SerException
  */
 default void deleteStu(String id) throws SerException{

 }

 }