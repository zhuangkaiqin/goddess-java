package com.bjike.goddess.people4.api;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.people4.bo.aClassBO;
import com.bjike.goddess.people4.to.aClassTO;

import java.util.List;

/**
 * 测试班级业务接口
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-19 10:22 ]
 * @Description: [ 测试班级业务接口 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public interface aClassAPI {

    /**
     * 增加一个班级
     *
     * @param aClaTO
     * @return
     * @throws SerException
     */
    default aClassBO addCla(aClassTO aClaTO) throws SerException{
        return null;
    }

    /**
     * 删除一个班级
     *
     * @param id
     * @throws SerException
     */
    default void deleteCla(String id) throws SerException{

    }

    /**
     * 更新一个班级的信息
     *
     * @param aClaTO
     * @return
     * @throws SerException
     */
    default aClassBO updateCla(aClassTO aClaTO) throws SerException{
        return null;
    }

    /**
     * 列出全部班级
     *
     * @return
     * @throws SerException
     */
    default List<aClassBO> findAllCla() throws SerException{
        return null;
    }

}