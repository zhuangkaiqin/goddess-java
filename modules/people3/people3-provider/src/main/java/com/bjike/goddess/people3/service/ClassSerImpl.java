package com.bjike.goddess.people3.service;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.jpa.service.ServiceImpl;
import com.bjike.goddess.common.utils.bean.BeanTransform;
import com.bjike.goddess.people3.bo.ClassBO;
import com.bjike.goddess.people3.bo.StudentBO;
import com.bjike.goddess.people3.dto.ClassDTO;
import com.bjike.goddess.people3.entity.Class;
import com.bjike.goddess.people3.entity.Student;
import com.bjike.goddess.people3.to.ClassTO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 测试班级类业务实现
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-09 11:20 ]
 * @Description: [ 测试班级类业务实现 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@CacheConfig(cacheNames = "people3SerCache")
@Service
public class ClassSerImpl extends ServiceImpl<Class, ClassDTO> implements ClassSer {

    @Override
    public ClassBO addCla(ClassTO claTO) throws SerException {

        Class aClass = BeanTransform.copyProperties(claTO, Class.class, true);
        List<StudentBO> StudentBO = BeanTransform.copyProperties(claTO.getStudents(), Student.class);

        Set result = new HashSet(StudentBO);
        aClass.setStudentSet(result);

        return BeanTransform.copyProperties(super.save(aClass), ClassBO.class);
    }

    @Override
    public void deleteCla(String id) throws SerException {
        super.remove(id);
    }

    @Override
    public ClassBO updateCla(ClassTO claTO) throws SerException {

        Class aClass = super.findById(claTO.getId());
        BeanTransform.copyProperties(claTO, aClass, true);
        super.update(aClass);
        return BeanTransform.copyProperties(aClass, Class.class, true);
    }

    @Override
    public List<ClassBO> findAllCla() throws SerException {
        List<Class> classes = super.findAll();

        List<ClassBO>  classBOs = BeanTransform.copyProperties(classes, ClassBO.class);

        for (int i = 0;i<classBOs.size();i++){
            ClassBO bo = classBOs.get(i); //2
            Class c = classes.get(i);     //2
            bo.setStudentSet( c.getStudentSet() );
        }



//        for (Class c : classes) {
//
//            //BeanTransform.copyProperties(classes, ClassBO.class, "studentSet");
//            List<Student> s = BeanTransform.copyProperties(c.getStudentSet(), StudentBO.class);
//            Set result = new HashSet(s);
//            c.setStudentSet(result);
//        }

        return classBOs;
    }
}