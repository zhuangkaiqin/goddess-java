package com.bjike.goddess.enterpriseculturemanage.api;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.enterpriseculturemanage.bo.EnterpriseCultureInfoBO;
import com.bjike.goddess.enterpriseculturemanage.bo.PublicizeProgramInfoBO;
import com.bjike.goddess.enterpriseculturemanage.dto.PublicizeProgramInfoDTO;
import com.bjike.goddess.enterpriseculturemanage.enums.AuditResult;
import com.bjike.goddess.enterpriseculturemanage.to.GuidePermissionTO;
import com.bjike.goddess.enterpriseculturemanage.to.PublicizeProgramInfoTO;

import java.util.List;

/**
 * 宣传方案信息业务接口
 *
 * @Author: [ Jason ]
 * @Date: [ 2017-03-31 05:28 ]
 * @Description: [ 宣传方案信息业务接口 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public interface PublicizeProgramInfoAPI {

    /**
     * 下拉导航权限
     */
    default Boolean sonPermission() throws SerException {
        return null;
    }
    /**
     * 导航权限
     */
    default Boolean guidePermission(GuidePermissionTO guidePermissionTO) throws SerException {
        return null;
    }

    /**
     * 查询企业文化信息
     *
     * @return 企业文化信息
     */
    List<EnterpriseCultureInfoBO> findInfo() throws SerException;

    /**
     * 宣传方案信息
     *
     * @param to 宣传方案信息
     * @return 宣传方案信息
     */
    PublicizeProgramInfoBO addModel(PublicizeProgramInfoTO to) throws SerException;

    /**
     * 编辑宣传方案信息
     *
     * @param to 宣传方案信息
     * @return 宣传方案信息
     */
    PublicizeProgramInfoBO editModel(PublicizeProgramInfoTO to) throws SerException;

    /**
     * 删除宣传方案信息
     *
     * @param id 宣传方案信息id
     */
    void delete(String id) throws SerException;

    /**
     * 审核宣传方案信息
     *
     * @param id              id
     * @param auditResult     结果
     * @param auditSuggestion 意见
     */
    void audit(String id, AuditResult auditResult, String auditSuggestion) throws SerException;

    /**
     * 宣传方案信息分页查询
     *
     * @param dto 分页条件
     * @return 宣传方案信息结果集
     */
    List<PublicizeProgramInfoBO> pageList(PublicizeProgramInfoDTO dto) throws SerException;

    PublicizeProgramInfoBO findById(String id) throws SerException;

    Long count(PublicizeProgramInfoDTO dto) throws SerException;
}