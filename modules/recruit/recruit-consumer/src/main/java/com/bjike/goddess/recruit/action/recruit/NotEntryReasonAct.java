package com.bjike.goddess.recruit.action.recruit;

import com.bjike.goddess.common.api.entity.ADD;
import com.bjike.goddess.common.api.entity.EDIT;
import com.bjike.goddess.common.api.exception.ActException;
import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.api.restful.Result;
import com.bjike.goddess.common.consumer.restful.ActResult;
import com.bjike.goddess.common.utils.bean.BeanTransform;
import com.bjike.goddess.recruit.api.NotEntryReasonAPI;
import com.bjike.goddess.recruit.bo.FailFirstInterviewReasonBO;
import com.bjike.goddess.recruit.bo.NotEntryReasonBO;
import com.bjike.goddess.recruit.dto.FailFirstInterviewReasonDTO;
import com.bjike.goddess.recruit.dto.NotEntryReasonDTO;
import com.bjike.goddess.recruit.to.FailFirstInterviewReasonTO;
import com.bjike.goddess.recruit.to.NotEntryReasonTO;
import com.bjike.goddess.recruit.vo.FailFirstInterviewReasonVO;
import com.bjike.goddess.recruit.vo.NotEntryReasonVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 未入职原因
 *
 * @Author: [sunfengtao]
 * @Date: [2017-03-15 16:42]
 * @Description: [ ]
 * @Version: [1.0.0]
 * @Copy: [com.bjike]
 */
@RestController
@RequestMapping("recruit/notEntryReason")
public class NotEntryReasonAct {

    @Autowired
    private NotEntryReasonAPI notEntryReasonAPI;

    /**
     * 获取列表
     *
     * @param dto 未入职原因传输对象
     * @return class NotEntryReasonVO
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/list")
    public Result list(NotEntryReasonDTO dto) throws ActException {
        try {
            List<NotEntryReasonBO> boList = notEntryReasonAPI.list(dto);
            List<NotEntryReasonVO> voList = BeanTransform.copyProperties(boList, NotEntryReasonVO.class);
            return ActResult.initialize(voList);
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 添加未入职原因
     *
     * @param to 未入职原因to信息
     * @return class NotEntryReasonVO
     * @throws ActException
     * @version v1
     */
    @PostMapping("v1/add")
    public Result add(@Validated({ADD.class}) NotEntryReasonTO to) throws ActException {
        try {
            NotEntryReasonBO bo = notEntryReasonAPI.save(to);
            NotEntryReasonVO vo = BeanTransform.copyProperties(bo, NotEntryReasonVO.class);
            return ActResult.initialize(vo);
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 删除未入职原因
     *
     * @param id 未入职原因唯一标识
     * @throws ActException
     * @version v1
     */
    @DeleteMapping("v1/delete/{id}")
    public Result delete(@PathVariable String id) throws ActException {
        try {
            notEntryReasonAPI.remove(id);
            return new ActResult("delete success!");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 编辑未入职原因
     *
     * @param to 未入职原因to信息
     * @throws ActException
     * @version v1
     */
    @PutMapping("v1/edit")
    public Result edit(@Validated({EDIT.class}) NotEntryReasonTO to) throws ActException {
        try {
            notEntryReasonAPI.update(to);
            return new ActResult("edit success!");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }
}