package com.bjike.goddess.people3.action.people3;

import com.bjike.goddess.common.api.exception.ActException;
import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.api.restful.Result;
import com.bjike.goddess.common.consumer.restful.ActResult;
import com.bjike.goddess.people3.api.ClassAPI;
import com.bjike.goddess.people3.bo.ClassBO;
import com.bjike.goddess.people3.to.ClassTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试班级类
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-09 11:20 ]
 * @Description: [ 测试班级类 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@RestController
@RequestMapping("class")
public class ClassAction {

    @Autowired
    private ClassAPI classAPI;

    /**
     * 增加一个班级
     *
     * @param claTO
     * @return
     * @throws ActException
     */
    @PostMapping("v1/addCla")
    public Result addCla(ClassTO claTO) throws ActException {
        try {
            Boolean result = (null != classAPI.addCla(claTO));
            return ActResult.initialize(result);
        }catch (SerException se){
            throw new ActException(se.getMessage());
        }

    }

    /**
     * 根据id删除一个班级
     *
     * @param id
     * @return
     * @throws ActException
     */
    @DeleteMapping("v1/deleteCla/{id}")
    public Result deleteCla(@PathVariable String id) throws ActException {
        try {
            classAPI.deleteCla(id);
            return new ActResult("删除成功");
        }catch (SerException se){
            throw new ActException(se.getMessage());
        }
    }

    /**
     * 修改一个班级信息
     *
     * @param claTO
     * @return
     * @throws ActException
     */
    @PostMapping("v1/updateCla")
    public Result updateCla(ClassTO claTO) throws ActException {
        try {
            ClassBO classBO = classAPI.updateCla(claTO);
            return ActResult.initialize(classBO);
        }catch (SerException se){
            throw new ActException(se.getMessage());
        }
    }

    /**
     * 查找全部班级
     *
     * @return
     * @throws ActException
     */
    @GetMapping("v1/findAllCla")
    public Result findAllCla() throws ActException {
        try {
            List<ClassBO> classes = classAPI.findAllCla();
            return ActResult.initialize(classes);
        }catch (SerException se){
            throw new ActException(se.getMessage());
        }
    }
}