package com.bjike.goddess.staffentry.service;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.api.service.Ser;
import com.bjike.goddess.staffentry.bo.CommunicationFormworkBO;
import com.bjike.goddess.staffentry.dto.CommunicationFormworkDTO;
import com.bjike.goddess.staffentry.entity.CommunicationFormwork;
import com.bjike.goddess.staffentry.to.CommunicationFormworkTO;
import com.bjike.goddess.staffentry.to.GuidePermissionTO;

import java.util.List;

/**
 * 各类交流沟通模块业务接口
 *
 * @Author: [ lijuntao ]
 * @Date: [ 2017-09-25 09:48 ]
 * @Description: [ 各类交流沟通模块业务接口 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public interface CommunicationFormworkSer extends Ser<CommunicationFormwork, CommunicationFormworkDTO> {
    /**
     * 下拉导航权限
     */
    default Boolean sonPermission() throws SerException {
        return null;
    }


    /**
     * 功能导航权限
     */
    default Boolean guidePermission(GuidePermissionTO guidePermissionTO) throws SerException {
        return null;
    }

    /**
     * 总条数
     *
     * @param dto
     * @return
     * @throws SerException
     */
    Long countComm(CommunicationFormworkDTO dto) throws SerException;

    /**
     * 一个各类交流沟通模板
     *
     * @param id
     * @return
     * @throws SerException
     */
    CommunicationFormworkBO getOne(String id) throws SerException;

    /**
     * 分页查询各类交流沟通模块
     *
     * @return class CommunicationFormworkBO
     * @throws SerException
     */
    List<CommunicationFormworkBO> list(CommunicationFormworkDTO dto) throws SerException;

    /**
     * 保存各类交流沟通模块
     *
     * @param to 各类交流沟通模块to
     * @return class CommunicationFormworkBO
     * @throws SerException
     */
    CommunicationFormworkBO save(CommunicationFormworkTO to) throws SerException;

    /**
     * 根据id删除各类交流沟通模块
     *
     * @param id 各类交流沟通模块唯一标识
     * @throws SerException
     */
    void remove(String id) throws SerException;

    /**
     * 更新各类交流沟通模块
     *
     * @param to 各类交流沟通模块to
     * @throws SerException
     */
    void update(CommunicationFormworkTO to) throws SerException;

    /**
     * 删除
     *
     * @param id
     * @throws SerException
     */
    void delete(String id) throws SerException;
}