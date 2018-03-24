package com.bjike.goddess.people4.action.people4;

import com.bjike.goddess.common.api.exception.ActException;
import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.api.restful.Result;
import com.bjike.goddess.common.consumer.restful.ActResult;
import com.bjike.goddess.people4.api.aClassAPI;
import com.bjike.goddess.people4.bo.aClassBO;
import com.bjike.goddess.people4.to.aClassTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试班级
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-19 10:22 ]
 * @Description: [ 测试班级 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@RestController
@RequestMapping("aclass")
public class aClassAction {

    @Autowired
    private aClassAPI aClaAPI;

    /**
     * 添加一个学生
     *
     * @param aClaTO
     * @return
     * @throws ActException
     */
    @PostMapping("v1/addCla")
    public Result addStu(aClassTO aClaTO) throws ActException {
        try {
            Boolean result = (null != aClaAPI.addCla(aClaTO));
            return ActResult.initialize(result);
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }

    /**
     * 删除一个班级
     *
     * @param id
     * @return
     * @throws ActException
     */
    @DeleteMapping("v1/deleteCla/{id}")
    public Result deleteStu(@PathVariable String id) throws ActException {
        try {
            aClaAPI.deleteCla(id);
            return new ActResult("删除成功");
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }

    /**
     * 更新一个班级信息
     *
     * @param aClaTO
     * @return
     * @throws ActException
     */
    @PostMapping("v1/updateCla")
    public Result updateStu(aClassTO aClaTO) throws ActException {
        try {
            aClassBO aClassBO = aClaAPI.updateCla(aClaTO);
            return ActResult.initialize(aClassBO);
        } catch (SerException se) {
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
    public Result findAllStu() throws ActException {
        try {
            List<aClassBO> studentBOs = aClaAPI.findAllCla();
            return ActResult.initialize(studentBOs);
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }

}