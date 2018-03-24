package com.bjike.goddess.people3.api;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.people3.bo.ClassBO;
import com.bjike.goddess.people3.service.ClassSer;
import com.bjike.goddess.people3.to.ClassTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试班级类业务接口实现
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-09 11:20 ]
 * @Description: [ 测试班级类业务接口实现 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@Service("classApiImpl")
public class ClassApiImpl implements ClassAPI {

    @Autowired
    public ClassSer claSer;

    @Override
    public ClassBO addCla(ClassTO claTO) throws SerException {
        return claSer.addCla(claTO);
    }

    @Override
    public void deleteCla(String id) throws SerException {
        claSer.deleteCla(id);
    }

    @Override
    public ClassBO updateCla(ClassTO claTO) throws SerException {
        return claSer.updateCla(claTO);
    }

    @Override
    public List<ClassBO> findAllCla() throws SerException {
        return claSer.findAllCla();
    }
}