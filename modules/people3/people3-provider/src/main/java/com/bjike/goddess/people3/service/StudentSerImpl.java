package com.bjike.goddess.people3.service;

import com.bjike.goddess.common.api.dto.Restrict;
import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.jpa.service.ServiceImpl;
import com.bjike.goddess.common.utils.bean.BeanTransform;
import com.bjike.goddess.people3.bo.StudentBO;
import com.bjike.goddess.people3.dao.StudentRep;
import com.bjike.goddess.people3.dto.StudentDTO;
import com.bjike.goddess.people3.entity.Student;
import com.bjike.goddess.people3.to.StudentTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试学生类业务实现
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-09 11:13 ]
 * @Description: [ 测试学生类业务实现 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@CacheConfig(cacheNames = "people3SerCache")
@Service
public class StudentSerImpl extends ServiceImpl<Student, StudentDTO> implements StudentSer {

    @Autowired
    private StudentRep studentRep;

    @Override
    public StudentBO addStu(StudentTO stuTO) throws SerException {

        Student stu = BeanTransform.copyProperties(stuTO,Student.class,true);

        return BeanTransform.copyProperties(super.save(stu),StudentBO.class);
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

    @Override
    public List<StudentBO> findAllStu() throws SerException {
        List<Student> students = super.findAll();

        return BeanTransform.copyProperties(students,StudentBO.class);
    }

    @Override
    public List<Student> findAllStuByClassNum(StudentDTO stuDTO) throws SerException {
        stuDTO.getConditions().add(Restrict.eq("classNum",stuDTO.getClassNum()));
//        stuDTO.getSorts().add("classNum=desc");

        List<Student> students = super.findByCis(stuDTO);
        return students;
//                BeanTransform.copyProperties(students,StudentBO.class);
    }

    @Override
    public List<StudentBO> findAllStuByClassType(String ClaType) throws SerException {
        StudentDTO stuDTO = new StudentDTO();
        stuDTO.getConditions().add(Restrict.eq("ClassNum", stuDTO.getClassNum()));
//        stuDTO.getSorts().add("StuNum=desc");
//
        List<Student> students = super.findByCis(stuDTO);



        return BeanTransform.copyProperties(students,StudentBO.class);
    }

    @Override
    public StudentBO findStuByStuId(String id) throws SerException {
        Student student = super.findById(id);

        return BeanTransform.copyProperties(student,StudentBO.class);
    }

    @Override
    public List<Student> findByidd(String classNum) {
        List<Student> list = studentRep.findByClassNum(classNum);
//        List<Student> list1 = null;
//        for (Student student : list) {
//            student.setCreateTime(null);
//            student.setModifyTime(null);
//            list1.add(student);
//        }
        System.out.println(list);
        return list;
    }

}