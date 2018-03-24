package com.bjike.goddess.people4.api;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.people4.bo.StudentBO;
import com.bjike.goddess.people4.service.StudentSer;
import com.bjike.goddess.people4.to.StudentTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试学生业务接口实现
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-19 10:07 ]
 * @Description: [ 测试学生业务接口实现 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@Service("studentApiImpl")
public class StudentApiImpl implements StudentAPI {
    public StudentSer studentSer;

    @Override
    public List<StudentBO> findAllStu() throws SerException {
        return studentSer.findAllStu();
    }

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
}