package com.bjike.goddess.people3.service;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.api.service.Ser;
import com.bjike.goddess.people3.bo.ClassBO;
import com.bjike.goddess.people3.dto.ClassDTO;
import com.bjike.goddess.people3.entity.Class;
import com.bjike.goddess.people3.to.ClassTO;

import java.util.List;

/**
 * 测试班级类业务接口
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-09 11:20 ]
 * @Description: [ 测试班级类业务接口 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public interface ClassSer extends Ser<Class, ClassDTO> {
    /**
     * 增加一个班级
     *
     * @return
     * @throws SerException
     */
    default ClassBO addCla(ClassTO claTO) throws SerException {
        return null;
    }

    /**
     * 根据id删除一个班级
     *
     * @param id
     * @throws SerException
     */
    default void deleteCla(String id) throws SerException {

    }

    /**
     * 修改一个班级信息
     *
     * @param claTO
     * @return
     * @throws SerException
     */
    default ClassBO updateCla(ClassTO claTO) throws SerException {
        return null;
    }

    /**
     * 查找全部班级
     *
     * @return
     * @throws SerException
     */
    default List<ClassBO> findAllCla() throws SerException {
        return null;
    }
}