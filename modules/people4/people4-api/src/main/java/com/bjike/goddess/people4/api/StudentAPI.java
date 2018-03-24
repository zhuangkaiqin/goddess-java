package com.bjike.goddess.people4.api;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.people4.bo.StudentBO;
import com.bjike.goddess.people4.to.StudentTO;

import java.util.List;

/**
 * 测试学生业务接口
 *user
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-19 10:07 ]
 * @Description: [ 测试学生业务接口 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public interface StudentAPI {

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
     * 增加一个学生
     *
     * @param stuTO
     * @return
     * @throws SerException
     */
    default StudentBO addStu(StudentTO stuTO) throws SerException{
        return  null;
    }

    /**
     * 删除一个学生
     *
     * @param id
     * @throws SerException
     */
    default void deleteStu(String id) throws SerException{

    }

    /**
     * 更新一个学生的信息
     *
     *
     * @param stuTO
     * @return
     * @throws SerException
     */
    default StudentBO updateStu(StudentTO stuTO) throws SerException{
        return null;
    }
}