package com.bjike.goddess.people2.api;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.people2.bo.studentBO;
import com.bjike.goddess.people2.service.studentSer;
import com.bjike.goddess.people2.to.studentTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试学生类业务接口实现
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-05 05:18 ]
 * @Description: [ 测试学生类业务接口实现 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@Service("studentApiImpl")
public class studentApiImpl implements studentAPI {
    @Autowired
    private studentSer studentSer;

    @Override
    public List<studentBO> findStuAll() throws SerException {
        return studentSer.findStuAll();
    }

    @Override
    public studentBO findStuByNum(String id) throws SerException {
        return studentSer.findStuByNum(id);
    }

    @Override
    public studentBO addStu(studentTO stuTO) throws SerException {
        return studentSer.addStu(stuTO);
    }

    @Override
    public studentBO updateStu(studentTO stuTO) throws SerException {
        return studentSer.updateStu(stuTO);
    }

    @Override
    public void deleteStu(String id) throws SerException {
        studentSer.deleteStu(id);
    }
}