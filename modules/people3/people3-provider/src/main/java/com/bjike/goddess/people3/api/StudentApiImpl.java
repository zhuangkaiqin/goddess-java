package com.bjike.goddess.people3.api;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.people3.bo.StudentBO;
import com.bjike.goddess.people3.dto.StudentDTO;
import com.bjike.goddess.people3.entity.Student;
import com.bjike.goddess.people3.service.StudentSer;
import com.bjike.goddess.people3.to.StudentTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试学生类业务接口实现
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-09 11:13 ]
 * @Description: [ 测试学生类业务接口实现 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@Service("studentApiImpl")
public class StudentApiImpl implements StudentAPI {


    @Autowired
    public StudentSer studentSer;

    @Override
    public StudentBO addStu(StudentTO stuTO) throws SerException {
        return studentSer.addStu(stuTO);
    }

    @Override
    public void deleteStu(String id) throws SerException {
        studentSer.deleteStu(id);
    }

    @Override
    public StudentBO updateStu(StudentTO stuTO) throws SerException {
        return studentSer.updateStu(stuTO);
    }

    @Override
    public List<StudentBO> findAllStu() throws SerException {
        return studentSer.findAllStu();
    }

    @Override
    public List<Student> findAllStuByClassNum(StudentDTO stuDTO) throws SerException {
        return studentSer.findAllStuByClassNum(stuDTO);
    }

    @Override
    public List<StudentBO> findAllStuByClassType(String ClaType) throws SerException {
        return studentSer.findAllStuByClassType(ClaType);
    }

    @Override
    public StudentBO findStuByStuId(String id) throws SerException {
        return studentSer.findStuByStuId(id);
    }
//
//    @Override
//    public List<Student> findByidd(String classNum) {
//
//        return studentSer.findByidd(classNum);
//    }
}