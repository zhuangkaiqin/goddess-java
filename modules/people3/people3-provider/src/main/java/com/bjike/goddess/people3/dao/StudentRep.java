package com.bjike.goddess.people3.dao;

import com.bjike.goddess.common.jpa.dao.JpaRep;
import com.bjike.goddess.people3.dto.StudentDTO;
import com.bjike.goddess.people3.entity.Student;

import java.util.List;

/**
 * 测试学生类持久化接口, 继承基类可使用ｊｐａ命名查询
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-09 11:13 ]
 * @Description: [ 测试学生类持久化接口, 继承基类可使用ｊｐａ命名查询 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public interface StudentRep extends JpaRep<Student, StudentDTO> {

//    @Query(value = "select  from people3_student where classNum = 1",nativeQuery = true)
    List<Student> findByClassNum(String classNum);

}