package com.bjike.goddess.people4.api;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.people4.bo.aClassBO;
import com.bjike.goddess.people4.service.aClassSer;
import com.bjike.goddess.people4.to.aClassTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试班级业务接口实现
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-19 10:22 ]
 * @Description: [ 测试班级业务接口实现 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@Service("aClassApiImpl")
public class aClassApiImpl implements aClassAPI {

    public aClassSer aClassSer;

    @Override
    public aClassBO addCla(aClassTO aClaTO) throws SerException {
        return aClassSer.addCla(aClaTO);
    }

    @Override
    public void deleteCla(String id) throws SerException {
        aClassSer.deleteCla(id);
    }

    @Override
    public aClassBO updateCla(aClassTO aClaTO) throws SerException {
        return aClassSer.updateCla(aClaTO);
    }

    @Override
    public List<aClassBO> findAllCla() throws SerException {
        return aClassSer.findAllCla();
    }
}