package com.bjike.goddess.rotation.service;

import com.bjike.goddess.archive.api.StaffRecordsAPI;
import com.bjike.goddess.archive.bo.StaffRecordsBO;
import com.bjike.goddess.assemble.api.ModuleAPI;
import com.bjike.goddess.common.api.dto.Restrict;
import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.jpa.service.ServiceImpl;
import com.bjike.goddess.common.provider.utils.RpcTransmit;
import com.bjike.goddess.common.utils.bean.BeanTransform;
import com.bjike.goddess.organize.api.PositionDetailUserAPI;
import com.bjike.goddess.organize.bo.PositionDetailBO;
import com.bjike.goddess.regularization.api.RegularizationAPI;
import com.bjike.goddess.rotation.bo.RecommendRotationBO;
import com.bjike.goddess.rotation.dto.RecommendRotationDTO;
import com.bjike.goddess.rotation.entity.RecommendRotation;
import com.bjike.goddess.rotation.enums.AuditType;
import com.bjike.goddess.rotation.enums.GuideAddrStatus;
import com.bjike.goddess.rotation.to.GuidePermissionTO;
import com.bjike.goddess.rotation.to.RecommendRotationTO;
import com.bjike.goddess.user.api.UserAPI;
import com.bjike.goddess.user.bo.UserBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

//import com.bjike.goddess.staffentry.api.EntryBasicInfoAPI;

/**
 * 岗位轮换推荐业务实现
 *
 * @Author: [ dengjunren ]
 * @Date: [ 2017-05-13 02:28 ]
 * @Description: [ 岗位轮换推荐业务实现 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@CacheConfig(cacheNames = "rotationSerCache")
@Service
public class RecommendRotationSerImpl extends ServiceImpl<RecommendRotation, RecommendRotationDTO> implements RecommendRotationSer {

//    @Autowired
//    private EntryBasicInfoAPI entryBasicInfoAPI;

    @Autowired
    private PositionDetailUserAPI positionDetailUserAPI;

    @Autowired
    private RegularizationAPI regularizationAPI;

    @Autowired
    private UserAPI userAPI;
    @Autowired
    private CusPermissionSer cusPermissionSer;

    @Autowired
    private SubsidyStandardSer subsidyStandardSer;
    @Autowired
    private ModuleAPI moduleAPI;
    @Autowired
    private StaffRecordsAPI staffRecordsAPI;

    private RecommendRotationBO transformBO(RecommendRotation entity) throws SerException {
        String userToken = RpcTransmit.getUserToken();
        RpcTransmit.transmitUserToken(userToken);
        RecommendRotationBO bo = BeanTransform.copyProperties(entity, RecommendRotationBO.class);
        StaffRecordsBO staffRecordsBO = null;
        if (moduleAPI.isCheck("archive")) {
            staffRecordsBO = staffRecordsAPI.findByName(entity.getUsername());
        }
        String time = null;
        if (moduleAPI.isCheck("regularization")) {
            time = regularizationAPI.getTime(entity.getUsername());
        }
        RpcTransmit.transmitUserToken(userToken);
        if (null != staffRecordsBO) {
            bo.setEntryTime(staffRecordsBO.getEntryTime());
        }
        if (null != time) {
            bo.setRegularTime(time);
        }
        if (null != entity.getApplyLevel()) {
            bo.setApplyLevelId(entity.getApplyLevel().getId());
            bo.setApplyLevelArrangement(entity.getApplyLevel().getArrangement());
        }
        if (null != entity.getRotationLevel()) {
            bo.setRotationLevelId(entity.getRotationLevel().getId());
            bo.setRotationLevelArrangement(entity.getRotationLevel().getArrangement());
        }
        return bo;
    }

    private List<RecommendRotationBO> transformBOList(List<RecommendRotation> list) throws SerException {
        List<RecommendRotationBO> bos = new ArrayList<>(0);
        for (RecommendRotation entity : list)
            bos.add(this.transformBO(entity));
        return bos;
    }

//    @Override
//    public RecommendRotationBO save(RecommendRotationTO to) throws SerException {
//        String userToken = RpcTransmit.getUserToken();
//        UserBO userBO = userAPI.currentUser();
//        RpcTransmit.transmitUserToken(userToken);
//        //user = userAPI.findByUsername(to.getUsername());
//        //查询入职模块的用户
//        List<EntryBasicInfoBO> entryBasicInfoVOList = entryBasicInfoAPI.getEntryBasicInfoByName(to.getUsername());
//        if (CollectionUtils.isEmpty(entryBasicInfoVOList)) {
//            throw new SerException("该用户不存在");
////            EntryBasicInfoBO user = entryBasicInfoVOList.get(0);
//        }
//
//        RecommendRotation entity = BeanTransform.copyProperties(to, RecommendRotation.class, true);
//        if (null == entryBasicInfoVOList.get(0))
//            throw new SerException("该用户不存在");
//        RpcTransmit.transmitUserToken(userToken);
//        if (moduleAPI.isCheck("organize")) {
//            List<PositionDetailBO> bos = positionDetailUserAPI.findPositionByUser(entryBasicInfoVOList.get(0).getId()).stream()
//                    .sorted(Comparator.comparing(PositionDetailBO::getArea)
//                            .thenComparing(PositionDetailBO::getDepartmentId))
//                    .collect(Collectors.toList());
//            RpcTransmit.transmitUserToken(userToken);
//            StringBuilder area = new StringBuilder(), department = new StringBuilder(), position = new StringBuilder(), arrangement = new StringBuilder();
//            String tempArea = "", tempDepartment = "", tempArrangement = "";
//            for (PositionDetailBO positionDetailBO : bos) {
//                if (!tempArea.equals(positionDetailBO.getArea())) {
//                    tempArea = positionDetailBO.getArea();
//                    area.append(tempArea + ",");
//                }
//                if (!tempDepartment.equals(positionDetailBO.getDepartmentName())) {
//                    tempDepartment = positionDetailBO.getDepartmentName();
//                    department.append(tempDepartment + ",");
//                }
//                position.append(positionDetailBO.getPosition());
//            }
//            for (String s : bos.stream()
//                    .sorted(Comparator.comparing(PositionDetailBO::getArrangementName))
//                    .map(PositionDetailBO::getArrangementName).collect(Collectors.toList()))
//                if (!tempArrangement.equals(s)) {
//                    tempArrangement = s;
//                    arrangement.append(s);
//                }
//            entity.setArea(area.toString());
//            entity.setPosition(position.toString());
//            entity.setArrangement(arrangement.toString());
//            entity.setDepartment(department.toString());
//        }
//        entity.setRecommend(userBO.getUsername());
//        entity.setRecommendTime(LocalDate.now());
//        entity.setAudit(AuditType.NONE);
//        entity.setApplyLevel(subsidyStandardSer.findById(to.getApplyLevelId()));
//        if (null == entity.getApplyLevel())
//            throw new SerException("推荐的层级不存在");
//        super.save(entity);
//        return this.transformBO(entity);
//    }

    @Override
    public RecommendRotationBO update(RecommendRotationTO to) throws SerException {
        String userToken = RpcTransmit.getUserToken();
        UserBO user = userAPI.currentUser();
        RpcTransmit.transmitUserToken(userToken);
        RecommendRotation entity = super.findById(to.getId());
        if (null == entity)
            throw new SerException("该数据不存在");
        if (!user.getUsername().equals(entity.getRecommend()))
            throw new SerException("不能修改他人的轮换推荐");
        BeanTransform.copyProperties(to, entity, true);
        if (moduleAPI.isCheck("organize")) {

            List<PositionDetailBO> bos = positionDetailUserAPI.findPositionByUser(userAPI.findByUsername(to.getUsername()).getId()).stream()
                    .sorted(Comparator.comparing(PositionDetailBO::getArea)
                            .thenComparing(PositionDetailBO::getDepartmentId))
                    .collect(Collectors.toList());
            StringBuilder area = new StringBuilder(), department = new StringBuilder(), position = new StringBuilder(), arrangement = new StringBuilder();
            String tempArea = "", tempDepartment = "", tempArrangement = "";
            for (PositionDetailBO positionDetailBO : bos) {
                if (!tempArea.equals(positionDetailBO.getArea())) {
                    tempArea = positionDetailBO.getArea();
                    area.append(tempArea + ",");
                }
                if (!tempDepartment.equals(positionDetailBO.getDepartmentName())) {
                    tempDepartment = positionDetailBO.getDepartmentName();
                    department.append(tempDepartment + ",");
                }
                position.append(positionDetailBO.getPosition());
            }
            for (String s : bos.stream()
                    .sorted(Comparator.comparing(PositionDetailBO::getArrangementName))
                    .map(PositionDetailBO::getArrangementName).collect(Collectors.toList()))
                if (!tempArrangement.equals(s)) {
                    tempArrangement = s;
                    arrangement.append(s);
                }

            entity.setArea(area.toString());
            entity.setPosition(position.toString());
            entity.setArrangement(arrangement.toString());
            entity.setDepartment(department.toString());
        }
        entity.setRecommend(user.getUsername());
        entity.setRecommendTime(LocalDate.now());
        entity.setModifyTime(LocalDateTime.now());
        entity.setApplyLevel(subsidyStandardSer.findById(to.getApplyLevelId()));
        if (null == entity.getApplyLevel())
            throw new SerException("推荐的层级不存在");
        super.update(entity);
        return this.transformBO(entity);
    }

    @Override
    public RecommendRotationBO delete(String id) throws SerException {
        String userToken = RpcTransmit.getUserToken();
        UserBO user = userAPI.currentUser();
        RpcTransmit.transmitUserToken(userToken);
        RecommendRotation entity = super.findById(id);
        if (!user.getUsername().equals(entity.getRecommend()))
            throw new SerException("不能删除他人的轮换推荐");
        if (null == entity)
            throw new SerException("该数据不存在");
        super.remove(entity);
        return this.transformBO(entity);
    }

    @Override
    public RecommendRotationBO opinion(RecommendRotationTO to) throws SerException {
        String userToken = RpcTransmit.getUserToken();
        UserBO user = userAPI.currentUser();
        RpcTransmit.transmitUserToken(userToken);
        RecommendRotation entity = super.findById(to.getId());
        if (null == entity)
            throw new SerException("该数据不存在");
        if (entity.getAudit() != AuditType.NONE)
            throw new SerException("该数据已被评价");
        BeanTransform.copyProperties(to, entity, true);
        entity.setModifyTime(LocalDateTime.now());
        entity.setGeneral(user.getUsername());
        entity.setAudit(to.getPass() ? AuditType.ALLOWED : AuditType.DENIED);
        entity.setRotationLevel(subsidyStandardSer.findById(to.getRotationLevelId()));
        if (to.getPass() && null == entity.getRotationLevel())
            throw new SerException("选择的层级不存在");
        super.update(entity);
        return this.transformBO(entity);
    }

    @Override
    public RecommendRotationBO getById(String id) throws SerException {
        RecommendRotation entity = super.findById(id);
        if (null == entity)
            throw new SerException("该数据不存在");
        return this.transformBO(entity);
    }

    @Override
    public List<RecommendRotationBO> maps(RecommendRotationDTO dto) throws SerException {
        dto.getSorts().add("audit=asc");
        dto.getSorts().add("rotationDate=desc");
        return this.transformBOList(super.findByPage(dto));
    }

    @Override
    public Long getTotal() throws SerException {
        RecommendRotationDTO dto = new RecommendRotationDTO();
        return super.count(dto);
    }

    @Override
    public List<RecommendRotationBO> findByUserArrangement(String username, String arrangementId) throws SerException {
        RecommendRotationDTO dto = new RecommendRotationDTO();
        dto.getConditions().add(Restrict.eq(USERNAME, username));
        dto.getConditions().add(Restrict.eq("audit", AuditType.ALLOWED.getValue()));
        dto.getConditions().add(Restrict.eq("rotationLevel.id", arrangementId));
        dto.getSorts().add("rotationDate=desc");
        return this.transformBOList(super.findByCis(dto));
    }

    /**
     * 核对查看权限（部门级别）
     */
    private void checkSeeIdentity() throws SerException {
        Boolean flag = false;
        String userToken = RpcTransmit.getUserToken();
        UserBO userBO = userAPI.currentUser();
        RpcTransmit.transmitUserToken(userToken);
        String userName = userBO.getUsername();
        if (!"admin".equals(userName.toLowerCase())) {
            flag = cusPermissionSer.getCusPermission("1");
            if (!flag) {
                throw new SerException("您不是相应部门的人员，不可以查看");
            }
        }
        RpcTransmit.transmitUserToken(userToken);
    }

    /**
     * 核对添加修改删除审核权限（岗位级别）
     */
    private void checkAddIdentity() throws SerException {
        Boolean flag = false;
        String userToken = RpcTransmit.getUserToken();
        UserBO userBO = userAPI.currentUser();
        RpcTransmit.transmitUserToken(userToken);
        String userName = userBO.getUsername();
        if (!"admin".equals(userName.toLowerCase())) {
            flag = cusPermissionSer.busCusPermission("2");
            if (!flag) {
                throw new SerException("您不是相应部门的人员，不可以操作");
            }
        }
        RpcTransmit.transmitUserToken(userToken);
    }


    /**
     * 导航栏核对查看权限（部门级别）
     */
    private Boolean guideSeeIdentity() throws SerException {
        Boolean flag = false;
        String userToken = RpcTransmit.getUserToken();
        UserBO userBO = userAPI.currentUser();
        RpcTransmit.transmitUserToken(userToken);
        String userName = userBO.getUsername();
        if (!"admin".equals(userName.toLowerCase())) {
            flag = cusPermissionSer.busCusPermission("2");
        } else {
            flag = true;
        }
        return flag;
    }

    @Override
    public Boolean sonPermission() throws SerException {
        String userToken = RpcTransmit.getUserToken();
        Boolean flagSee = guideSeeIdentity();
        RpcTransmit.transmitUserToken(userToken);
        Boolean flagAdd = guideAddIdentity();
        if (flagSee || flagAdd) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 导航栏核对添加修改删除审核权限（岗位级别）
     */
    private Boolean guideAddIdentity() throws SerException {
        Boolean flag = false;
        String userToken = RpcTransmit.getUserToken();
        UserBO userBO = userAPI.currentUser();
        RpcTransmit.transmitUserToken(userToken);
        String userName = userBO.getUsername();
        if (!"admin".equals(userName.toLowerCase())) {
            flag = cusPermissionSer.getCusPermission("1");
        } else {
            flag = true;
        }
        return flag;
    }

    @Override
    public Boolean guidePermission(GuidePermissionTO guidePermissionTO) throws SerException {
        String userToken = RpcTransmit.getUserToken();
        GuideAddrStatus guideAddrStatus = guidePermissionTO.getGuideAddrStatus();
        Boolean flag = true;
        switch (guideAddrStatus) {
            case LIST:
                flag = guideSeeIdentity();
                break;
            case ADD:
                flag = guideAddIdentity();
                break;
            case EDIT:
                flag = guideAddIdentity();
                break;
            case AUDIT:
                flag = guideAddIdentity();
                break;
            case DELETE:
                flag = guideAddIdentity();
                break;
            case CONGEL:
                flag = guideAddIdentity();
                break;
            case THAW:
                flag = guideAddIdentity();
                break;
            case COLLECT:
                flag = guideAddIdentity();
                break;
            case IMPORT:
                flag = guideAddIdentity();
                break;
            case EXPORT:
                flag = guideAddIdentity();
                break;
            case UPLOAD:
                flag = guideAddIdentity();
                break;
            case DOWNLOAD:
                flag = guideAddIdentity();
                break;
            case SEE:
                flag = guideSeeIdentity();
                break;
            case SEEFILE:
                flag = guideSeeIdentity();
                break;
            default:
                flag = true;
                break;
        }

        RpcTransmit.transmitUserToken(userToken);
        return flag;
    }
}