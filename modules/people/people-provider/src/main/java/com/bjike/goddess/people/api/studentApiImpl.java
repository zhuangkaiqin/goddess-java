package com.bjike.goddess.people.api;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.people.bo.studentBO;
import com.bjike.goddess.people.service.studentSer;
import com.bjike.goddess.people.to.studentTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试学生类业务接口实现
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-02 11:32 ]
 * @Description: [ 测试学生类业务接口实现 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@Service("studentApiImpl")
public class studentApiImpl implements studentAPI {

    @Autowired
    private studentSer studentSer;

    @Override
    public List<studentBO> findall() throws SerException {
        return studentSer.findall();
    }

    @Override
    public studentBO findBy_Id(String id) throws SerException {
        return studentSer.findBy_Id(id);
    }

    @Override
    public studentBO add(studentTO stuTO) throws SerException {
        return studentSer.add(stuTO);
    }

    @Override
    public void delete(String id) throws SerException {
        studentSer.delete(id);
    }

    @Override
    public studentBO update(studentTO stuTO) throws SerException {
        return studentSer.update(stuTO);
    }
}