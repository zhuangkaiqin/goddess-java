package com.bjike.goddess.people4.service;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.jpa.service.ServiceImpl;
import com.bjike.goddess.common.utils.bean.BeanTransform;
import com.bjike.goddess.people4.bo.StudentBO;
import com.bjike.goddess.people4.bo.aClassBO;
import com.bjike.goddess.people4.dto.aClassDTO;
import com.bjike.goddess.people4.entity.aClass;
import com.bjike.goddess.people4.to.aClassTO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 测试班级业务实现
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-19 10:22 ]
 * @Description: [ 测试班级业务实现 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@CacheConfig(cacheNames = "people4SerCache")
@Service
public class aClassSerImpl extends ServiceImpl<aClass, aClassDTO> implements aClassSer {

    @Override
    public aClassBO addCla(aClassTO aClaTO) throws SerException {
        aClass aclass = BeanTransform.copyProperties(aClaTO,aClass.class,true);
        List<StudentBO> studentBOs = BeanTransform.copyProperties(aClaTO,aClass.class,true);

        Set result = new HashSet(studentBOs);
        aclass.setStudentSet(result);

        return BeanTransform.copyProperties(super.save(aclass),aClassBO.class);
    }

    @Override
    public void deleteCla(String id) throws SerException {
        super.remove(id);
    }

    @Override
    public aClassBO updateCla(aClassTO aClaTO) throws SerException {
        aClass aclass = super.findById(aClaTO.getId());
        BeanTransform.copyProperties(aClaTO,aclass,true);
        super.update(aclass);
        return BeanTransform.copyProperties(aclass,aClass.class,true);
    }

    @Override
    public List<aClassBO> findAllCla() throws SerException {
        List<aClass> aClasses = super.findAll();
        List<aClassBO> aClassBOs = BeanTransform.copyProperties(aClasses,aClassBO.class);

        for(int i = 0;i < aClassBOs.size();i++){
            aClassBO bo = aClassBOs.get(i);
            aClass classes = aClasses.get(i);

            bo.setStudents(classes.getStudentSet());
        }
        return aClassBOs;
    }
}