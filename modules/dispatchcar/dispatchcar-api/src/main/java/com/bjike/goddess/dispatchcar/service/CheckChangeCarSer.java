package com.bjike.goddess.dispatchcar.service;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.api.service.Ser;
import com.bjike.goddess.dispatchcar.bo.CheckChangeCarBO;
import com.bjike.goddess.dispatchcar.dto.DispatchCarInfoDTO;
import com.bjike.goddess.dispatchcar.entity.CheckChangeCar;
import com.bjike.goddess.dispatchcar.dto.CheckChangeCarDTO;
import com.bjike.goddess.dispatchcar.to.CorrectMistakeTO;

import java.util.List;

/**
* 出车核对修改记录业务接口
* @Author:			[ jiangzaixuan ]
* @Date:			[  2017-09-25 11:24 ]
* @Description:	[ 出车核对修改记录业务接口 ]
* @Version:		[ v1.0.0 ]
* @Copy:   		[ com.bjike ]
*/
public interface CheckChangeCarSer extends Ser<CheckChangeCar, CheckChangeCarDTO> {
    /**
     * 查看核对修改记录
     */
    List<CheckChangeCarBO> list(CheckChangeCarDTO dto) throws SerException;

    /**
     * 问题解决
     */
    void modify(CorrectMistakeTO to) throws SerException;
 }