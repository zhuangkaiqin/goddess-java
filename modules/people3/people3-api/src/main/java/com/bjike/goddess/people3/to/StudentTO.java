package com.bjike.goddess.people3.to;

import com.bjike.goddess.common.api.to.BaseTO;
import com.bjike.goddess.people3.entity.Class;

/**
* 测试学生类
* @Author:			[ Jianyangfeng ]
* @Date:			[  2018-03-09 11:13 ]
* @Description:	[ 测试学生类 ]
* @Version:		[ v1.0.0 ]
* @Copy:   		[ com.bjike ]
*/
public class StudentTO extends BaseTO { 

/**
* 学生学号
*/
 private String  StuNum; 

/**
* 学生姓名
*/
 private String  StuName; 

/**
* 学生年龄
*/
 private String  StuAge;

 private Class aClass;

 private String ClassNum;

 public Class getaClass() {
  return aClass;
 }

 public void setaClass(Class aClass) {
  this.aClass = aClass;
 }

 public String getClassNum() {
  return ClassNum;
 }

 public void setClassNum(String classNum) {
  ClassNum = classNum;
 }

 public String getStuNum () { 
 return StuNum;
 } 
 public void setStuNum (String StuNum ) { 
 this.StuNum = StuNum ; 
 } 
 public String getStuName () { 
 return StuName;
 } 
 public void setStuName (String StuName ) { 
 this.StuName = StuName ; 
 } 
 public String getStuAge () { 
 return StuAge;
 } 
 public void setStuAge (String StuAge ) { 
 this.StuAge = StuAge ; 
 } 
 }