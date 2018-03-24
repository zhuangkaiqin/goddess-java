package com.bjike.goddess.people.action.people;

import com.bjike.goddess.common.api.exception.ActException;
import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.api.restful.Result;
import com.bjike.goddess.common.consumer.restful.ActResult;
import com.bjike.goddess.common.utils.bean.BeanTransform;
import com.bjike.goddess.people.api.studentAPI;
import com.bjike.goddess.people.bo.studentBO;
import com.bjike.goddess.people.to.studentTO;
import com.bjike.goddess.people.vo.studentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试学生类
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-02 11:32 ]
 * @Description: [ 测试学生类 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@RestController
@RequestMapping("student")
public class studentAction {

    @Autowired
    private studentAPI studentapi;

    /**
     * 添加学生
     *
     * @param stuTO
     * @return
     * @throws ActException
     */
    @PostMapping("v1/add")
    public Result add(studentTO stuTO) throws ActException {
        try {
            Boolean result = (null != studentapi.add(stuTO));
            return ActResult.initialize(result);
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }

    @DeleteMapping("v1/delete/{id}")
    public Result delete(@PathVariable String id) throws ActException {
        try {
            studentapi.delete(id);
            return new ActResult("删除成功!");

        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }

    /**
     * 根据id查找数据
     *
     * @param Id
     * @return
     * @throws ActException
     */
    @GetMapping("v1/findbyid/{Id}")
    public Result findBy_Id(@PathVariable String Id) throws ActException {
        try {
            studentBO stubo = studentapi.findBy_Id(Id);
            return ActResult.initialize(BeanTransform.copyProperties(stubo, studentVO.class));
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }

    /**
     * 查找全部数据
     *
     * @return
     * @throws ActException
     */
    @GetMapping("v1/findall")
    public Result findall() throws ActException {
        try {
            List<studentBO> studentBOs = studentapi.findall();
            return ActResult.initialize(studentBOs);
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }

    /**
     * 更新数据
     *
     * @param stuTO
     * @return
     * @throws ActException
     */
    @PostMapping("v1/update")
    public Result update(studentTO stuTO) throws ActException {
        try {
            studentBO studentBO =  studentapi.update(stuTO);
            return ActResult.initialize(studentBO);
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }
}