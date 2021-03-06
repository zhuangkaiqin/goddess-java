package com.bjike.goddess.enterpriseculturemanage.api;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.utils.bean.BeanTransform;
import com.bjike.goddess.enterpriseculturemanage.bo.EnterpriseCultureInfoBO;
import com.bjike.goddess.enterpriseculturemanage.bo.PeriodicalProgramInfoBO;
import com.bjike.goddess.enterpriseculturemanage.dto.PeriodicalProgramInfoDTO;
import com.bjike.goddess.enterpriseculturemanage.enums.AuditResult;
import com.bjike.goddess.enterpriseculturemanage.service.EnterpriseCultureInfoSer;
import com.bjike.goddess.enterpriseculturemanage.service.PeriodicalProgramInfoSer;
import com.bjike.goddess.enterpriseculturemanage.to.GuidePermissionTO;
import com.bjike.goddess.enterpriseculturemanage.to.PeriodicalProgramInfoTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 刊物方案信息业务接口实现
 *
 * @Author: [ Jason ]
 * @Date: [ 2017-04-01 09:07 ]
 * @Description: [ 刊物方案信息业务接口实现 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@Service("periodicalProgramInfoApiImpl")
public class PeriodicalProgramInfoApiImpl implements PeriodicalProgramInfoAPI {

    @Autowired
    private PeriodicalProgramInfoSer periodicalProgramInfoSer;
    @Autowired
    private EnterpriseCultureInfoSer enterpriseCultureInfoSer;

    @Override
    public Boolean sonPermission() throws SerException {
        return periodicalProgramInfoSer.sonPermission();
    }

    @Override
    public Boolean guidePermission(GuidePermissionTO guidePermissionTO) throws SerException {
        return periodicalProgramInfoSer.guidePermission(guidePermissionTO);
    }

    @Override
    public List<EnterpriseCultureInfoBO> findInfo() throws SerException {
        return enterpriseCultureInfoSer.findThawAll();
    }

    @Override
    public PeriodicalProgramInfoBO addModel(PeriodicalProgramInfoTO to) throws SerException {
        return periodicalProgramInfoSer.insertModel(to);
    }

    @Override
    public PeriodicalProgramInfoBO editModel(PeriodicalProgramInfoTO to) throws SerException {
        return periodicalProgramInfoSer.updateModel(to);
    }

    @Override
    public void delete(String id) throws SerException {
        periodicalProgramInfoSer.remove(id);
    }

    @Override
    public void audit(String id , AuditResult auditResult, String  auditSuggestion) throws SerException {
        periodicalProgramInfoSer.audit(id,auditResult,auditSuggestion);
    }

    @Override
    public List<PeriodicalProgramInfoBO> pageList(PeriodicalProgramInfoDTO dto) throws SerException {
        return periodicalProgramInfoSer.pageList(dto);
    }

    @Override
    public Long count(PeriodicalProgramInfoDTO dto) throws SerException {
        return periodicalProgramInfoSer.count(dto);
    }

    @Override
    public PeriodicalProgramInfoBO findById(String id) throws SerException {
        return BeanTransform.copyProperties(periodicalProgramInfoSer.findById(id),PeriodicalProgramInfoBO.class);
    }
}