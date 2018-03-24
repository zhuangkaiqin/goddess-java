package com.bjike.goddess.people.api;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.people.bo.studentBO;
import com.bjike.goddess.people.to.studentTO;

import java.util.List;

/**
 * 测试学生类业务接口
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-02 11:32 ]
 * @Description: [ 测试学生类业务接口 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public interface studentAPI {

    /**
     * 获取全部信息list
     *
     * @return
     * @throws SerException
     */
    default List<studentBO> findall() throws SerException {
        return null;
    }

    /**
     * 通过id找那条数据
     *
     * @return
     * @throws SerException
     */
    default studentBO findBy_Id(String id) throws SerException {
        return null;
    }

    /**
     * 增加数据
     *
     * @param stuBO
     * @return
     * @throws SerException
     */
    default studentBO add(studentTO stuTO) throws SerException {
        return null;
    }

    /**
     * 删除数据
     *
     * @param id
     * @throws SerException
     */
    default void delete(String id) throws SerException {
    }

    /**
     * 更新数据
     *
     * @param stuTO
     * @return
     * @throws SerException
     */
    default studentBO update(studentTO stuTO) throws SerException {
        return null;
    }

}