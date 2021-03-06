package com.bjike.goddess.subjectcollect.service;

import com.bjike.goddess.common.api.dto.Restrict;
import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.jpa.service.ServiceImpl;
import com.bjike.goddess.common.provider.utils.RpcTransmit;
import com.bjike.goddess.common.utils.bean.BeanTransform;
import com.bjike.goddess.common.utils.excel.Excel;
import com.bjike.goddess.common.utils.excel.ExcelUtil;
import com.bjike.goddess.subjectcollect.bo.CompareCollectBO;
import com.bjike.goddess.subjectcollect.bo.SubjectCollectBO;
import com.bjike.goddess.subjectcollect.dto.SubjectCollectDTO;
import com.bjike.goddess.subjectcollect.entity.SubjectCollect;
import com.bjike.goddess.subjectcollect.enums.GuideAddrStatus;
import com.bjike.goddess.subjectcollect.excel.SonPermissionObject;
import com.bjike.goddess.subjectcollect.excel.SubjectCollectExport;
import com.bjike.goddess.subjectcollect.to.GuidePermissionTO;
import com.bjike.goddess.subjectcollect.to.SubjectCollectTO;
import com.bjike.goddess.user.api.UserAPI;
import com.bjike.goddess.user.bo.UserBO;
import com.bjike.goddess.voucher.api.VoucherGenerateAPI;
import com.bjike.goddess.voucher.bo.VoucherGenerateBO;
import com.bjike.goddess.voucher.dto.VoucherGenerateDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 科目汇总表业务实现
 *
 * @Author: [ xiazhili ]
 * @Date: [ 2017-04-07 04:02 ]
 * @Description: [ 科目汇总表业务实现 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@CacheConfig(cacheNames = "subjectcollectSerCache")
@Service
public class SubjectCollectSerImpl extends ServiceImpl<SubjectCollect, SubjectCollectDTO> implements SubjectCollectSer {
    @Autowired
    private UserAPI userAPI;
    @Autowired
    private CusPermissionSer cusPermissionSer;
    @Autowired
    private VoucherGenerateAPI voucherGenerateAPI;

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
            flag = cusPermissionSer.getCusPermission("1");
        } else {
            flag = true;
        }
        return flag;
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
            flag = cusPermissionSer.busCusPermission("2");
        } else {
            flag = true;
        }
        return flag;
    }

    @Override
    public List<SonPermissionObject> sonPermission() throws SerException {
        List<SonPermissionObject> list = new ArrayList<>();
        String userToken = RpcTransmit.getUserToken();
        Boolean flagSeeInfo = guideSeeIdentity();
        RpcTransmit.transmitUserToken(userToken);
        Boolean flagAddInfo = guideAddIdentity();

        SonPermissionObject obj = new SonPermissionObject();

        obj = new SonPermissionObject();
        obj.setName("subjectcollect");
        obj.setDescribesion("科目汇总表");
        if (flagSeeInfo || flagAddInfo) {
            obj.setFlag(true);
        } else {
            obj.setFlag(false);
        }
        list.add(obj);

        return list;
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
        return flag;
    }

    @Override
    public Long countSubjectCollect(SubjectCollectDTO subjectCollectDTO) throws SerException {
        Long count = super.count(subjectCollectDTO);
        return count;
    }

    @Override
    public SubjectCollectBO getOne(String id) throws SerException {
        SubjectCollect subjectCollect = super.findById(id);
        return BeanTransform.copyProperties(subjectCollect, SubjectCollectBO.class);
    }

    @Override
    public List<SubjectCollectBO> findListSubjectCollect(SubjectCollectDTO subjectCollectDTO) throws SerException {
        checkSeeIdentity();
        List<SubjectCollect> subjectCollects = super.findByPage(subjectCollectDTO);
        List<SubjectCollectBO> subjectCollectBOS = BeanTransform.copyProperties(subjectCollects, SubjectCollectBO.class);
        return subjectCollectBOS;
    }

    @Transactional(rollbackFor = SerException.class)
    @Override
    public SubjectCollectBO insertSubjectCollect(SubjectCollectTO subjectCollectTO) throws SerException {
        checkAddIdentity();
        //zhuangkaiqin
        if (1 > subjectCollectTO.getMonths() || 12 < subjectCollectTO.getMonths()) {
            throw new SerException("输入的月份不符合");
        }
        SubjectCollect subjectCollect = BeanTransform.copyProperties(subjectCollectTO, SubjectCollect.class);
        subjectCollect.setCreateTime(LocalDateTime.now());
        super.save(subjectCollect);
        return BeanTransform.copyProperties(subjectCollect, SubjectCollectBO.class);
    }

    @Transactional(rollbackFor = SerException.class)
    @Override
    public SubjectCollectBO editSubjectCollect(SubjectCollectTO subjectCollectTO) throws SerException {
        checkAddIdentity();
        SubjectCollect subjectCollect = super.findById(subjectCollectTO.getId());
        BeanTransform.copyProperties(subjectCollectTO, subjectCollect, true);
        subjectCollect.setModifyTime(LocalDateTime.now());
        super.update(subjectCollect);
        return BeanTransform.copyProperties(subjectCollect, SubjectCollectBO.class);
    }

    @Override
    public byte[] exportExcel(SubjectCollectDTO dto) throws SerException {
        if (null != dto.getArea()) {
            dto.getConditions().add(Restrict.in("area", dto.getArea()));
        }
        List<SubjectCollect> list = super.findByCis(dto);
        List<SubjectCollectExport> exports = new ArrayList<>();
        list.stream().forEach(str -> {
            SubjectCollectExport export = BeanTransform.copyProperties(str, SubjectCollectExport.class);
            exports.add(export);
        });
        Excel excel = new Excel(0, 2);
        byte[] bytes = ExcelUtil.clazzToExcel(exports, excel);
        return bytes;
    }

    @Transactional(rollbackFor = SerException.class)
    @Override
    public void removeSubjectCollect(String id) throws SerException {
        checkAddIdentity();
        super.remove(id);
    }

    @Override
    public List<String> getArea() throws SerException {
        String[] fields = new String[]{"area"};
        List<SubjectCollectBO> subjectCollectBOS = super.findBySql("select distinct area from subjectcollect_subjectcollect group by area order by area asc ", SubjectCollectBO.class, fields);
        List<String> areasList = subjectCollectBOS.stream().map(SubjectCollectBO::getArea)
                .filter(area -> (StringUtils.isNotBlank(area))).distinct().collect(Collectors.toList());


        return areasList;
    }

    @Override
    public List<CompareCollectBO> collectCompare(Integer[] months) throws SerException {
        String[] monthsTemp = new String[months.length];
        for (int i = 0; i < months.length; i++) {
            monthsTemp[i] = "'" + months[i] + "'";
        }
        String monthsStr = StringUtils.join(monthsTemp, ",");
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT months AS months,firstSubject AS firstSubject,area AS area, ");
        sb.append(" projectName AS projectName,projectGroup AS projectGroup, ");
        sb.append(" sum(beginningDebitAmount) AS beginningDebitAmount,sum(beginningCreditAmount) AS beginningCreditAmount, ");
        sb.append(" (sum(beginningDebitAmount)-sum(beginningCreditAmount)) AS beginMinusMoney, ");
        sb.append(" sum(issueDebitAmount) AS issueDebitAmount,sum(issueCreditAmount) AS issueCreditAmount, ");
        sb.append(" (sum(issueDebitAmount)-sum(issueCreditAmount)) AS issueMinusMoney, ");
        sb.append(" sum(endDebitAmount) AS endDebitAmount,sum(endCreditAmount) AS endCreditAmount, ");
        sb.append(" (sum(endDebitAmount)-sum(endCreditAmount)) AS endMinusMoney FROM subjectcollect_subjectcollect a ");
        sb.append(" WHERE months IN (%s) GROUP BY firstSubject,area,projectName,projectGroup, ");
        sb.append(" months ORDER BY months ");
        String sql = sb.toString();
        sql = String.format(sql, monthsStr);
        String[] fields = new String[]{"months", "firstSubject", "area", "projectName", "projectGroup",
                "beginningDebitAmount", "beginningCreditAmount", "beginMinusMoney", "issueDebitAmount", "issueCreditAmount",
                "issueMinusMoney", "endDebitAmount", "endCreditAmount", "endMinusMoney"};
        List<CompareCollectBO> compareCollectBOS = super.findBySql(sql, CompareCollectBO.class, fields);
        return compareCollectBOS;
    }


    @Override
    //chenjunhao
    public SubjectCollectBO getSum(SubjectCollectDTO dto) throws SerException {
        List<SubjectCollect> subjectCollects = super.findByCis(dto);
        double beginDebit = 0;  //借方
        double beginCredit = 0;    //贷方
        double endDebit = 0;
        double endCredit = 0;
        double currentDebit = 0;
        double currentCredit = 0;
        String projectName = null;
        for (SubjectCollect subjectCollect : subjectCollects) {
            beginDebit += subjectCollect.getBeginningDebitAmount();
            beginCredit += subjectCollect.getBeginningCreditAmount();
            endDebit += subjectCollect.getEndDebitAmount();
            endCredit += subjectCollect.getEndCreditAmount();
            currentDebit += subjectCollect.getIssueDebitAmount();
            currentCredit += subjectCollect.getIssueCreditAmount();
            projectName = subjectCollect.getProjectName();
        }
        SubjectCollectBO bo = new SubjectCollectBO();
        bo.setBeginningDebitAmount(beginDebit);
        bo.setBeginningCreditAmount(beginCredit);
        bo.setEndDebitAmount(endDebit);
        bo.setEndCreditAmount(endCredit);
        bo.setIssueDebitAmount(currentDebit);
        bo.setIssueCreditAmount(currentCredit);
        bo.setProjectName(projectName);
        return bo;
    }

    @Override
    //chenjunhao
    public Set<String> allFirstSubjects() throws SerException {
        List<SubjectCollect> list = super.findAll();
        Set<String> set = new HashSet<String>();
        for (SubjectCollect s : list) {
            set.add(s.getFirstSubject());
        }
        return set;
    }

    @Override
    //chenjunhao
    public Set<String> allProjectNames() throws SerException {
        List<SubjectCollect> list = super.findAll();
        Set<String> set = new HashSet<String>();
        for (SubjectCollect s : list) {
            set.add(s.getProjectName());
        }
        return set;
    }

    @Override
    public List<VoucherGenerateBO> synchrodata() throws SerException {
        VoucherGenerateDTO voucherGenerateDTO = new VoucherGenerateDTO();
        List<VoucherGenerateBO> voucherGenerateBOs = voucherGenerateAPI.listNoPage(voucherGenerateDTO);
        List<SubjectCollectBO> subjectCollectBOs = new ArrayList<>();
        if (null != voucherGenerateBOs && voucherGenerateBOs.size() > 0) {
            for (VoucherGenerateBO voucherGenerateBO : voucherGenerateBOs) {
                SubjectCollectBO subjectCollectBO = new SubjectCollectBO();
                subjectCollectBO.setVoucher(true);
                subjectCollectBO.setCode(voucherGenerateBO.getVoucherNum().toString());
                subjectCollectBO.setMonths(LocalDate.parse(voucherGenerateBO.getVoucherDate()).getMonthValue());
                subjectCollectBO.setFirstSubject(voucherGenerateBO.getFirstSubject());
                subjectCollectBO.setArea(voucherGenerateBO.getArea());
                subjectCollectBO.setProjectName(voucherGenerateBO.getProjectName());
                subjectCollectBO.setProjectGroup(voucherGenerateBO.getProjectGroup());
                subjectCollectBO.setBeginningDebitAmount(voucherGenerateBO.getBorrowMoney());
                subjectCollectBO.setBeginningCreditAmount(voucherGenerateBO.getLoanMoney());

            }
        }
        return null;
    }

    //zhuangkaiqin
    @Override
    public List<SubjectCollectBO> subjectCollect(SubjectCollectDTO subjectCollectDTO) throws SerException {
        String firstSubject = subjectCollectDTO.getFirstSubject();
        String[] field = new String[]{"firstSubject", "months", "beginningDebitAmount",
                "beginningCreditAmount", "issueDebitAmount", "issueCreditAmount",
                "endDebitAmount", "endCreditAmount"};
        String sql = getSql("firstSubject", firstSubject, subjectCollectDTO);
        List<SubjectCollect> list = new ArrayList<>();
        //表头为一级科目/月份/期初借方余额/期初贷方余额/本期借方发生额/本期贷方发生额/期末借方余额/期末贷方月余额
        list = super.findBySql(sql, SubjectCollect.class, field);
        return BeanTransform.copyProperties(list, SubjectCollectBO.class);
    }

    //zhuangkaiqin
    @Override
    public List<SubjectCollectBO> areaCollect(SubjectCollectDTO subjectCollectDTO) throws SerException {
        String area = "";
        if (null != subjectCollectDTO.getArea() && subjectCollectDTO.getArea().length > 0) {
            area = subjectCollectDTO.getArea()[0];
        }
        String[] field = new String[]{"area", "months", "beginningDebitAmount",
                                        "beginningCreditAmount", "issueDebitAmount",
                                        "issueCreditAmount", "endDebitAmount", "endCreditAmount"};
        String sql = getSql("area", area, subjectCollectDTO);
        List<SubjectCollect> list = new ArrayList<>();
        //表头为地区/月份/期初借方余额/期初贷方余额/本期借方发生额/本期贷方发生额/期末借方余额/期末贷方月余额
        list = super.findBySql(sql, SubjectCollect.class, field);
        return BeanTransform.copyProperties(list, SubjectCollectBO.class);
    }

    @Override
    public List<SubjectCollectBO> groupCollect(SubjectCollectDTO subjectCollectDTO) throws SerException {
        String projectGroup = subjectCollectDTO.getProjectGroup();
        String[] field = new String[]{"projectGroup", "months", "beginningDebitAmount",
                                        "beginningCreditAmount", "issueDebitAmount", "issueCreditAmount",
                                        "endDebitAmount", "endCreditAmount"};
        String sql = getSql("projectGroup", projectGroup, subjectCollectDTO);
        List<SubjectCollect> list = new ArrayList<>();
        //表头为项目组/月份/期初借方余额/期初贷方余额/本期借方发生额/本期贷方发生额/期末借方余额/期末贷方月余额
        list = super.findBySql(sql.toString(), SubjectCollect.class, field);
        return BeanTransform.copyProperties(list, SubjectCollectBO.class);
    }

    @Override
    public List<SubjectCollectBO> pNameCollect(SubjectCollectDTO subjectCollectDTO) throws SerException {
        String projectName = subjectCollectDTO.getProjectName();
        String[] field = new String[]{"projectName", "months", "beginningDebitAmount",
                                        "beginningCreditAmount", "issueDebitAmount", "issueCreditAmount",
                                        "endDebitAmount", "endCreditAmount"};
        String sql = getSql("projectName", projectName, subjectCollectDTO);
        List<SubjectCollect> list = new ArrayList<>();
        //表头为项目名称/月份/期初借方余额/期初贷方余额/本期借方发生额/本期贷方发生额/期末借方余额/期末贷方余额
        list = super.findBySql(sql.toString(), SubjectCollect.class, field);
        return BeanTransform.copyProperties(list, SubjectCollectBO.class);
    }

    //zhuangkaiiqn
    private String getSql(String file, String fileValue, SubjectCollectDTO subjectCollectDTO) throws SerException {
        StringBuffer sql = new StringBuffer("");
        sql.append(" select " + file + ", months,");
        sql.append(" sum(beginningDebitAmount) as beginningDebitAmounts, ");
        sql.append(" sum(beginningCreditAmount) as beginningCreditAmounts, ");
        sql.append(" sum(issueDebitAmount) as issueDebitAmounts, ");
        sql.append(" sum(issueCreditAmount) as issueCreditAmounts, ");
        sql.append(" sum(endDebitAmount) as endDebitAmounts, ");
        sql.append(" sum(endCreditAmount) as endCreditAmounts ");
        sql.append(" from subjectcollect_subjectcollect ");
        if (StringUtils.isBlank(fileValue)) {
            sql.append(" where 1 = 1 ");
        } else {
            sql.append(" where " + file + " = '" + fileValue + "'");
        }
        if (StringUtils.isNotBlank(subjectCollectDTO.getStartTime()) && StringUtils.isNotBlank(subjectCollectDTO.getEndTime())) {
            sql.append(" and months between " + subjectCollectDTO.getStartTime() + " and " + subjectCollectDTO.getEndTime() + " ");
        }
        sql.append(" group by " + file + " ,months");
        return sql.toString();
    }
}