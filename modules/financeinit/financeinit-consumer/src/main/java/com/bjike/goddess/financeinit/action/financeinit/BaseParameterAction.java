package com.bjike.goddess.financeinit.action.financeinit;

import com.bjike.goddess.common.api.entity.ADD;
import com.bjike.goddess.common.api.entity.EDIT;
import com.bjike.goddess.common.api.exception.ActException;
import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.api.restful.Result;
import com.bjike.goddess.common.consumer.interceptor.login.LoginAuth;
import com.bjike.goddess.common.consumer.restful.ActResult;
import com.bjike.goddess.common.utils.bean.BeanTransform;
import com.bjike.goddess.financeinit.api.BaseParameterAPI;
import com.bjike.goddess.financeinit.api.CompanyBasicInfoAPI;
import com.bjike.goddess.financeinit.bo.BaseParameterBO;
import com.bjike.goddess.financeinit.dto.BaseParameterDTO;
import com.bjike.goddess.financeinit.entity.BaseParameter;
import com.bjike.goddess.financeinit.to.BaseParameterTO;
import com.bjike.goddess.financeinit.vo.BaseParameterVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 基本参数
 *
 * @Author: [ lijuntao ]
 * @Date: [ 2017-10-10 04:11 ]
 * @Description: [ 基本参数 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@RestController
@RequestMapping("baseparameter")
public class BaseParameterAction {

    @Autowired
    private BaseParameterAPI baseParameterAPI;
    @Autowired
    private CompanyBasicInfoAPI companyBasicInfoAPI;

    /**
     * 列表总条数
     *
     * @param baseParameterDTO 基本参数dto
     * @des 获取所有基本参数总条数
     * @version v1
     */
    @GetMapping("v1/count")
    public Result count(BaseParameterDTO baseParameterDTO) throws ActException {
        try {
            Long count = baseParameterAPI.countBasicPara(baseParameterDTO);
            return ActResult.initialize(count);
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 一个基本参数
     *
     * @param id 基本参数id
     * @return class BaseParameterVO
     * @des 根据id获取基本参数
     * @version v1
     */
    @GetMapping("v1/getOneById/{id}")
    public Result getOneById(@PathVariable String id) throws ActException {
        try {
            BaseParameterVO baseParameterVO = BeanTransform.copyProperties(
                    baseParameterAPI.getOneById(id), BaseParameterVO.class);
            return ActResult.initialize(baseParameterVO);
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 基本参数列表
     *
     * @param baseParameterDTO 基本参数dto
     * @return class BaseParameterVO
     * @des 获取所有核算部门
     * @version v1
     */
    @GetMapping("v1/listAccount")
    public Result findListAccount(BaseParameterDTO baseParameterDTO, BindingResult bindingResult, HttpServletRequest request) throws ActException {
        try {
            List<BaseParameterVO> baseParameterVOS = BeanTransform.copyProperties(
                    baseParameterAPI.listBasicPara(baseParameterDTO), BaseParameterVO.class, request);
            return ActResult.initialize(baseParameterVOS);
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 添加核算部门
     *
     * @param baseParameterTO 核算部门数据to
     * @return class BaseParameterVO
     * @des 添加核算部门
     * @version v1
     */
    @LoginAuth
    @PostMapping("v1/add")
    public Result addAccount(@Validated(value = ADD.class) BaseParameterTO baseParameterTO, BindingResult bindingResult) throws ActException {
        try {
            BaseParameterBO baseParameterBO = baseParameterAPI.addBasicPara(baseParameterTO);
            return ActResult.initialize(BeanTransform.copyProperties(baseParameterBO, BaseParameterVO.class));
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }


    /**
     * 编辑核算部门
     *
     * @param baseParameterTO 核算部门数据bo
     * @return class BaseParameterVO
     * @des 编辑核算部门
     * @version v1
     */
    @LoginAuth
    @PutMapping("v1/edit")
    public Result editAccount(@Validated(value = EDIT.class) BaseParameterTO baseParameterTO, BindingResult bindingResult) throws ActException {
        try {
            BaseParameterBO baseParameterBO = baseParameterAPI.editBasicPara(baseParameterTO);
            return ActResult.initialize(BeanTransform.copyProperties(baseParameterBO, BaseParameterVO.class));
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 删除
     *
     * @param id id
     * @des 根据id删除核算部门
     * @version v1
     */
    @LoginAuth
    @DeleteMapping("v1/delete/{id}")
    public Result deleteAccount(@PathVariable String id) throws ActException {
        try {
            baseParameterAPI.deleteBasicPara(id);
            return new ActResult("delete success!");
        } catch (SerException e) {
            throw new ActException("删除失败：" + e.getMessage());
        }
    }
}