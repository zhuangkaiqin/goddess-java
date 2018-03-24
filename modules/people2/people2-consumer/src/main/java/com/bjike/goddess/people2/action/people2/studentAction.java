package com.bjike.goddess.people2.action.people2;

import com.bjike.goddess.common.api.exception.ActException;
import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.api.restful.Result;
import com.bjike.goddess.common.consumer.restful.ActResult;
import com.bjike.goddess.people2.api.studentAPI;
import com.bjike.goddess.people2.bo.studentBO;
import com.bjike.goddess.people2.to.studentTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试学生类
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-05 05:18 ]
 * @Description: [ 测试学生类 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@RestController
@RequestMapping("student")
public class studentAction {

    @Autowired
    private studentAPI studentAPI;

    /**
     * 添加一个学生
     *
     * @param stuTO
     * @return
     * @throws ActException
     */
    @PostMapping("v1/addStu")
    public Result addStu(studentTO stuTO) throws ActException {
        try {
            Boolean result = (null != studentAPI.addStu(stuTO));
            return ActResult.initialize(result);
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }

    /**
     * 删除一个学生
     *
     * @param id
     * @return
     * @throws ActException
     */
    @DeleteMapping("v1/deleteStu/{id}")
    public Result deleteStu(@PathVariable String id) throws ActException {
        try {
            studentAPI.deleteStu(id);
            return new ActResult("删除成功");
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }

    /**
     * 查找全部学生列表
     *
     * @return
     * @throws ActException
     */
    @GetMapping("v1/findStuall")
    public Result findStuAll() throws ActException {
        try {
            List<studentBO> studentBOs = studentAPI.findStuAll();
            return ActResult.initialize(studentBOs);
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }

    }

    /**
     * 根据学生学号查找学生信息
     *
     * @param stuNum
     * @return
     * @throws ActException
     */
    @PostMapping("v1/findStuByNum/{stuNum}")
    public Result findStuByNum(@PathVariable String stuNum) throws ActException {
        try {
            studentBO studentBO = studentAPI.findStuByNum(stuNum);
            return ActResult.initialize(studentBO);
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }

    /**
     * 更新学生信息
     *
     * @param studentTO
     * @return
     * @throws ActException
     */
    @PostMapping("v1/updateStu")
    public Result updateStu(studentTO studentTO) throws ActException{
        try {
            studentBO studentBO = studentAPI.updateStu(studentTO);
            return ActResult.initialize(studentBO);
        }catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }
}