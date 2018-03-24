package com.bjike.goddess.people4.action.people4;

import com.bjike.goddess.common.api.exception.ActException;
import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.api.restful.Result;
import com.bjike.goddess.common.consumer.restful.ActResult;
import com.bjike.goddess.people4.api.StudentAPI;
import com.bjike.goddess.people4.bo.StudentBO;
import com.bjike.goddess.people4.to.StudentTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试学生
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-19 10:07 ]
 * @Description: [ 测试学生 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@RestController
@RequestMapping("student")
public class StudentAction {

    @Autowired
    private StudentAPI stuAPI;

    /**
     * 添加一个学生
     *
     * @param stuTO
     * @return
     * @throws ActException
     */
    @PostMapping("v1/addStu")
    public Result addStu(StudentTO stuTO) throws ActException {
        try {
            Boolean result = (null != stuAPI.addStu(stuTO));
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
            stuAPI.deleteStu(id);
            return new ActResult("删除成功");
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }

    /**
     * 更新一个学生信息
     *
     * @param stuTO
     * @return
     * @throws ActException
     */
    @PostMapping("v1/updateStu")
    public Result updateStu(StudentTO stuTO) throws ActException {
        try {
            StudentBO studentBO = stuAPI.updateStu(stuTO);
            return ActResult.initialize(studentBO);
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }

    /**
     * 查找全部学生
     *
     * @return
     * @throws ActException
     */
    @GetMapping("v1/findAllStu")
    public Result findAllStu() throws ActException {
        try {
            List<StudentBO> studentBOs = stuAPI.findAllStu();
            return ActResult.initialize(studentBOs);
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }

}