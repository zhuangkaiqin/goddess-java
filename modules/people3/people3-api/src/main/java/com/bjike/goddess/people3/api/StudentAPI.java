package com.bjike.goddess.people3.api;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.people3.bo.StudentBO;
import com.bjike.goddess.people3.dto.StudentDTO;
import com.bjike.goddess.people3.entity.Student;
import com.bjike.goddess.people3.to.StudentTO;

import java.util.List;

/**
 * 测试学生类业务接口
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-09 11:13 ]
 * @Description: [ 测试学生类业务接口 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public interface StudentAPI {

    /**
     * 增加一个学生
     *
     * @param stuTO
     * @return
     * @throws SerException
     */
    default StudentBO addStu(StudentTO stuTO) throws SerException {
        return null;
    }

    /**
     * 根据id删除一个学生
     *
     * @param id
     * @throws SerException
     */
    default void deleteStu(String id) throws SerException {

    }

    /**
     * 修改一个学生信息
     *
     * @param stuTO
     * @return
     * @throws SerException
     */
    default StudentBO updateStu(StudentTO stuTO) throws SerException {
        return null;
    }

    /**
     * 查找全部学生
     *
     * @return
     * @throws SerException
     */
    default List<StudentBO> findAllStu() throws SerException {
        return null;
    }

    /**
     * 根据教室编号找那个班级的学生
     *
     * @return
     * @throws SerException
     */
    default List<Student> findAllStuByClassNum(StudentDTO stuDTO) throws SerException {
        return null;
    }

    /**
     * 根据专业查找学生
     *
     * @return
     * @throws SerException
     */
    default List<StudentBO> findAllStuByClassType(String ClaType) throws SerException {
        return null;
    }

    /**
     * 根据学生id查找学生
     *
     * @param id
     * @return
     * @throws SerException
     */
    default StudentBO findStuByStuId(String id) throws SerException {
        return null;
    }


}