package com.bjike.goddess.people3.action.people3;

import com.bjike.goddess.common.api.exception.ActException;
import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.api.restful.Result;
import com.bjike.goddess.common.consumer.restful.ActResult;
import com.bjike.goddess.people3.api.StudentAPI;
import com.bjike.goddess.people3.bo.StudentBO;
import com.bjike.goddess.people3.dto.StudentDTO;
import com.bjike.goddess.people3.entity.Student;
import com.bjike.goddess.people3.to.StudentTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试学生类
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-09 11:13 ]
 * @Description: [ 测试学生类 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@RestController
@RequestMapping("student")
public class StudentAction {

    @Autowired
    private StudentAPI studentAPI;

    /**
     * 增加一个学生
     *
     * @param stuTO
     * @return
     * @throws ActException
     */
    @PostMapping("v1/addStu")
    public Result addStu(StudentTO stuTO) throws ActException {
        try {
            Boolean result = (null != studentAPI.addStu(stuTO));
            return ActResult.initialize(result);
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }

    /**
     * 根据id删除一个学生
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
     * 修改一个学生信息
     *
     * @param stuTO
     * @return
     * @throws ActException
     */
    @PostMapping("v1/updateStu")
    public Result updateStu(StudentTO stuTO) throws ActException {
        try {
            StudentBO studentBO = studentAPI.updateStu(stuTO);
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
            List<StudentBO> studentBOList = studentAPI.findAllStu();
            return ActResult.initialize(studentBOList);
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }

    /**
     * 根据教室编号找那个班级的学生
     *
     * @return
     * @throws ActException
     */
    @PostMapping("v1/findAllStuByClassNum")
    public Result findAllStuByClassNum(StudentDTO stuDTO) throws ActException {
        try {
            List<Student> studentBOs = studentAPI.findAllStuByClassNum(stuDTO);
            return ActResult.initialize(studentBOs);
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }

    /**
     * 根据专业查找学生
     *
     * @param ClaType
     * @return
     * @throws ActException
     */
    @PostMapping("v1/findAllStuByClassType/{ClaType}")
    public Result findAllStuByClassType(@PathVariable String ClaType) throws ActException {
        try {
            List<StudentBO> studentBOs = studentAPI.findAllStuByClassType(ClaType);
            return ActResult.initialize(studentBOs);
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }

    /**
     * 根据学生id查找学生
     *
     * @param id
     * @return
     * @throws SerException
     */
    @PostMapping("v1/findStuByStuId/{id}")
    public Result findStuByStuId(@PathVariable String id) throws ActException {
        try {
            StudentBO studentBO = studentAPI.findStuByStuId(id);
            return ActResult.initialize(studentBO);
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }

//    @GetMapping("/v1/test/{classNum}")
//    public Result test(@PathVariable String classNum) {
//        System.out.println(classNum);
//        System.out.println();
//        return new ActResult("success",studentAPI.findByidd(classNum));
//    }
}