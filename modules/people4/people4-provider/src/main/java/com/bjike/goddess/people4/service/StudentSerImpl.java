package com.bjike.goddess.people4.service;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.jpa.service.ServiceImpl;
import com.bjike.goddess.common.utils.bean.BeanTransform;
import com.bjike.goddess.people4.bo.StudentBO;
import com.bjike.goddess.people4.dto.StudentDTO;
import com.bjike.goddess.people4.entity.Student;
import com.bjike.goddess.people4.to.StudentTO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试学生业务实现
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-19 10:07 ]
 * @Description: [ 测试学生业务实现 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@CacheConfig(cacheNames = "people4SerCache")
@Service
public class StudentSerImpl extends ServiceImpl<Student, StudentDTO> implements StudentSer {
    @Override
    public List<StudentBO> findAllStu() throws SerException {
        List<Student> students = super.findAll();
        return BeanTransform.copyProperties(students,StudentBO.class);
    }

    @Override
    public StudentBO addStu(StudentTO stuTO) throws SerException {
        Student student = BeanTransform.copyProperties(stuTO,Student.class,true);
        return BeanTransform.copyProperties(super.save(student),StudentBO.class);
    }

    @Override
    public void deleteStu(String id) throws SerException {
        super.remove(id);
    }

    @Override
    public StudentBO updateStu(StudentTO stuTO) throws SerException {
        Student student = super.findById(stuTO.getId());
        BeanTransform.copyProperties(stuTO,student,true);
        super.update(student);
        return BeanTransform.copyProperties(student,StudentBO.class);
    }
}