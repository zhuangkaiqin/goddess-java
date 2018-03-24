package com.bjike.goddess.people.service;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.jpa.service.ServiceImpl;
import com.bjike.goddess.common.utils.bean.BeanTransform;
import com.bjike.goddess.people.bo.studentBO;
import com.bjike.goddess.people.dto.studentDTO;
import com.bjike.goddess.people.entity.student;
import com.bjike.goddess.people.to.studentTO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试学生类业务实现
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-02 11:32 ]
 * @Description: [ 测试学生类业务实现 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@CacheConfig(cacheNames = "peopleSerCache")
@Service
public class studentSerImpl extends ServiceImpl<student, studentDTO> implements studentSer {


    @Override
    public List<studentBO> findall() throws SerException {
        List<student> students = super.findAll();
        return BeanTransform.copyProperties(students, studentBO.class);
    }

    @Override
    public studentBO findBy_Id(String id) throws SerException {

        student student = super.findById(id);

        return BeanTransform.copyProperties(student, studentBO.class);
    }

    @Override
    public studentBO add(studentTO stuTO) throws SerException {

        student student = BeanTransform.copyProperties(stuTO, student.class,true);

        return BeanTransform.copyProperties(super.save(student), studentBO.class);
    }

    @Override
    public studentBO update(studentTO stuTO) throws SerException {
        student s = super.findById(stuTO.getId());
        BeanTransform.copyProperties(stuTO, s,true);
        super.update(s);
        return BeanTransform.copyProperties(s, studentBO.class);
    }

    @Override
    public void delete(String id) throws SerException {
        super.remove(id);
    }
}