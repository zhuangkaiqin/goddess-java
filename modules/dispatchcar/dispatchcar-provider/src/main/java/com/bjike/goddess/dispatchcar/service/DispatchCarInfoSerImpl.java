package com.bjike.goddess.dispatchcar.service;

import com.bjike.goddess.assemble.api.ModuleAPI;
import com.bjike.goddess.businessproject.api.BaseInfoManageAPI;
import com.bjike.goddess.carinfo.api.DriverInfoAPI;
import com.bjike.goddess.carinfo.bo.DriverInfoBO;
import com.bjike.goddess.carinfo.dto.DriverInfoDTO;
import com.bjike.goddess.common.api.dto.Restrict;
import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.api.type.Status;
import com.bjike.goddess.common.jpa.service.ServiceImpl;
import com.bjike.goddess.common.provider.utils.RpcTransmit;
import com.bjike.goddess.common.utils.bean.BeanTransform;
import com.bjike.goddess.common.utils.date.DateUtil;
import com.bjike.goddess.common.utils.excel.Excel;
import com.bjike.goddess.common.utils.excel.ExcelUtil;
import com.bjike.goddess.dispatchcar.bean.AuditResult;
import com.bjike.goddess.dispatchcar.bean.DispatchInfo;
import com.bjike.goddess.dispatchcar.bo.*;
import com.bjike.goddess.dispatchcar.dto.CollectDispatchcarDTO;
import com.bjike.goddess.dispatchcar.dto.DispatchCarInfoDTO;
import com.bjike.goddess.dispatchcar.dto.LeaseCarCostDTO;
import com.bjike.goddess.dispatchcar.entity.CheckChangeCar;
import com.bjike.goddess.dispatchcar.entity.DispatchCarInfo;
import com.bjike.goddess.dispatchcar.entity.LeaseCarCost;
import com.bjike.goddess.dispatchcar.enums.*;
import com.bjike.goddess.dispatchcar.excel.DispatchCarInfoSetExcel;
import com.bjike.goddess.dispatchcar.excel.DispatchcarInfoCollectSetExcel;
import com.bjike.goddess.dispatchcar.excel.SonPermissionObject;
import com.bjike.goddess.dispatchcar.to.*;
import com.bjike.goddess.message.api.MessageAPI;
import com.bjike.goddess.message.enums.SendType;
import com.bjike.goddess.message.to.MessageTO;
import com.bjike.goddess.oilcardmanage.api.OilCardBasicAPI;
import com.bjike.goddess.oilcardmanage.bo.OilCardBasicBO;
import com.bjike.goddess.organize.api.PositionDetailAPI;
import com.bjike.goddess.organize.api.PositionDetailUserAPI;
import com.bjike.goddess.organize.bo.PositionDetailBO;
import com.bjike.goddess.staffentry.api.StaffEntryRegisterAPI;
import com.bjike.goddess.staffentry.bo.StaffEntryRegisterBO;
import com.bjike.goddess.staffentry.dto.StaffEntryRegisterDTO;
import com.bjike.goddess.user.api.UserAPI;
import com.bjike.goddess.user.api.UserDetailAPI;
import com.bjike.goddess.user.bo.UserBO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

//import com.bjike.goddess.carinfo.api.DriverAPI;

/**
 * 出车记录业务实现
 *
 * @Author: [ Jason ]
 * @Date: [ 2017-04-12 05:26 ]
 * @Description: [ 出车记录业务实现 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@CacheConfig(cacheNames = "dispatchcarSerCache")
@Service
public class DispatchCarInfoSerImpl extends ServiceImpl<DispatchCarInfo, DispatchCarInfoDTO> implements DispatchCarInfoSer {

    @Autowired
    private UserAPI userAPI;
    @Autowired
    private UserDetailAPI userDetailAPI;
    @Autowired
    private LeaseCarCostSer leaseCarCostSer;
    @Autowired
    private CusPermissionSer cusPermissionSer;
    @Autowired
    private OilCardBasicAPI oilCardBasicAPI;
    @Autowired
    private MessageAPI messageAPI;

    @Autowired
    private DriverInfoAPI driverInfoAPI;

    @Autowired
    private StaffEntryRegisterAPI staffEntryRegisterAPI;

    @Autowired
    private PositionDetailAPI positionDetailAPI;

    @Autowired
    private ModuleAPI moduleAPI;

    @Autowired
    private PositionDetailUserAPI positionDetailUserAPI;

    @Autowired
    private ServerCarInfoSer carInfoSer;

    @Autowired
    private BaseInfoManageAPI baseInfoManageAPI;

    @Autowired
    private CheckChangeCarSer checkChangeCarSer;


    @Override
    @Transactional(rollbackFor = SerException.class)
    public DispatchCarInfoBO insertModel(DispatchCarInfoTO to) throws SerException {
        RpcTransmit.getUserToken();
        UserBO userBO = userAPI.findByUsername(to.getCarUser());
        if (userBO != null) {
            to.setUserNumber(userBO.getEmployeeNumber());
        } else {
            throw new SerException("公司不存在该员工");
        }
        //加油费 = 加油量 * 当天油价 ，加油量 = 总油耗 * 总里程数 ， 总油耗 = 本车耗油 + 是否开空调 + 是否市内
        DriverInfoBO driver = driverInfoAPI.findByDriver(to.getDriver());
        if (driver == null) {
            throw new SerException("司机不存在!");
        }
        Double oilWear = driver.getCarFuel();
        if (to.getAircondition()) {
            oilWear = oilWear + 0.01;
        }
        if (to.getDowntown()) {
            oilWear = oilWear + 0.01;
        }


        DispatchCarInfo model = BeanTransform.copyProperties(to, DispatchCarInfo.class, true);
        if (to.getAcctype() == Acctype.MARKET) {
            List<PositionDetailBO> positionDetailUserBOS = positionDetailUserAPI.findPositionByUser(userBO.getId());
            if (!positionDetailUserBOS.get(0).getPosition().equals("项目经理")) {
                throw new SerException("只有项目经理才能制定市场费");
            }
        }

        //判断是否为本公司人员出车
        if (to.getAcctype() == Acctype.MARKET) {
            model.setCompanyDispatch(false);
        } else {
            model.setCompanyDispatch(true);
        }
        model.setMileageSubtract(model.getEndMileage() - model.getStartMileage());
        //计算餐补、加班费，满8小时，并有4小时为22点后，则给予餐费补贴30元,超过8个小时后的加班费 = 租车费 / 8 * 小时数
        //实际上班时长
        Long workHours = ChronoUnit.HOURS.between(model.getStartTime(), model.getEndTime());
        if (workHours > 8) {
            //当天22点
            LocalDateTime dateTime = LocalDateTime.of(model.getStartTime().toLocalDate(), LocalTime.of(22, 0));
            //出车开始时间+4 > 22:00 则补贴30元
            if (model.getStartTime().plusHours(4).isAfter(dateTime)) {
                model.setMealCost(30.0);
            } else {
                model.setMealCost(0.0);
            }
            if (!model.getSiesta()) {
                model.setOverWorkTime((double) (workHours - 8));
            } else {
                model.setOverWorkTime((double) (workHours - 8 - 1));
            }
        } else {
            model.setMealCost(0.0);
            model.setOverWorkTime(0.0);
        }
        //查找租车费用
        model.setCarRentalCost(findCost(to));
//        model.setOilWear(oilWear);
        model.setMileageSubtract(to.getEndMileage() - to.getStartMileage());

        //设置应加油量
        model.setShouldAmount(model.getMileageSubtract() * oilWear);
        //设置应加油费
        model.setShouldAmountMoney(model.getShouldAmount() * to.getOilPrice());
        //设置实加油费
        model.setAddOilAmountMoney(model.getAddOilAmount() * to.getOilPrice());
        //设置补充加油量和欠加油量
        if (model.getAddOilAmount() - model.getShouldAmount() > 0) {
            model.setLessOil(0.0);
            model.setLessOilFee(0.0);
            model.setSupplementOil(model.getAddOilAmount() - model.getShouldAmount());
            model.setSupplementFee(model.getSupplementOil() * to.getOilPrice());
        } else if (model.getAddOilAmount() - model.getShouldAmount() < 0) {
            model.setLessOil(model.getShouldAmount() - model.getAddOilAmount());
            model.setLessOilFee(model.getLessOil() * to.getOilPrice());
            model.setSupplementOil(0.0);
            model.setSupplementFee(0.0);
        } else {
            model.setLessOil(0.0);
            model.setLessOilFee(0.0);
            model.setSupplementOil(0.0);
            model.setSupplementFee(0.0);
        }

//        model.setAddOilAmount(model.getMileageSubtract() * oilWear);
//        if (to.getOilPrice() != null) {
//            model.setOilCost(model.getAddOilAmount() * to.getOilPrice());
//        } else {
//            model.setOilCost(0.0);
//        }

        //查询油卡余额
        model.setOilCardBalance(oilCardBasicAPI.findByCode(to.getOilCardNumber()).getBalance());
//        model.setOverWorkCost(model.getCarRentalCost() / 8 * model.getOverWorkTime());
//        model.setCost(model.getMealCost() + model.getCarRentalCost() + model.getOverWorkCost() + model.getParkCost() + model.getRoadCost());
//        model.setTotalCost(model.getMealCost() + model.getCarRentalCost() + model.getOverWorkCost() + model.getParkCost() + model.getRoadCost() + model.getOilCost());
        model.setFindType(FindType.WAITAUDIT);
        //设置出车单号----IKE20170101-1...

        setNumber(model);
        super.save(model);
        return BeanTransform.copyProperties(model, DispatchCarInfoBO.class);
    }

    @Override
    @Transactional(rollbackFor = SerException.class)
    public DispatchCarInfoBO updateModel(DispatchCarInfoTO to) throws SerException {
        if (to.getId() != null) {
            DispatchCarInfo model = super.findById(to.getId());
            if (model != null) {
                UserBO userBO = userAPI.findByUsername(to.getCarUser());
                to.setUserNumber(userBO.getEmployeeNumber());
                //加油费 = 加油量 * 当天油价 ，加油量 = 总油耗 * 总里程数 ， 总油耗 = 本车耗油 + 是否开空调 + 是否市内
                DriverInfoBO driver = driverInfoAPI.findByDriver(to.getDriver());
                if (driver == null) {
                    throw new SerException("司机不存在!");
                }
                Double oilWear = driver.getCarFuel();
                if (to.getAircondition()) {
                    oilWear = oilWear + 0.01;
                }
                if (to.getDowntown()) {
                    oilWear = oilWear + 0.01;
                }
//                model.setOilWear(oilWear);
                model.setMileageSubtract(to.getEndMileage() - to.getStartMileage());
                //设置应加油量
                model.setShouldAmount(model.getMileageSubtract() * oilWear);
                //设置应加油费
                model.setShouldAmountMoney(model.getShouldAmount() * to.getOilPrice());
                //设置实加油费
                model.setAddOilAmountMoney(model.getAddOilAmount() * to.getOilPrice());
                //设置补充加油量和欠加油量
                if (model.getAddOilAmount() - model.getShouldAmount() > 0) {
                    model.setLessOil(0.0);
                    model.setLessOilFee(0.0);
                    model.setSupplementOil(model.getAddOilAmount() - model.getShouldAmount());
                    model.setSupplementFee(model.getSupplementOil() * to.getOilPrice());
                } else if (model.getAddOilAmount() - model.getShouldAmount() < 0) {
                    model.setLessOil(model.getShouldAmount() - model.getAddOilAmount());
                    model.setLessOilFee(model.getLessOil() * to.getOilPrice());
                    model.setSupplementOil(0.0);
                    model.setSupplementFee(0.0);
                } else {
                    model.setLessOil(0.0);
                    model.setLessOilFee(0.0);
                    model.setSupplementOil(0.0);
                    model.setSupplementFee(0.0);
                }
//                model.setAddOilAmount(model.getMileageSubtract() * oilWear);
//                if (to.getOilPrice() != null) {
//                    model.setOilCost(model.getAddOilAmount() * to.getOilPrice());
//                } else {
//                    model.setOilCost(0.0);
//                }

                BeanTransform.copyProperties(to, model, true);

                model.setMileageSubtract(model.getEndMileage() - model.getStartMileage());
                //计算餐补、加班费，满8小时，并有4小时为22点后，则给予餐费补贴30元,超过8个小时后的加班费 = 租车费 / 8 * 小时数
                //实际上班时长
                Long workHours = ChronoUnit.HOURS.between(model.getStartTime(), model.getEndTime());
                if (workHours > 8) {
                    //当天22点
                    LocalDateTime dateTime = LocalDateTime.of(model.getStartTime().toLocalDate(), LocalTime.of(22, 0));
                    //出车开始时间+4 > 22:00 则补贴30元
                    if (model.getStartTime().plusHours(4).isAfter(dateTime)) {
                        model.setMealCost(30.0);
                    } else {
                        model.setMealCost(0.0);
                    }
                    if (!model.getSiesta()) {
                        model.setOverWorkTime((double) (workHours - 8));
                    } else {
                        model.setOverWorkTime((double) (workHours - 8 - 1));
                    }
                } else {
                    model.setMealCost(0.0);
                    model.setOverWorkTime(0.0);
                }
                //查找租车费用
                model.setCarRentalCost(findCost(to));
                //查询油卡余额
                model.setOilCardBalance(oilCardBasicAPI.findByCode(to.getOilCardNumber()).getBalance());
//                model.setOverWorkCost(model.getCarRentalCost() / 8 * model.getOverWorkTime());
//                model.setCost(model.getMealCost() + model.getCarRentalCost() + model.getOverWorkCost() + model.getParkCost() + model.getRoadCost());
//                model.setTotalCost(model.getMealCost() + model.getCarRentalCost() + model.getOverWorkCost() + model.getParkCost() + model.getRoadCost() + model.getOilCost());

                model.setModifyTime(LocalDateTime.now());
                super.update(model);
            } else {
                throw new SerException("更新对象不能为空");
            }
        } else {
            throw new SerException("id不能为空");
        }
        return BeanTransform.copyProperties(to, DispatchCarInfoBO.class);
    }

    @Override
    @Transactional(rollbackFor = SerException.class)
    public void freeze(String id) throws SerException {
        DispatchCarInfo model = super.findById(id);
        if (model != null) {
            model.setModifyTime(LocalDateTime.now());
            model.setStatus(Status.CONGEAL);
            super.update(model);
        } else {
            throw new SerException("冻结对象不能为空!");
        }
    }

    @Override
    @Transactional(rollbackFor = SerException.class)
    public void breakFreeze(String id) throws SerException {
        DispatchCarInfo model = super.findById(id);
        if (model != null) {
            if (model.getStatus() != Status.THAW) {
                model.setModifyTime(LocalDateTime.now());
                model.setStatus(Status.THAW);
                super.update(model);
            } else {
                throw new SerException("该记录无需重复解冻");
            }
        } else {
            throw new SerException("非法Id,出车记录对象不能为空!");
        }
    }

    @Override
    @Transactional(rollbackFor = SerException.class)
    public List<DispatchCarInfoBO> pageList(DispatchCarInfoDTO dto) throws SerException {
        dto.getSorts().add("createTime=desc");
        if (!"".equals(dto.getCarUser()) && dto.getCarUser() != null) {
            dto.getConditions().add(Restrict.eq("carUser", dto.getCarUser()));
        }
        if (!"".equals(dto.getNumber()) && dto.getNumber() != null) {
            dto.getConditions().add(Restrict.eq("number", dto.getNumber()));
        }
        List<DispatchCarInfo> list = super.findByCis(dto);
        return BeanTransform.copyProperties(list, DispatchCarInfoBO.class);
    }

    @Override
    @Transactional(rollbackFor = SerException.class)
    public void fileUpload(DispatchCarInfoTO to) throws SerException {

    }

    @Override
    @Transactional(rollbackFor = SerException.class)
    public AuditDetailBO findAudit(String id) throws SerException {
        DispatchCarInfo model = super.findById(id);
        if (model == null) {
            throw new SerException("审核对象不存在!");
        }

        //因为model里面有个枚举所以不能直接转换成String,要单独拿出来.
        DispatchInfo info = new DispatchInfo();
        BeanUtils.copyProperties(model, info, "evaluatedriver");
        info.setEvaluatedriver(info.getEvaluatedriver());
        AuditDetailBO returnBO = new AuditDetailBO();
        returnBO.setInfo(info);

        List<AuditResult> list = new ArrayList<AuditResult>();
        if (model != null) {
            DateUtil dateUtil = new DateUtil();
            //查询资金模块负责人核对结果
            if (!StringUtils.isEmpty(model.getMoneyModuleIdea())) {
                AuditResult bo = new AuditResult();
                bo.setAuditUser(model.getMoneyModule());
                bo.setPosition("资金模块负责人");
                bo.setSuggestion(model.getMoneyModuleIdea());
                bo.setAuditTime(dateUtil.dateToString(model.getMoneyDate()));
                list.add(bo);
            }
            //预算模块负责人核对结果
            if (!StringUtils.isEmpty(model.getBudgetModuleIdea())) {
                AuditResult bo = new AuditResult();
                bo.setAuditUser(model.getBudgetAuditUser());
                bo.setPosition("预算模块负责人");
                bo.setSuggestion(model.getBudgetModuleIdea());
                bo.setAuditTime(dateUtil.dateToString(model.getBudgetAuditTime()));
                list.add(bo);
            }
            //项目负责人/任务下发人审核结果
            if (!StringUtils.isEmpty(model.getProjectChargeIdea())) {
                AuditResult bo = new AuditResult();
                bo.setAuditUser(model.getPrincipal());
                bo.setPosition("项目模块负责人");
                bo.setSuggestion(model.getProjectChargeIdea());
                bo.setAuditResult(model.getIfPass());
                bo.setAuditTime(dateUtil.dateToString(model.getPrincipalAuditTime()));
                list.add(bo);
            }
            //客户模块负责人核对结果
            if (!StringUtils.isEmpty(model.getClientModuleIdea())) {
                AuditResult bo = new AuditResult();
                bo.setAuditUser(model.getClientModule());
                bo.setPosition("客户模块负责人");
                bo.setSuggestion(model.getClientModuleIdea());
                bo.setAuditTime(dateUtil.dateToString(model.getClientDate()));
                list.add(bo);
            }

            //素养模块负责人核对结果
            if (!StringUtils.isEmpty(model.getHeadModuleIdea())) {
                AuditResult bo = new AuditResult();
                bo.setAuditUser(model.getHeadModule());
                bo.setPosition("素养模块负责人");
                bo.setSuggestion(model.getHeadModuleIdea());
                bo.setAuditTime(dateUtil.dateToString(model.getHeadDate()));
                list.add(bo);
            }

            //财务模块负责人核对结果
            if (!StringUtils.isEmpty(model.getAccountModuleIdea())) {
                AuditResult bo = new AuditResult();
                bo.setAuditUser(model.getAccountModule());
                bo.setPosition("财务模块负责人");
                bo.setSuggestion(model.getAccountModuleIdea());
                bo.setAuditTime(dateUtil.dateToString(model.getAccountDate()));
                list.add(bo);
            }
            returnBO.setList(list);
            return returnBO;
        }
        return returnBO;
    }

    /**
     * 查询审核结果
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor = SerException.class)
    public List<AuditResultBO> findAuditResults(String id) throws SerException {
        DispatchCarInfo model = super.findById(id);
        if (model == null) {
            throw new SerException("审核对象不存在!");
        }

        List<AuditResultBO> list = new ArrayList<AuditResultBO>();
        if (model != null) {
            DateUtil dateUtil = new DateUtil();
            //查询资金审核结果
            if (!StringUtils.isEmpty(model.getMoneyModuleIdea()) && model.getMoneyDate() != null) {
                AuditResultBO bo = new AuditResultBO();
                bo.setAuditUser(model.getMoneyModule());
                bo.setPosition("资金模块负责人");
                bo.setSuggestion(model.getHeadModuleIdea());
                bo.setAuditTime(dateUtil.dateToString(model.getMoneyDate()));
                list.add(bo);
            }
            //预算审核结果
            if (!StringUtils.isEmpty(model.getBudgetModuleIdea()) && model.getBudgetAuditTime() != null) {
                AuditResultBO bo = new AuditResultBO();
                bo.setAuditUser(model.getBudgetAuditUser());
                bo.setPosition("预算模块负责人");
                bo.setSuggestion(model.getBudgetModuleIdea());
                bo.setAuditTime(dateUtil.dateToString(model.getBudgetAuditTime()));
                list.add(bo);
            }
            //项目负责人/任务下发人审核结果
            if (!StringUtils.isEmpty(model.getProjectChargeIdea()) && model.getPrincipalAuditTime() != null) {
                AuditResultBO bo = new AuditResultBO();
                bo.setAuditUser(model.getPrincipal());
                bo.setPosition("项目模块负责人");
                bo.setSuggestion(model.getProjectChargeIdea());
                bo.setAuditResult(model.getIfPass());
                bo.setAuditTime(dateUtil.dateToString(model.getPrincipalAuditTime()));
                list.add(bo);
            }
            //客户模块负责人核对结果
            if (!StringUtils.isEmpty(model.getClientModuleIdea())) {
                AuditResult bo = new AuditResult();
                bo.setAuditUser(model.getClientModule());
                bo.setPosition("客户模块负责人");
                bo.setSuggestion(model.getClientModuleIdea());
                bo.setAuditTime(dateUtil.dateToString(model.getClientDate()));
            }

            //素养模块负责人核对结果
            if (!StringUtils.isEmpty(model.getHeadModuleIdea())) {
                AuditResult bo = new AuditResult();
                bo.setAuditUser(model.getHeadModule());
                bo.setPosition("素养模块负责人");
                bo.setSuggestion(model.getHeadModuleIdea());
                bo.setAuditTime(dateUtil.dateToString(model.getHeadDate()));
            }

        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = SerException.class)
    public void fundSugg(DispatchCarInfoTO dispatchCarInfoTO, PredictPayTO to) throws SerException {
        UserBO userBO = userAPI.currentUser();
        List<PositionDetailBO> positionDetailBOS = positionDetailUserAPI.findPositionByUser(userBO.getId());
        if (positionDetailBOS != null) {
            if (positionDetailBOS.get(0).getPosition().equals("资金模块负责人")) {
                DispatchCarInfo model = super.findById(to.getId());
                if (model != null) {
                    model.setMoneyModuleIdea(to.getMoneyModuleIdea());
                    model.setMoneyModule(userBO.getUsername());
                    model.setMoneyDate(LocalDateTime.now());
                    model.setExpectPayDate(DateUtil.parseDate(to.getExpectPayDate()));
                    model.setPaymentSchedule(to.getPaymentSchedule());
                    //如果核对时问题描述或者问题类型不为空则数据存入到出车核对修改记录
                    filter(dispatchCarInfoTO, model, checkChangeCar(to, model, userBO));
                    BeanTransform.copyProperties(to, model, "modifyTime", "createTime", "carSource", "companyDispatch", "addOilExplain", "supplementOil", "supplementFee", "oweOilExplain", "lessOil", "lessOilFee", "shouldAmount", "shouldAmountMoney", "addOilAmountMoney");
                    super.update(model);
                } else {
                    throw new SerException("核对对象不能为空!");
                }
            } else {
                throw new SerException("资金模块负责人方可核对记录!");
            }
        } else {
            throw new SerException("获取不到当前登录用户的职位信息!");
        }
    }

    @Override
    @Transactional(rollbackFor = SerException.class)
    public void budgetSugg(DispatchCarInfoTO dispatchCarInfoTO, CheckChangeCarTO to) throws SerException {
        UserBO userBO = userAPI.currentUser();
        List<PositionDetailBO> positionDetailBOS = positionDetailUserAPI.findPositionByUser(userBO.getId());
        if (positionDetailBOS != null && positionDetailBOS.size() > 0) {
            if (positionDetailBOS.get(0).getPosition().equals("预算模块负责人")) {
                DispatchCarInfo model = super.findById(to.getId());
                if (model != null) {
                    model.setBudgetModuleIdea(to.getAuditSugg());
                    model.setBudgetAuditUser(userBO.getUsername());
                    model.setBudgetAuditTime(LocalDateTime.now());
                    //todo 核对修改记录
                    //如果核对时问题描述或者问题类型不为空则数据存入到出车核对修改记录
                    filter(dispatchCarInfoTO, model, checkChangeCar(to, model, userBO));
                    BeanTransform.copyProperties(to, model, "modifyTime", "createTime", "carSource", "companyDispatch", "addOilExplain", "supplementOil", "supplementFee", "oweOilExplain", "lessOil", "lessOilFee", "shouldAmount", "shouldAmountMoney", "addOilAmountMoney");
                    super.update(model);
                } else {
                    throw new SerException("核对对象不能为空!");
                }
            } else {
                throw new SerException("预算模块负责人方可核对记录!");
            }
        } else {
            throw new SerException("无法获取当前登录用户的职位信息!");
        }
    }

    private void filter(Object o1, Object o2, CheckChangeCar checkChangeCar) throws SerException {    //o1 修改前的数据 o2 修改后的数据
        Field[] field = o1.getClass().getDeclaredFields();//获取实体类的所有属性，返回field数组
        for (int i = 0; i < field.length; i++) {//遍历所有的属性
            String name = field[i].getName(); // 获取属性的名字
            name = name.substring(0, 1).toUpperCase() + name.substring(1);// 将属性的首字符大写，方便构造get，set方法
            try {
                Method m1 = o1.getClass().getMethod("get" + name);
                Object value1 = m1.invoke(o1);// 调用getter方法获取属性值
                Method m2 = o2.getClass().getMethod("get" + name);
                Object value2 = m2.invoke(o2);
                if (null != value1) {
                    if (value1 != value2) {
                        String filed = field[i].getName();    //修改的字段名
                        String value = value2.toString();  //修改后的属性值

                        //要获取注解中文名的类名
                        Class clazz = DispatchCarInfo.class;
                        try {
                            try {
                                Field field2 = clazz.getDeclaredField(filed);
                                Column col = field2.getAnnotation(Column.class);
                                System.out.println(col.columnDefinition());
                                String dispatchcar = col.columnDefinition();
                                dispatchcar = dispatchcar.substring(dispatchcar.indexOf("COMMENT"), dispatchcar.lastIndexOf("'") + 1);
                                dispatchcar = dispatchcar.substring(dispatchcar.indexOf("'") + 1, dispatchcar.lastIndexOf("'"));
                                System.out.println(dispatchcar);
                                String content = dispatchcar + value1 + ": 修改为 " + value2;
                                checkChangeCar.setContent(content);
                                checkChangeCarSer.save(checkChangeCar);
                            } catch (NoSuchFieldException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        } catch (SecurityException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (null != value2) {
                        String filed = field[i].getName();    //修改的字段名
                        String value = value2.toString();  //修改后的属性值


                        //要获取注解中文名的类名
                        Class clazz = DispatchCarInfo.class;
                        try {
                            try {
                                Field field2 = clazz.getDeclaredField(filed);
                                Column col = field2.getAnnotation(Column.class);
                                System.out.println(col.columnDefinition());
                                String dispatchcar = col.columnDefinition();
                                dispatchcar = dispatchcar.substring(dispatchcar.indexOf("COMMENT"), dispatchcar.lastIndexOf("'") + 1);
                                dispatchcar = dispatchcar.substring(dispatchcar.indexOf("'") + 1, dispatchcar.lastIndexOf("'"));
                                System.out.println(dispatchcar);
                                String content = dispatchcar + value1 + ": 修改为 " + value2;
                                checkChangeCar.setContent(content);
                                checkChangeCarSer.save(checkChangeCar);
                            } catch (NoSuchFieldException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        } catch (SecurityException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {

            }
        }
    }


    public static <T> Map<String, String> compare(T obj1, T Obj2)
            throws Exception {

        Map<String, String> result = new HashMap<String, String>();

        Field[] fs = obj1.getClass().getDeclaredFields();
        for (Field f : fs) {
            f.setAccessible(true);
            Object v1 = f.get(obj1);
            Object v2 = f.get(Obj2);
            if (!equals(v1, v2)) {
                result.put(f.getName(), String.valueOf(equals(v1, v2)));

            }
        }
        return result;
    }

    public static boolean equals(Object obj1, Object obj2) {

        if (obj1 == obj2) {
            return true;
        }
        if (obj1 == null || obj2 == null) {
            return false;
        }
        return obj1.equals(obj2);
    }


    @Override
    public void clientSugg(CheckChangeCarTO to) throws SerException {
        UserBO userBO = userAPI.currentUser();
        List<PositionDetailBO> positionDetailBOS = positionDetailUserAPI.findPositionByUser(userBO.getId());
        if (positionDetailBOS != null && positionDetailBOS.size() > 0) {
            if (positionDetailBOS.get(0).getPosition().equals("客户模块负责人")) {
                DispatchCarInfo model = super.findById(to.getId());
                if (model != null) {
                    if (model.getAcctype() == Acctype.MARKET) {
                        model.setClientModuleIdea(to.getAuditSugg());
                        model.setClientModule(userBO.getUsername());
                        model.setClientDate(LocalDateTime.now());
                        //如果核对问题时冻结了数据，则把数据的查询类型设置为有误
                        if (to.getIfFreeze() == true) {
                            model.setFindType(FindType.WRONG);
                            model.setStatus(Status.CONGEAL);
                        }
                        super.update(model);
                    } else {
                        throw new SerException("科目类型不为市场费，无法审核!");
                    }
                } else {
                    throw new SerException("核对对象不能为空");
                }
            } else {
                throw new SerException("客户模块负责人方可核对记录!");
            }
        } else {
            throw new SerException("无法获取当前登录用户的职位信息!");
        }
    }

    @Override
    public void headSugg(CheckChangeCarTO to) throws SerException {
        UserBO userBO = userAPI.currentUser();
        List<PositionDetailBO> positionDetailBOS = positionDetailUserAPI.findPositionByUser(userBO.getId());
        if (positionDetailBOS != null && positionDetailBOS.size() > 0) {
            if (positionDetailBOS.get(0).getPosition().equals("素养模块负责人")) {
                DispatchCarInfo model = super.findById(to.getId());
                if (model != null) {
                    if (model.getAcctype() == Acctype.TRAINING) {
                        model.setHeadModuleIdea(to.getAuditSugg());
                        model.setHeadModule(userBO.getUsername());
                        model.setHeadDate(LocalDateTime.now());
                        //如果核对问题时冻结了数据，则把数据的查询类型设置为有误
                        if (to.getIfFreeze() == true) {
                            model.setFindType(FindType.WRONG);
                            model.setStatus(Status.CONGEAL);
                        }
                        super.update(model);
                    } else {

                        throw new SerException("科目类型不为培训费，无法审核!");
                    }
                } else {
                    throw new SerException("核对对象不能为空");
                }
            } else {
                throw new SerException("素养模块负责人方可核对记录!");
            }
        } else {
            throw new SerException("无法获取当前登录用户的职位信息!");
        }
    }

    @Override
    public void receivePaper(String id, Boolean isCorrect) throws SerException {
        UserBO userBO = userAPI.currentUser();
        List<PositionDetailBO> positionDetailBOS = positionDetailUserAPI.findPositionByUser(userBO.getId());
        if (positionDetailBOS != null && positionDetailBOS.size() > 0) {
            DispatchCarInfo model = super.findById(id);
            if (model != null) {
                model.setCorrect(isCorrect);
                super.update(model);
            } else {
                throw new SerException("核对对象不能为空");
            }
        } else {
            throw new SerException("无法获取当前登录用户的职位信息");
        }
    }

    @Override
    public void mail(MailTO to) throws SerException {
        UserBO userBO = userAPI.currentUser();
        DispatchCarInfo model = super.findById(to.getId());
        model.setSender(userBO.getUsername());
        model.setSendDate(LocalDate.now());
        model.setIfSendArchiveAl(to.getIfSendArchiveAl());
        model.setIfSendReimbursementAl(to.getIfSendReimbursementAl());
        model.setTotalParking(to.getTotalParking());
        model.setTotalReceipts(to.getTatalReceipts());
        model.setIfSendAddOilReceipts(to.getIfSendAddOilReceipts());
        super.update(model);
    }

    @Override
    public void financialSugg(DispatchCarInfoTO dispatchCarInfoTO, CheckChangeCarTO to) throws SerException {
        UserBO userBO = userAPI.currentUser();
        List<PositionDetailBO> positionDetailBOS = positionDetailUserAPI.findPositionByUser(userBO.getId());
        if (positionDetailBOS != null && positionDetailBOS.size() > 0) {
            if (positionDetailBOS.get(0).getPosition().equals("财务模块负责人")) {
                DispatchCarInfo model = super.findById(to.getId());
                if (model != null) {
                    if (model.getCorrect() == true) {
                        model.setAccountModuleIdea(to.getAuditSugg());
                        model.setAccountModule(userBO.getUsername());
                        model.setAccountDate(LocalDateTime.now());
                        //如果核对时问题描述或者问题类型不为空则数据存入到出车核对修改记录
                        filter(dispatchCarInfoTO, model, checkChangeCar(to, model, userBO));
                        BeanTransform.copyProperties(to, model, "modifyTime", "createTime", "carSource", "companyDispatch", "addOilExplain", "supplementOil", "supplementFee", "oweOilExplain", "lessOil", "lessOilFee", "shouldAmount", "shouldAmountMoney", "addOilAmountMoney");
                        super.update(model);
                    } else {
                        throw new SerException("必须收到票据核对无误后方可审核");
                    }
                } else {
                    throw new SerException("核对对象不能为空");
                }
            } else {
                throw new SerException("财务模块负责人方可核对记录!");
            }
        } else {
            throw new SerException("无法获取当前登录用户的职位信息!");
        }
    }

    private CheckChangeCar checkChangeCar(CheckChangeCarTO to, DispatchCarInfo model, UserBO userBO) throws SerException {
        CheckChangeCar checkChangeCar = new CheckChangeCar();
        if (to.getProblemDes() != null || to.getProblemType() != null) {
            checkChangeCar.setCarUser(model.getCarUser());
            checkChangeCar.setDispatchDate(model.getDispatchDate());
            checkChangeCar.setModifier(userBO.getUsername());
            checkChangeCar.setModifyDate(LocalDate.now());
            checkChangeCar.setNumber(model.getNumber());
            if (to.getProblemDes() != null) {
                checkChangeCar.setProblemDes(to.getProblemDes());
            }
            if (to.getProblemType() != null) {
                checkChangeCar.setProblemType(to.getProblemType());
            }
        }
        return checkChangeCar;
    }

    private CheckChangeCar checkChangeCar(PredictPayTO to, DispatchCarInfo model, UserBO userBO) throws SerException {
        CheckChangeCar checkChangeCar = new CheckChangeCar();
        if (to.getProblemDes() != null || to.getProblemType() != null) {
            checkChangeCar.setCarUser(model.getCarUser());
            checkChangeCar.setDispatchDate(model.getDispatchDate());
            checkChangeCar.setModifier(userBO.getUsername());
            checkChangeCar.setModifyDate(LocalDate.now());
            checkChangeCar.setNumber(model.getNumber());
            if (to.getProblemDes() != null) {
                checkChangeCar.setProblemDes(to.getProblemDes());
            }
            if (to.getProblemType() != null) {
                checkChangeCar.setProblemType(to.getProblemType());
            }
        }
        return checkChangeCar;
    }

    @Override
    @Transactional(rollbackFor = SerException.class)
    public void principalSugg(String id, String principalSugg, Boolean auditResult) throws SerException {
        UserBO userBO = userAPI.currentUser();
        if (userBO != null) {
            DispatchCarInfo model = super.findById(id);
            if (model != null) {
                if (!model.getPrincipal().equals(userBO.getUsername())) {
                    throw new SerException("项目负责人或任务下发人方可审核");
                }
                model.setProjectChargeIdea(principalSugg);
                model.setIfPass(auditResult);
                model.setPrincipalAuditTime(LocalDateTime.now());
                if (auditResult == false) {
                    model.setFindType(FindType.WRONG);
                }
                super.update(model);
            } else {
                throw new SerException("核对对象不能为空!");
            }
        }
    }


//    @Override
//    @Transactional(rollbackFor = SerException.class)
//    public void receiptAudit(String id, String auditReceiptSugg, String receiveReceiptDate, Boolean auditReceiptResult) throws SerException {
//        DispatchCarInfo model = super.findById(id);
//        if (model != null) {
//            model.setAuditReceiptSugg(auditReceiptSugg);
//            model.setReceiveReceiptDate(DateUtil.parseDate(receiveReceiptDate));
//            model.setAuditResult(auditReceiptResult);
//            if (auditReceiptResult) {
//                model.setFindType(FindType.WAITPAY);
//            }
//            super.update(model);
//        } else {
//            throw new SerException("核对对象不能为空!");
//        }
//    }

    @Override
    @Transactional(rollbackFor = SerException.class)
    public void pay(String id) throws SerException {
        DispatchCarInfo model = super.findById(id);
        if (model != null) {
            if (FindType.PAYED == model.getFindType()) {
                throw new SerException("无需重复审核!");
            }
            model.setFindType(FindType.PAYED);
            model.setIfPass(true);
            //付款后代表所有审核均通过，修改油余额
            //TODO 这里应该考虑分布式事务，联系焕来或贵钦解决该问题。 TCC
            OilCardBasicBO basicBO = oilCardBasicAPI.findByCode(model.getOilCardNumber());
            OilCardBasicBO bo = oilCardBasicAPI.find(basicBO.getId());
            bo.setBalance(bo.getBalance() - model.getOilPrice());
//            OilCardBasic oilCardBasic = BeanTransform.copyProperties(bo,OilCardBasic.class,true);
            oilCardBasicAPI.updateOliCardBasic(bo);
            if (bo.getBalance() < 300) {
                String content = "运营商务部的同事，你们好，" + bo.getOilCardCode() + "号油卡余额" + bo.getBalance() + "元，低于300元，请在一天内充值，请综合资源部同事跟进充值情况";
                MessageTO to = new MessageTO("油卡余额不足300元", content);
                to.setSendType(SendType.EMAIL);
                //TODO 未明确发送对象
//                to.setReceivers(sendUsers);
                messageAPI.send(to);
            }
            super.update(model);
        } else {
            throw new SerException("付款对象不能为空!");
        }
    }

    @Override
    @Transactional(rollbackFor = SerException.class)
    public List<DispatchCollectBO> dispatchCollect(CollectIntervalType collectIntervalType, CollectType collectType) throws SerException {

        if (collectIntervalType == null) {
            throw new SerException("汇总类型不能为空!");
        }

        List<DispatchCarInfo> proejctList = null;
        if (collectType == CollectType.AREA) {
            //分组查询地区、项目组、项目
            proejctList = super.findBySql("select area , project_group , project ,1 from dispatchcar_basicinfo group by area , project_group , project ",
                    DispatchCarInfo.class, new String[]{"area", "group", "project"});
        } else {
            //分组查询地区、项目组、项目
            proejctList = super.findBySql("select driver , project_group , project ,1 from dispatchcar_basicinfo group by driver , project_group , project ",
                    DispatchCarInfo.class, new String[]{"area", "group", "project"});
        }

        List<DispatchCollectBO> returnList = new ArrayList<DispatchCollectBO>();

        //遍历项目组
        for (DispatchCarInfo proejct : proejctList) {
            DispatchCollectBO bo = new DispatchCollectBO();
            bo.setDriver(proejct.getDriver());
            bo.setArea(proejct.getArea());
            bo.setGroup(proejct.getGroup());
            bo.setProject(proejct.getProject());

            findByType(proejct.getArea(), proejct.getGroup(), proejct.getProject(), bo, collectIntervalType);

            returnList.add(bo);
        }

        return returnList;
    }

    @Override
    @Transactional(rollbackFor = SerException.class)
    public List<FinanceCollectBO> weekCollect(String startDate, String endDate) throws SerException {
        DispatchCarInfoDTO dto = new DispatchCarInfoDTO();
        LocalDate start = null;
        LocalDate end = null;
        //页面初始化时(即不填写查询时间)加载本周汇总记录
        if (StringUtils.isEmpty(startDate) && StringUtils.isEmpty(endDate)) {
            start = DateUtil.getStartWeek();
            end = DateUtil.getEndWeek();
        } else {
            if (!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
                start = DateUtil.parseDate(startDate);
                end = DateUtil.parseDate(endDate);
            } else {
                throw new SerException("请选择查询时间段!");
            }
        }
        LocalDate[] condition = new LocalDate[]{start, end};
        dto.getConditions().add(Restrict.between("dispatchDate", condition));

        return financeCollect(dto);
    }

    @Override
    @Transactional(rollbackFor = SerException.class)
    public FinanceCollectBO findCollectDetail(String id) throws SerException {
        DispatchCarInfo model = super.findById(id);
        FinanceCollectBO bo = new FinanceCollectBO();
        if (model != null) {
            bo.setId(model.getId());
            bo.setDate(DateUtil.dateToString(model.getDispatchDate()));
            bo.setNumber(model.getNumber());
            bo.setDriver(model.getDriver());
            bo.setArea(model.getArea());
            bo.setCarUser(model.getCarUser());
            bo.setProject(model.getProject());
            bo.setProjectGroup(model.getGroup());
            bo.setAcctype(model.getAcctype());
//            bo.setOilCost(model.getOilCost());
            bo.setOverWorkTime(model.getOverWorkTime());
//            bo.setOverWorkCost(model.getOverWorkCost());
            bo.setMealCost(model.getMealCost());
            bo.setParkCost(model.getParkCost());
            bo.setRoadCost(model.getParkCost());
        }
        return bo;
    }

    @Override
    @Transactional(rollbackFor = SerException.class)
    public void predict(PredictPayTO to) throws SerException {
        UserBO userBO = userAPI.currentUser();
        DispatchCarInfo model = super.findById(to.getId());
        if (model != null) {
            model.setExpectPayDate(DateUtil.parseDate(to.getExpectPayDate()));
            model.setPaymentSchedule(to.getPaymentSchedule());
            model.setMoneyModuleIdea(to.getMoneyModuleIdea());
            if (to.getProblemDes() != null || to.getProblemType() != null) {
                CheckChangeCarTO checkChangeCarTO = new CheckChangeCarTO();
                checkChangeCarTO.setAuditSugg(to.getMoneyModuleIdea());
                checkChangeCarTO.setProblemDes(to.getProblemDes());
                checkChangeCarTO.setProblemType(to.getProblemType());
                checkChangeCar(checkChangeCarTO, model, userBO);
            }
        } else {
            throw new SerException("编辑对象不能为空");
        }
    }

    @Override
    @Transactional(rollbackFor = SerException.class)
    public List<SonPermissionObject> financeSonPermission() throws SerException {
        List<SonPermissionObject> list = new ArrayList<>();

        Boolean flagAddSign = financeGuideSeeIdentity();
        SonPermissionObject obj = new SonPermissionObject();

        obj = new SonPermissionObject();
        obj.setName("finance");
        obj.setDescribesion("财务出车汇总");
        if (flagAddSign) {
            obj.setFlag(true);
        } else {
            obj.setFlag(false);
        }
        list.add(obj);

        return list;
    }

    /**
     * 出车管理导航栏核对查看权限（部门级别）
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
     * 出车管理导航栏核对删除添加编辑..审核权限（部门级别）
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


    /**
     * 财务出车管理导航栏核对查看权限（部门级别）
     */
    private Boolean financeGuideSeeIdentity() throws SerException {
        Boolean flag = false;
        String userToken = RpcTransmit.getUserToken();
        UserBO userBO = userAPI.currentUser();
        RpcTransmit.transmitUserToken(userToken);
        String userName = userBO.getUsername();
        if (!"admin".equals(userName.toLowerCase())) {
            flag = cusPermissionSer.getCusPermission("2");
        } else {
            flag = true;
        }
        return flag;
    }

    /**
     * 财务出车管理导航栏核对功能审核权限（部门级别）
     */
    private Boolean financeGuideAddIdentity() throws SerException {
        Boolean flag = false;
        String userToken = RpcTransmit.getUserToken();
        UserBO userBO = userAPI.currentUser();
        RpcTransmit.transmitUserToken(userToken);
        String userName = userBO.getUsername();
        if (!"admin".equals(userName.toLowerCase())) {
            flag = cusPermissionSer.getCusPermission("2");
        } else {
            flag = true;
        }
        return flag;
    }


    @Override
    @Transactional(rollbackFor = SerException.class)
    public Boolean financeGuidePermission(GuidePermissionTO to) throws SerException {
        String userToken = RpcTransmit.getUserToken();
        GuideAddrStatus guideAddrStatus = to.getGuideAddrStatus();
        Boolean flag = true;
        switch (guideAddrStatus) {
            case WEEK:
                flag = financeGuideAddIdentity();
                break;
            case MONTH:
                flag = financeGuideAddIdentity();
                break;
            case AREACOLLECT:
                flag = financeGuideAddIdentity();
                break;
            case GROUPCOLLECT:
                flag = financeGuideAddIdentity();
                break;
            case DRIVERCOLLECT:
                flag = financeGuideAddIdentity();
                break;
            case AREAANALYZE:
                flag = financeGuideAddIdentity();
                break;
            case groupAnalyze:
                flag = financeGuideAddIdentity();
                break;
            case DRIVERANALYZE:
                flag = financeGuideAddIdentity();
                break;
            case DETAIL:
                flag = financeGuideAddIdentity();
                break;
            default:
                flag = true;
                break;
        }
        return flag;
    }

    @Override
    @Transactional(rollbackFor = SerException.class)
    public List<FinanceCollectBO> monthCollect(Integer year, Integer month) throws SerException {
        DispatchCarInfoDTO dto = new DispatchCarInfoDTO();
        LocalDate start = null;
        LocalDate end = null;
        //页面初始化时(即不填写查询时间)加载本月汇总记录
        if (StringUtils.isEmpty(year) && StringUtils.isEmpty(month)) {
            start = DateUtil.getStartMonth();
            end = DateUtil.getEndMonth();
        } else {
            start = DateUtil.getStartDayOfMonth(year, month);
            end = DateUtil.getEndDaYOfMonth(year, month);
        }
        LocalDate[] condition = new LocalDate[]{start, end};
        dto.getConditions().add(Restrict.between("dispatchDate", condition));

        return financeCollect(dto);
    }

    @Override
    @Transactional(rollbackFor = SerException.class)
    public List<FinanceCollectBO> selectCollect(FinanceCollectTO to) throws SerException {
        DispatchCarInfoDTO dto = new DispatchCarInfoDTO();

        //年份、月份必填
        LocalDate start = DateUtil.getStartDayOfMonth(to.getYear(), to.getMonth());
        LocalDate end = DateUtil.getEndDaYOfMonth(to.getYear(), to.getMonth());
        LocalDate[] condition = new LocalDate[]{start, end};
        dto.getConditions().add(Restrict.between("dispatchDate", condition));

        if (to.getAcctype() != null) {
            dto.getConditions().add(Restrict.eq("acctype", to.getAcctype()));
        }
        if (!StringUtils.isEmpty(to.getArea())) {
            dto.getConditions().add(Restrict.eq("area", to.getArea()));
        }
        if (!StringUtils.isEmpty(to.getGroup())) {
            dto.getConditions().add(Restrict.eq("group", to.getGroup()));
        }
        if (!StringUtils.isEmpty(to.getProject())) {
            dto.getConditions().add(Restrict.eq("project", to.getProject()));
        }
        if (!StringUtils.isEmpty(to.getDriver())) {
            dto.getConditions().add(Restrict.eq("driver", to.getDriver()));
        }
        if (!StringUtils.isEmpty(to.getCarUser())) {
            dto.getConditions().add(Restrict.eq("carUser", to.getCarUser()));
        }

        return financeCollect(dto);
    }

    @Override
    @Transactional(rollbackFor = SerException.class)
    public List<FinanceAnalyzeBO> selectAnalyze(FinanceCollectTO to) throws SerException {
        DispatchCarInfoDTO currentDTO = new DispatchCarInfoDTO();
        DispatchCarInfoDTO lastDTO = new DispatchCarInfoDTO();
        DispatchCarInfoDTO totoalDTO = new DispatchCarInfoDTO();
        //年份、月份必填:指定月份及制定月份的上一月
        LocalDate start = DateUtil.getStartDayOfMonth(to.getYear(), to.getMonth());
        LocalDate end = DateUtil.getEndDaYOfMonth(to.getYear(), to.getMonth());
        LocalDate lastStart = DateUtil.getStartDayOfMonth(to.getYear(), to.getMonth() - 1);
        LocalDate lastEnd = DateUtil.getEndDaYOfMonth(to.getYear(), to.getMonth() - 1);

        LocalDate[] currentTime = new LocalDate[]{start, end};
        LocalDate[] lastTime = new LocalDate[]{lastStart, lastEnd};

        currentDTO.getConditions().add(Restrict.between("dispatchDate", currentTime));
        lastDTO.getConditions().add(Restrict.between("dispatchDate", lastTime));

        //总费用
        totoalDTO.getConditions().add(Restrict.between("dispatchDate", currentTime));

        if (to.getAcctype() != null) {
            currentDTO.getConditions().add(Restrict.eq("acctype", to.getAcctype()));
            lastDTO.getConditions().add(Restrict.eq("acctype", to.getAcctype()));
        }
        if (!StringUtils.isEmpty(to.getArea())) {
            currentDTO.getConditions().add(Restrict.eq("area", to.getArea()));
            lastDTO.getConditions().add(Restrict.eq("area", to.getArea()));
        }
        if (!StringUtils.isEmpty(to.getGroup())) {
            currentDTO.getConditions().add(Restrict.eq("group", to.getGroup()));
            lastDTO.getConditions().add(Restrict.eq("group", to.getGroup()));
        }
        if (!StringUtils.isEmpty(to.getProject())) {
            currentDTO.getConditions().add(Restrict.eq("project", to.getProject()));
            lastDTO.getConditions().add(Restrict.eq("project", to.getProject()));
        }
        if (!StringUtils.isEmpty(to.getDriver())) {
            currentDTO.getConditions().add(Restrict.eq("driver", to.getDriver()));
            lastDTO.getConditions().add(Restrict.eq("driver", to.getDriver()));
        }
        if (!StringUtils.isEmpty(to.getCarUser())) {
            currentDTO.getConditions().add(Restrict.eq("carUser", to.getCarUser()));
            lastDTO.getConditions().add(Restrict.eq("carUser", to.getCarUser()));
        }
        return financeAnalyze(currentDTO, lastDTO, totoalDTO, to);
    }

    public List<FinanceAnalyzeBO> financeAnalyze(DispatchCarInfoDTO currentDTO, DispatchCarInfoDTO lastDTO, DispatchCarInfoDTO totalDTO, FinanceCollectTO to) throws SerException {
        //查询月份
        List<DispatchCarInfo> currentList = super.findByCis(currentDTO);
        //查询上一月份
        List<DispatchCarInfo> lastList = super.findByCis(lastDTO);
        //查询全部记录
        List<DispatchCarInfo> totalList = super.findByCis(totalDTO);

        List<FinanceAnalyzeBO> boList = new ArrayList<FinanceAnalyzeBO>();

        FinanceAnalyzeBO bo = null;

        Double currentCost = currentList.stream().filter(p -> p.getCost() != null).mapToDouble(p -> p.getCost()).sum();
        Double lastCost = lastList.stream().filter(p -> p.getCost() != null).mapToDouble(p -> p.getCost()).sum();
        Double totalCost = totalList.stream().filter(p -> p.getCost() != null).mapToDouble(p -> p.getCost()).sum();
        Double costSubtract = currentCost - lastCost;
        Double growRate = 0.0;
        if (lastCost == 0) {
            growRate = null;
        } else {
            growRate = costSubtract / lastCost;
        }
        Double percent = 0.0;
        String percentStr = "";
        if (totalCost == 0) {
            percent = null;
            percentStr = null;
        } else {
            percent = currentCost / totalCost * 100;
            DecimalFormat format = new DecimalFormat("#.00");
            percentStr = format.format(percent) + "%";
        }

        //根据分析页面(条件)不同返回不同的分析结果
        if (!StringUtils.isEmpty(to.getArea())) {
            bo = new FinanceAnalyzeBO(to.getArea(), currentCost, lastCost, costSubtract, growRate, percentStr);
        } else if (!StringUtils.isEmpty(to.getGroup())) {
            bo = new FinanceAnalyzeBO(to.getGroup(), currentCost, lastCost, costSubtract, growRate, percentStr);
        } else if (!StringUtils.isEmpty(to.getDriver())) {
            bo = new FinanceAnalyzeBO(to.getDriver(), currentCost, lastCost, costSubtract, growRate, percentStr);
        } else {
            throw new SerException("请选择分析的地区或项目组或司机!");
        }
        boList.add(bo);
        return boList;
    }

    public List<FinanceCollectBO> financeCollect(DispatchCarInfoDTO dto) throws SerException {
        List<DispatchCarInfo> list = super.findByCis(dto);
        List<FinanceCollectBO> boList = new ArrayList<FinanceCollectBO>();

        for (DispatchCarInfo model : list) {
            FinanceCollectBO bo = new FinanceCollectBO();
            bo.setId(model.getId());
            bo.setDate(DateUtil.dateToString(model.getDispatchDate()));
            bo.setNumber(model.getNumber());
            bo.setDriver(model.getDriver());
            bo.setArea(model.getArea());
            bo.setCarUser(model.getCarUser());
            bo.setProject(model.getProject());
            bo.setProjectGroup(model.getGroup());
            bo.setAcctype(model.getAcctype());
//            bo.setOilCost(model.getOilCost());
            bo.setOverWorkTime(model.getOverWorkTime());
//            bo.setOverWorkCost(model.getOverWorkCost());
            bo.setMealCost(model.getMealCost());
            bo.setParkCost(model.getParkCost());
            bo.setRoadCost(model.getParkCost());
            boList.add(bo);
        }

        Double totalOilCost = 0.0;
        Double totalOverWorkTime = 0.0;
        Double totalOverWorkCost = 0.0;
        Double totalMealCost = 0.0;
        Double totalParkCost = 0.0;
        Double totalRoadCost = 0.0;

        if (list != null && !list.isEmpty()) {
//            totalOilCost = list.stream().filter(p -> p.getOilCost() != null).mapToDouble(p -> p.getOilCost()).sum();
            totalOverWorkTime = list.stream().filter(p -> p.getOverWorkTime() != null).mapToDouble(p -> p.getOverWorkTime()).sum();
//            totalOverWorkCost = list.stream().filter(p -> p.getOverWorkCost() != null).mapToDouble(p -> p.getOverWorkCost()).sum();
            totalMealCost = list.stream().filter(p -> p.getMealCost() != null).mapToDouble(p -> p.getMealCost()).sum();
            totalParkCost = list.stream().filter(p -> p.getParkCost() != null).mapToDouble(p -> p.getParkCost()).sum();
            totalRoadCost = list.stream().filter(p -> p.getRoadCost() != null).mapToDouble(p -> p.getRoadCost()).sum();

            FinanceCollectBO totalBO = new FinanceCollectBO("合计", "", "", "", "", "", "", null,
                    totalOverWorkTime,
                    totalMealCost, totalParkCost, totalRoadCost, "");
            boList.add(totalBO);
        } else {
            FinanceCollectBO totalBO = new FinanceCollectBO("合计", "", "", "", "", "", "", null,
                    totalOverWorkTime,
                    totalMealCost, totalParkCost, totalRoadCost, "");
            boList.add(totalBO);
        }
        return boList;
    }

    public void findByType(String area, String group, String project, DispatchCollectBO bo, CollectIntervalType collectIntervalType) throws SerException {
        //查询指定汇总类型的出车情况(昨日/上周/上月/上季度/上年度)
        DispatchCarInfoDTO currentDTO = new DispatchCarInfoDTO();
        currentDTO.getConditions().add(Restrict.eq("area", area));
        currentDTO.getConditions().add(Restrict.eq("group", group));
        currentDTO.getConditions().add(Restrict.eq("project", project));
        //查询指定汇总类型的出车情况(本日/本周/本月/本季度/本年度)
        DispatchCarInfoDTO lastDTO = new DispatchCarInfoDTO();
        lastDTO.getConditions().add(Restrict.eq("area", area));
        lastDTO.getConditions().add(Restrict.eq("group", group));
        lastDTO.getConditions().add(Restrict.eq("project", project));

        switch (collectIntervalType) {
            case DAY:
                setDayCondition(currentDTO, lastDTO, bo, CollectIntervalType.DAY);
                findByCondition(currentDTO, lastDTO, bo);
                break;
            case WEEK:
                setCondition(currentDTO, lastDTO, bo, CollectIntervalType.WEEK);
                findByCondition(currentDTO, lastDTO, bo);
                break;
            case MONTH:
                setCondition(currentDTO, lastDTO, bo, CollectIntervalType.MONTH);
                findByCondition(currentDTO, lastDTO, bo);
                break;
            case QUARTER:
                setCondition(currentDTO, lastDTO, bo, CollectIntervalType.QUARTER);
                findByCondition(currentDTO, lastDTO, bo);
                break;
            case YEAR:
                setCondition(currentDTO, lastDTO, bo, CollectIntervalType.YEAR);
                findByCondition(currentDTO, lastDTO, bo);
                break;
        }
    }

    //查询本天、昨天出车情况
    public void setDayCondition(DispatchCarInfoDTO currentDTO, DispatchCarInfoDTO lastDTO, DispatchCollectBO bo, CollectIntervalType collectIntervalType) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        currentDTO.getConditions().add(Restrict.like("dispatchDate", today.toString()));
        lastDTO.getConditions().add(Restrict.like("dispatchDate", yesterday.toString()));
    }

    //查询周、月、季、年
    public void setCondition(DispatchCarInfoDTO currentDTO, DispatchCarInfoDTO lastDTO, DispatchCollectBO bo, CollectIntervalType collectIntervalType) throws SerException {
        LocalDate currentStarDay = null;
        LocalDate currentEndDay = null;

        LocalDate lastStarDay = null;
        LocalDate lastEndDay = null;

        switch (collectIntervalType) {
            case WEEK:
                currentStarDay = DateUtil.getStartWeek();
                currentEndDay = DateUtil.getEndWeek();
                lastStarDay = currentStarDay.minusWeeks(1);
                lastEndDay = currentEndDay.minusWeeks(1);

                break;
            case MONTH:
                currentStarDay = DateUtil.getStartMonth();
                currentEndDay = DateUtil.getEndMonth();
                lastStarDay = currentStarDay.minusMonths(1);
                lastEndDay = currentEndDay.minusMonths(1);

                break;
            case QUARTER:
                Date date = new Date();
                date.getMonth();
                currentStarDay = DateUtil.getStartDayOfMonth(getQuarterStart(date.getMonth()));
                currentEndDay = DateUtil.getEndDaYOfMonth(getQuarterEnd(date.getMonth()));
                lastStarDay = currentStarDay.minusMonths(3);
                lastEndDay = currentEndDay.minusMonths(3);

                break;
            case YEAR:
                currentStarDay = DateUtil.getStartYear();
                currentEndDay = DateUtil.getEndYear();
                lastStarDay = currentStarDay.minusYears(1);
                lastEndDay = currentEndDay.minusYears(1);

                break;
        }

        LocalDate[] currentCondition = new LocalDate[]{currentStarDay, currentEndDay};
        LocalDate[] lastCondition = new LocalDate[]{lastStarDay, lastEndDay};

        currentDTO.getConditions().add(Restrict.between("dispatchDate", currentCondition));
        lastDTO.getConditions().add(Restrict.between("dispatchDate", lastCondition));
    }

    //根据月份获取季度开始月
    public int getQuarterStart(int month) {
        int months[] = {0, 3, 6, 9};
        if (month >= 0 && month <= 2) // 1-3月;0,1,2
            return months[0];
        else if (month >= 3 && month <= 5) // 4-6月;3,4,5
            return months[1];
        else if (month >= 6 && month <= 8) // 7-9月;6,7,8
            return months[2];
        else
            // 10-12月;10,11,12
            return months[3];
    }

    //根据传来的季度获取开始月
    public int getQuarterStartBy(Integer quarter) {
        int months[] = {0, 3, 6, 9};
        if (quarter == 1) // 1-3月;0,1,2
            return months[0];
        else if (quarter == 2) // 4-6月;3,4,5
            return months[1];
        else if (quarter == 3) // 7-9月;6,7,8
            return months[2];
        else
            // 10-12月;10,11,12
            return months[3];
    }

    //根据月份获取季度结束月
    public int getQuarterEnd(int month) {
        int months[] = {2, 5, 8, 11};
        if (month >= 0 && month <= 2) // 1-3月;0,1,2
            return months[0];
        else if (month >= 3 && month <= 5) // 4-6月;3,4,5
            return months[1];
        else if (month >= 6 && month <= 8) // 7-9月;6,7,8
            return months[2];
        else
            // 10-12月;10,11,12
            return months[3];
    }

    //根据月份获取季度结束月
    public int getQuarterEndBy(int quarter) {
        int months[] = {2, 5, 8, 11};
        if (quarter == 1) // 1-3月;0,1,2
            return months[0];
        else if (quarter == 2) // 4-6月;3,4,5
            return months[1];
        else if (quarter == 3) // 7-9月;6,7,8
            return months[2];
        else
            // 10-12月;10,11,12
            return months[3];
    }


    public void findByCondition(DispatchCarInfoDTO currentDTO, DispatchCarInfoDTO lastDTO, DispatchCollectBO bo) throws SerException {

        List<DispatchCarInfo> curretnList = super.findByCis(currentDTO);
        List<DispatchCarInfo> lastList = super.findByCis(lastDTO);

        Long companyDispatch = curretnList.stream().filter(p -> p.getCompanyDispatch() == Boolean.TRUE).count();
        Long uncompanyDispatch = curretnList.stream().filter(p -> p.getCompanyDispatch() == Boolean.FALSE).count();
        Long currentDispatch = (long) curretnList.size();
        Long lastDispatch = (long) lastList.size();
        Long dispatchSubtract = currentDispatch - lastDispatch;
        Double leaseCarCost = curretnList.stream().filter(p -> p.getCarRentalCost() != null).mapToDouble(p -> p.getCarRentalCost()).sum();
        Double parkCost = curretnList.stream().filter(p -> p.getParkCost() != null).mapToDouble(p -> p.getParkCost()).sum();
        Double roadCost = curretnList.stream().filter(p -> p.getRoadCost() != null).mapToDouble(p -> p.getRoadCost()).sum();
        Double mealCost = curretnList.stream().filter(p -> p.getMealCost() != null).mapToDouble(p -> p.getMealCost()).sum();

        Double lastLeaseCarCost = lastList.stream().filter(p -> p.getCarRentalCost() != null).mapToDouble(p -> p.getCarRentalCost()).sum();
        Double lastRoadCost = lastList.stream().filter(p -> p.getRoadCost() != null).mapToDouble(p -> p.getRoadCost()).sum();
        Double LastMealCost = lastList.stream().filter(p -> p.getMealCost() != null).mapToDouble(p -> p.getMealCost()).sum();

        Double totalCurrent = leaseCarCost + roadCost + mealCost;
        Double totalLast = lastLeaseCarCost + lastRoadCost + LastMealCost;
        Double costSubtract = totalCurrent - totalLast;

        bo.setCompanyDispatch(companyDispatch);
        bo.setUncompanyDispatch(uncompanyDispatch);
        bo.setCurrentDispatch(currentDispatch);
        bo.setLastDispatch(lastDispatch);
        bo.setDispatchSubtract(dispatchSubtract);
        bo.setLeaseCarCost(leaseCarCost);
        bo.setParkCarCost(parkCost);
        bo.setRoadCost(roadCost);
        bo.setMealCost(mealCost);
        bo.setCurrentCost(totalCurrent);
        bo.setLastCost(costSubtract);
        bo.setCostSubtract(costSubtract);
    }


    /**
     * 根据地区、项目组查询租车费
     */
    public Double findCost(DispatchCarInfoTO to) throws SerException {
        LeaseCarCostDTO dto = new LeaseCarCostDTO();
        dto.getConditions().add(Restrict.eq("area", to.getArea()));
        dto.getConditions().add(Restrict.eq("group", to.getGroup()));
        LeaseCarCost leaseCarCost = leaseCarCostSer.findOne(dto);
        if (leaseCarCost != null) {
            return leaseCarCost.getCost();
        } else {
            throw new SerException("该地区和项目组对应的租车费未添加,请先添加租车费用信息!");
        }
    }

    /**
     * //设置出车单号----IKE20170101-1...
     */
    public void setNumber(DispatchCarInfo model) throws SerException {
        String todayStr = LocalDate.now().toString();
        String todayNum = todayStr.replace("-", "");

        //查询当天最新的一条数据
        DispatchCarInfoDTO dto = new DispatchCarInfoDTO();
        dto.getConditions().add(Restrict.like("createTime", todayStr));
        dto.getSorts().add("createTime=desc");
        dto.setLimit(1);
        List<DispatchCarInfo> list = super.findByPage(dto);
        StringBuilder numStr = new StringBuilder();
        //拼接出车单
        if (model != null && !list.isEmpty()) {
            String number = list.get(0).getNumber();
            numStr.append(number.substring(0, 12));
            int lastNum = Integer.parseInt(number.substring(12)) + 1;
            numStr.append(lastNum);
        } else {
            numStr.append("IKE");
            numStr.append(todayNum);
            numStr.append("-");
            numStr.append("1");
        }
        model.setNumber(numStr.toString());
    }

    public LocalDateTime changeEndFormat(LocalDate date) throws SerException {
        String StartDayTime = date.toString() + " 23:59:59";
        return LocalDateTime.parse(StartDayTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public List<DriverDispatchFeeBO> findDispatchFree(String area, String projectGroup, Integer year, Integer month) throws SerException {
        if (month != null) {
            if (month != null) {
                //按月份查询已付款的出车数
                StringBuilder sql = new StringBuilder("select sum(cost) as fee from dispatchcar_basicinfo car where ");
                sql.append(" month(car.dispatchDate) = " + month + " and YEAR(car.dispatchDate)= " + year + " and area = '" + area + "' and project_group = '" + projectGroup + "' and car.findType = " + FindType.PAYED.getCode());
                String[] fields = new String[]{"fee"};
                return super.findBySql(sql.toString(), DriverDispatchFeeBO.class, fields);
            } else {
                throw new SerException("查询月份不能为空!");
            }
        } else {
            throw new SerException("查询月份不能为空!");
        }
    }

    @Override
    public List<DriverDispatchsBO> findDispatchs(String area, String projectGroup, Integer year, Integer month) throws SerException {
        if (month != null) {
            //按月份查询已付款的出车数
            StringBuilder sql = new StringBuilder("select count(driver) as sum from dispatchcar_basicinfo car where ");
            sql.append(" month(car.dispatchDate) = " + month + " and YEAR(car.dispatchDate) = " + year + " and area = '" + area + "' and project_group = '" + projectGroup + "' and car.findType = " + FindType.PAYED.getCode());
            String[] fields = new String[]{"sum"};
            return super.findBySql(sql.toString(), DriverDispatchsBO.class, fields);
        } else {
            throw new SerException("查询月份不能为空!");
        }
    }

    @Override
    public Double findOilAmount(String oilCardCode, Integer year, Integer month) throws SerException {

        String sql = "select addOilAmount , oilPrice from dispatchcar_basicinfo where oilCardNumber = '" + oilCardCode + "'"
                + " and month(addOilTime) = '" + month
                + "' and year(addOilTime) = '" + year + "'";
        String[] fields = new String[]{"addOilAmount", "oilPrice"};
        List<DispatchCarInfo> list = super.findBySql(sql, DispatchCarInfo.class, fields);

        if (!CollectionUtils.isEmpty(list)) {
            Double addOilAmount = list.stream().filter(p -> p.getAddOilAmount() != null).mapToDouble(DispatchCarInfo::getAddOilAmount).sum();
            Double oilPrice = list.stream().filter(p -> p.getOilPrice() != null).mapToDouble(DispatchCarInfo::getOilPrice).sum();
            return addOilAmount * oilPrice;
        } else {
            return 0.0;
        }
    }

    @Override
    public List<DriverInfoBO> findDriver() throws SerException {
        DriverInfoDTO dto = new DriverInfoDTO();
        //查询所有未解约审核通过的司机
        dto.getConditions().add(Restrict.eq("breakAgreement", 0));
        dto.getConditions().add(Restrict.eq("audit", 1));
        List<DriverInfoBO> boList = driverInfoAPI.pageList(dto);
        return boList;
    }

    @Override
    public List<StaffEntryRegisterBO> findAllEntry() throws SerException {
        List<StaffEntryRegisterBO> boList = new ArrayList<>(0);
        if (moduleAPI.isCheck("staffentry")) {
            StaffEntryRegisterDTO dto = new StaffEntryRegisterDTO();
            boList = staffEntryRegisterAPI.listStaffEntryRegister(dto);
        }
        return boList;
    }

    @Override
    public List<OilCardBasicCarBO> findAllOil() throws SerException {
        List<OilCardBasicBO> boList = oilCardBasicAPI.findOilCard();
        List<OilCardBasicCarBO> carBOS = BeanTransform.copyProperties(boList, OilCardBasicCarBO.class);
        return carBOS;
    }

//    @Override
//    public void copyServer() throws SerException {
//        List<ServerCarInfo> boList = carInfoSer.findAll();
//        List<DispatchCarInfo> dispatchCarInfos = new ArrayList<>();
//        for(ServerCarInfo carInfo : boList){
//            DispatchCarInfo dispatchCarInfo = new DispatchCarInfo();
//            dispatchCarInfo.setDriver("无");
//            if(!"".equals(carInfo.getDriver_id()) && carInfo.getDriver_id() != null){
//                    DriverBO driverBO = driverAPI.findOne(carInfo.getDriver_id());
//                    dispatchCarInfo.setDriver(driverBO.getDriverName());
//                }
//                dispatchCarInfo.setNumber(carInfo.getRunNum());
//                dispatchCarInfo.setCarUser(carInfo.getOwner());
//                dispatchCarInfo.setArea(carInfo.getArea());
//                dispatchCarInfo.setProject(carInfo.getProject());
//
//
//                if(carInfo.getStartDate() != null && !"".equals(carInfo.getStartDate())) {
//                    dispatchCarInfo.setStartTime(DateUtil.parseDateTime(carInfo.getStartDate()));
//                }
//                if(carInfo.getEndDate() != null && !"".equals(carInfo.getEndDate())) {
//                    dispatchCarInfo.setEndTime(DateUtil.parseDateTime(carInfo.getEndDate()));
//                }
//                dispatchCarInfo.setSiesta(carInfo.getRest());
//                dispatchCarInfo.setOverWorkTime(carInfo.getOverTime());
//                if(!"".equals(carInfo.getOwner()) && carInfo.getOwner() != null) {
//                    dispatchCarInfo.setAccompanyUser(carInfo.getWithOwner());
//                }else{
//                    dispatchCarInfo.setAccompanyUser("无");
//                }
//                dispatchCarInfo.setAircondition(carInfo.getIsAri());
//                dispatchCarInfo.setDowntown(carInfo.getCity());
//                dispatchCarInfo.setOilPrice(carInfo.getOilPrice());
//                dispatchCarInfo.setOilCardBalance(carInfo.getBalance());
//                dispatchCarInfo.setStartMileage(carInfo.getStartNum());
//                dispatchCarInfo.setEndMileage(carInfo.getEndNum());
//                dispatchCarInfo.setMileageSubtract(carInfo.getTotalNum());
//                dispatchCarInfo.setPlanTaskAmount(carInfo.getPlanNum());
//                dispatchCarInfo.setFinishTaskAmount(carInfo.getFinishNum());
//                dispatchCarInfo.setParkCost(carInfo.getPassCosts());
//                dispatchCarInfo.setRoadCost(carInfo.getPassCosts());
//                if(carInfo.getManner().equals("优秀")) {
//                    dispatchCarInfo.setEvaluatedriver(Evaluate.GOOD);
//                }else if(carInfo.getManner().equals("良好")){
//                    dispatchCarInfo.setEvaluatedriver(Evaluate.WELL);
//                }else{
//                    dispatchCarInfo.setEvaluatedriver(Evaluate.BAD);
//                }
//                dispatchCarInfo.setReceipt(carInfo.getNote());
//                dispatchCarInfo.setMealCost(carInfo.getFootCost());
//                dispatchCarInfo.setOilWear(carInfo.getOilTotal());
//                dispatchCarInfo.setOverWorkCost(carInfo.getCosts());
//                dispatchCarInfo.setTotalCost(carInfo.getMoney());
//
//                dispatchCarInfo.setCarNumber("无");
//                dispatchCarInfo.setCarRentalCost(0.0);
//                dispatchCarInfo.setCost(0.0);
//                if(!"".equals(carInfo.getToDate()) && carInfo.getToDate() != null){
//                    dispatchCarInfo.setDispatchDate(DateUtil.parseDate(carInfo.getToDate()));
//                }
//                dispatchCarInfo.setDispatchReason("无");
//                dispatchCarInfo.setGroup("无");
//                dispatchCarInfo.setOilCardNumber("无");
//                dispatchCarInfo.setPrincipal("无");
//                dispatchCarInfo.setProjectApproval(false);
//                dispatchCarInfo.setUserNumber("无");
//                dispatchCarInfos.add(dispatchCarInfo);
//            }
//        super.save(dispatchCarInfos);
//    }

    @Override
    public void copyDriver() throws SerException {

    }

    @Override
    public List<String> findAllProject() throws SerException {
        Set<String> project = new HashSet<>();
        if (moduleAPI.isCheck("businessproject")) {
            project = baseInfoManageAPI.allInnerProjects();
        } else {
            throw new SerException("请去模块管理设置模块关联");
        }
        List<String> allProject = new ArrayList<>(project);
        return allProject;
    }

    @Override
    public List<DispatchCarInfoBO> findWrongRecord(DispatchCarInfoDTO dto) throws SerException {
        DispatchCarInfoDTO dispatchCarInfoDTO = new DispatchCarInfoDTO();
        dispatchCarInfoDTO.getConditions().add(Restrict.eq("findType", FindType.WRONG));
        if (!"".equals(dto.getCarUser()) && dto.getCarUser() != null) {
            dto.getConditions().add(Restrict.eq("carUser", dto.getCarUser()));
        }
        if (!"".equals(dto.getNumber()) && dto.getNumber() != null) {
            dto.getConditions().add(Restrict.eq("number", dto.getNumber()));
        }
        List<DispatchCarInfo> dispatchCarInfos = super.findByCis(dispatchCarInfoDTO);
        List<DispatchCarInfoBO> dispatchCarInfoBOS = BeanTransform.copyProperties(dispatchCarInfos, DispatchCarInfoBO.class);
        return dispatchCarInfoBOS;
    }

    @Override
    public void correctMistake(DispatchCarInfoTO to) throws SerException {
        DispatchCarInfo model = super.findById(to.getId());
        if (model != null) {
            UserBO userBO = userAPI.findByUsername(to.getCarUser());
            to.setUserNumber(userBO.getEmployeeNumber());
            //加油费 = 加油量 * 当天油价 ，加油量 = 总油耗 * 总里程数 ， 总油耗 = 本车耗油 + 是否开空调 + 是否市内
            DriverInfoBO driver = driverInfoAPI.findByDriver(to.getDriver());
            if (driver == null) {
                throw new SerException("司机不存在!");
            }
            Double oilWear = driver.getCarFuel();
            if (to.getAircondition()) {
                oilWear = oilWear + 0.01;
            }
            if (to.getDowntown()) {
                oilWear = oilWear + 0.01;
            }
//                model.setOilWear(oilWear);
            model.setMileageSubtract(to.getEndMileage() - to.getStartMileage());
            //设置应加油量
            model.setShouldAmount(model.getMileageSubtract() * oilWear);
            //设置应加油费
            model.setShouldAmountMoney(model.getShouldAmount() * to.getOilPrice());
            //设置实加油费
            model.setAddOilAmountMoney(model.getAddOilAmount() * to.getOilPrice());
            //设置补充加油量和欠加油量
            if (model.getAddOilAmount() - model.getShouldAmount() > 0) {
                model.setLessOil(0.0);
                model.setLessOilFee(0.0);
                model.setSupplementOil(model.getAddOilAmount() - model.getShouldAmount());
                model.setSupplementFee(model.getSupplementOil() * to.getOilPrice());
            } else if (model.getAddOilAmount() - model.getShouldAmount() < 0) {
                model.setLessOil(model.getShouldAmount() - model.getAddOilAmount());
                model.setLessOilFee(model.getLessOil() * to.getOilPrice());
                model.setSupplementOil(0.0);
                model.setSupplementFee(0.0);
            } else {
                model.setLessOil(0.0);
                model.setLessOilFee(0.0);
                model.setSupplementOil(0.0);
                model.setSupplementFee(0.0);
            }
//                model.setAddOilAmount(model.getMileageSubtract() * oilWear);
//                if (to.getOilPrice() != null) {
//                    model.setOilCost(model.getAddOilAmount() * to.getOilPrice());
//                } else {
//                    model.setOilCost(0.0);
//                }

            BeanTransform.copyProperties(to, model, true);

            model.setMileageSubtract(model.getEndMileage() - model.getStartMileage());
            //计算餐补、加班费，满8小时，并有4小时为22点后，则给予餐费补贴30元,超过8个小时后的加班费 = 租车费 / 8 * 小时数
            //实际上班时长
            Long workHours = ChronoUnit.HOURS.between(model.getStartTime(), model.getEndTime());
            if (workHours > 8) {
                //当天22点
                LocalDateTime dateTime = LocalDateTime.of(model.getStartTime().toLocalDate(), LocalTime.of(22, 0));
                //出车开始时间+4 > 22:00 则补贴30元
                if (model.getStartTime().plusHours(4).isAfter(dateTime)) {
                    model.setMealCost(30.0);
                } else {
                    model.setMealCost(0.0);
                }
                if (!model.getSiesta()) {
                    model.setOverWorkTime((double) (workHours - 8));
                } else {
                    model.setOverWorkTime((double) (workHours - 8 - 1));
                }
            } else {
                model.setMealCost(0.0);
                model.setOverWorkTime(0.0);
            }
            //查找租车费用
            model.setCarRentalCost(findCost(to));
            //查询油卡余额
            model.setOilCardBalance(oilCardBasicAPI.findByCode(to.getOilCardNumber()).getBalance());
//                model.setOverWorkCost(model.getCarRentalCost() / 8 * model.getOverWorkTime());
//                model.setCost(model.getMealCost() + model.getCarRentalCost() + model.getOverWorkCost() + model.getParkCost() + model.getRoadCost());
//                model.setTotalCost(model.getMealCost() + model.getCarRentalCost() + model.getOverWorkCost() + model.getParkCost() + model.getRoadCost() + model.getOilCost());

            model.setModifyTime(LocalDateTime.now());
            model.setFindType(FindType.WAITAUDIT);
            super.update(model);
        } else {
            throw new SerException("更新对象不能为空");
        }
    }

    @Override
    public void leadExcel(List<DispatchCarInfoTO> toList) throws SerException {
        UserBO userBO = userAPI.currentUser();
        List<DispatchCarInfo> list = BeanTransform.copyProperties(toList, DispatchCarInfo.class, true);
        list.stream().forEach(str -> {
            str.setModifyTime(LocalDateTime.now());
            str.setCreateTime(LocalDateTime.now());
        });
        super.save(list);
    }

    @Override
    public byte[] exportExcel(ExportDispatchCarInfoTO to) throws SerException {
        DispatchCarInfoDTO dto = new DispatchCarInfoDTO();
        //根据地区和开始时间和结束时间来导出excel
        if (org.apache.commons.lang3.StringUtils.isNotBlank(to.getArea()) && org.apache.commons.lang3.StringUtils.isNotBlank(to.getStarTime()) && org.apache.commons.lang3.StringUtils.isNotBlank(to.getEndTime())) {
            LocalDate[] localDates = new LocalDate[]{DateUtil.parseDate(to.getStarTime()), DateUtil.parseDate(to.getEndTime())};
            dto.getConditions().add(Restrict.eq("area", to.getArea()));
            dto.getConditions().add(Restrict.between("dispatchDate", localDates));
        }

        List<DispatchCarInfo> list = super.findByCis(dto);
        List<DispatchCarInfoSetExcel> toList = new ArrayList<DispatchCarInfoSetExcel>();
        for (DispatchCarInfo model : list) {
            DispatchCarInfoSetExcel excel = BeanTransform.copyProperties(model, DispatchCarInfoSetExcel.class);
            toList.add(excel);
        }
        Excel excel = new Excel(0, 2);
        byte[] bytes = ExcelUtil.clazzToExcel(toList, excel);
        return bytes;

    }

    @Override
    public byte[] templateExport() throws SerException {
        List<DispatchCarInfoSetExcel> dispatchCarInfoSetExcels = new ArrayList<>();

        DispatchCarInfoSetExcel excel = new DispatchCarInfoSetExcel();
        excel.setCarSource(CarSource.MANUALENTRY);
        excel.setNumber("出车单号");
        excel.setDriver("司机名称");
        excel.setCompanyDispatch(true);
        excel.setCarUser("用车人");
        excel.setUserNumber("员工编号");
        excel.setArea("所属地区");
        excel.setGroup("所属项目组");
        excel.setProjectApproval(true);
        excel.setProject("项目名称");
        excel.setAcctype(Acctype.MAIN);
        excel.setDispatchDate("2017-01-01");
        excel.setStartTime("2017-01-01 09:09:09");
        excel.setEndTime("2017-01-01 10:01:01");
        excel.setSiesta(true);
        excel.setOverWorkTime(10.0);
        excel.setDispatchReason("用车事由");
        excel.setAccompanyUser("用车随同人员");
        excel.setCarNumber("车牌号码");
        excel.setOilCardNumber("所有油卡编号");
        excel.setAircondition(true);
        excel.setDowntown(true);
        excel.setAddOil(true);
        excel.setAddOilExplain("补充加油说明");
        excel.setSupplementFee(10.0);
        excel.setOweOilExplain("欠油说明");
        excel.setLessOil(10.0);
        excel.setLessOilFee(10.0);
        excel.setAddOilTime("2017-01-01 01:01:01");
        excel.setShouldAmount(10.1);
        excel.setShouldAmountMoney(10.1);
        excel.setAddOilAmount(10.1);
        excel.setAddOilAmountMoney(10.1);
        excel.setOilCardBalance(10.1);
        excel.setOilPrice(10.1);
        excel.setPrincipal("任务下达人");
        excel.setPlanTaskAmount(10);
        excel.setFinishTaskAmount(10);
        excel.setStartMileage(10.1);
        excel.setEndMileage(10.1);
        excel.setMileageSubtract(10.1);
        excel.setMileageOfGPS(10.2);
        excel.setParkCost(10.1);
        excel.setRoadCost(10.1);
        excel.setMealCost(10.1);
        excel.setEvaluatedriver(Evaluate.GOOD);
        excel.setReceipt(true);
        excel.setProjectCharge("项目模块负责人");
        excel.setProjectChargeIdea("项目模块负责意见");
        excel.setIfPass(true);
        excel.setClientModule("客户模块负责人");
        excel.setClientModuleIdea("客户模块负责人意见");
        excel.setClientDate("2017-01-01 10:10:10");
        excel.setIfFreeze(true);
        excel.setHeadModule("素养模块负责人");
        excel.setHeadModuleIdea("素养模块负责人意见");
        excel.setHeadDate("2017-01-01 10:10:10");
        excel.setBudgetAuditUser("预算模块负责人");
        excel.setBudgetModuleIdea("预算模块意见");
        excel.setBudgetAuditTime("2017-01-01 10:10:10");
        excel.setAccountModule("账务模块负责人");
        excel.setAccountModuleIdea("账务模块负责人意见");
        excel.setAccountDate("2017-01-01 10:01:01");
        excel.setCorrect(true);
        excel.setSender("寄件人");
        excel.setSendDate("2017-01-01");
        excel.setIfSendArchiveAl(true);
        excel.setIfSendReimbursementAl(true);
        excel.setTotalParking(10.0);
        excel.setTotalReceipts(10);
        excel.setIfSendAddOilReceipts(true);
        excel.setReceiver("收票人");
        excel.setReceiveDate("2017-01-01");
        excel.setReceiveReceipts("收到发票情况");
        excel.setMoneyModule("资金模块负责人");
        excel.setMoneyModuleIdea("资金模块意见");
        excel.setMoneyDate("2017-01-01 10:10:10");
        excel.setExpectPayDate("2017-01-01");
        excel.setPaymentSchedule("付款计划");
        excel.setIfPayed(true);
        excel.setPrincipalAuditTime("2017-01-01 10:10:10");
        excel.setCarRentalCost(10.1);
        excel.setStaus(Status.CONGEAL);
        excel.setFindType(FindType.WAITAUDIT);


        Excel exce = new Excel(0, 2);
        byte[] bytes = ExcelUtil.clazzToExcel(dispatchCarInfoSetExcels, exce);
        return bytes;
    }

    @Override
    public List<PayDriverMoneyCollectBO> driverCollect(String startTime, String endTime) throws SerException {
        DispatchCarInfoDTO dto = new DispatchCarInfoDTO();
        LocalDate startDate = DateUtil.parseDate(startTime);
        LocalDate endDate = DateUtil.parseDate(endTime);
        LocalDate[] localDates = new LocalDate[]{startDate, endDate};
        dto.getConditions().add(Restrict.between("expectPayDate", localDates));
        dto.getConditions().add(Restrict.between("findType", FindType.WAITPAY));
        List<DispatchCarInfo> dispatchCarInfos = super.findByCis(dto);
        List<PayDriverMoneyCollectBO> boList = new ArrayList<>();
        if (dispatchCarInfos != null && dispatchCarInfos.size() > 0) {
            for (DispatchCarInfo dispatchCarInfo : dispatchCarInfos) {
                Double carRentalCost = 0.0;
                Double overUnitCost = 0.0;
                Double overWorkTime = 0.0;
                Double parkRost = 0.0;
                Double roadCost = 0.0;
                Double mealCost = 0.0;
                String driver = dispatchCarInfo.getDriver();
                dto.getConditions().add(Restrict.eq("driver", driver));
                List<DispatchCarInfo> dispatchCarInfoList = super.findByCis(dto);
                carRentalCost = dispatchCarInfoList.stream().filter(p -> p.getCarRentalCost() != null).mapToDouble(P -> P.getCarRentalCost()).sum();
                overWorkTime = dispatchCarInfoList.stream().filter(p -> p.getOverWorkTime() != null).mapToDouble(P -> P.getOverWorkTime()).sum();
                overUnitCost = carRentalCost / overWorkTime;
                parkRost = dispatchCarInfoList.stream().filter(p -> p.getParkCost() != null).mapToDouble(P -> P.getParkCost()).sum();
                roadCost = dispatchCarInfoList.stream().filter(p -> p.getRoadCost() != null).mapToDouble(P -> P.getRoadCost()).sum();
                mealCost = dispatchCarInfoList.stream().filter(p -> p.getMealCost() != null).mapToDouble(P -> P.getMealCost()).sum();
                PayDriverMoneyCollectBO payDriverMoneyCollectBO = new PayDriverMoneyCollectBO("", "", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "合计");
                payDriverMoneyCollectBO.setCarRentalCost(carRentalCost);
                payDriverMoneyCollectBO.setOverWorkTime(overWorkTime);
                payDriverMoneyCollectBO.setOverUnitCost(overUnitCost);
                payDriverMoneyCollectBO.setParkCost(parkRost);
                payDriverMoneyCollectBO.setRoadCost(roadCost);
                payDriverMoneyCollectBO.setMealCost(mealCost);
                boList.add(payDriverMoneyCollectBO);
            }
        }
        return boList;
    }

    @Override
    public List<PayedCollectBO> collectPayed(String startTime, String endTime) throws SerException {
        DispatchCarInfoDTO dto = new DispatchCarInfoDTO();
        LocalDate startDate = DateUtil.parseDate(startTime);
        LocalDate endDate = DateUtil.parseDate(endTime);
        LocalDate[] localDates = new LocalDate[]{startDate, endDate};
        dto.getConditions().add(Restrict.eq("findType", FindType.PAYED));
        dto.getConditions().add(Restrict.between("expectPayDate", localDates));
        List<DispatchCarInfo> list = super.findByCis(dto);
        Set<String> areaSet = list.stream().map(p -> p.getArea()).collect(Collectors.toSet());
        List<PayedCollectBO> payedCollectBOS = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (DispatchCarInfo dispatchCarInfo : list) {
                for (String area : areaSet) {
                    PayedCollectBO payedCollectBO = new PayedCollectBO("", "", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
                    PayedCollectBO payedCollectBO2 = new PayedCollectBO("", "合计", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
                    DispatchCarInfoDTO dispatchCarInfoDTO = new DispatchCarInfoDTO();
                    dispatchCarInfoDTO.getConditions().add(Restrict.eq("findType", FindType.PAYED));
                    dispatchCarInfoDTO.getConditions().add(Restrict.between("expectPayDate", localDates));
                    dispatchCarInfoDTO.getConditions().add(Restrict.eq("area", area));
                    List<DispatchCarInfo> dispatchCarInfos = super.findByCis(dispatchCarInfoDTO);
                    Set<String> projectSet = dispatchCarInfos.stream().map(p -> p.getProject()).collect(Collectors.toSet());
                    for (String project : projectSet) {
                        DispatchCarInfoDTO dispatchCarInfoDTO2 = new DispatchCarInfoDTO();
                        dispatchCarInfoDTO2.getConditions().add(Restrict.eq("findType", FindType.PAYED));
                        dispatchCarInfoDTO2.getConditions().add(Restrict.between("expectPayDate", localDates));
                        dispatchCarInfoDTO2.getConditions().add(Restrict.eq("area", area));
                        dispatchCarInfoDTO2.getConditions().add(Restrict.eq("project", project));
                        List<DispatchCarInfo> dispatchCarInfoList = super.findByCis(dispatchCarInfoDTO2);
                        Double supplementFee = 0.0;
                        Double carRentalUtilCost = 0.0;
                        Double overWorkTime = 0.0;
                        Double overWorkCost = 0.0;
                        Double parkCost = 0.0;
                        Double roadCost = 0.0;
                        Double mealCost = 0.0;
                        supplementFee = dispatchCarInfoList.stream().filter(p -> p.getSupplementFee() != null).mapToDouble(p -> p.getSupplementFee()).sum();
                        Double carRentalCost = dispatchCarInfoList.stream().filter(p -> p.getCarRentalCost() != null).mapToDouble(p -> p.getCarRentalCost()).sum();
                        carRentalUtilCost = carRentalCost / dispatchCarInfoList.size();
                        overWorkTime = dispatchCarInfoList.stream().filter(p -> p.getOverWorkTime() != null).mapToDouble(p -> p.getOverWorkTime()).sum();
                        overWorkCost = dispatchCarInfoList.stream().filter(p -> p.getOverWorkCost() != null).mapToDouble(p -> p.getOverWorkCost()).sum();
                        parkCost = dispatchCarInfoList.stream().filter(p -> p.getParkCost() != null).mapToDouble(p -> p.getParkCost()).sum();
                        roadCost = dispatchCarInfoList.stream().filter(p -> p.getMealCost() != null).mapToDouble(p -> p.getMealCost()).sum();
                        payedCollectBO.setArea(area);
                        payedCollectBO.setProject(project);
                        payedCollectBO.setSupplementFee(supplementFee);
                        payedCollectBO.setCarRentalUtilCost(carRentalUtilCost);
                        payedCollectBO.setOverWorkTime(overWorkTime);
                        payedCollectBO.setOverWorkCost(overWorkCost);
                        payedCollectBO.setParkCost(parkCost);
                        payedCollectBO.setRoadCost(roadCost);
                        payedCollectBO.setMealCost(mealCost);
                        Double total = supplementFee + overWorkCost + parkCost + roadCost + mealCost;
                        payedCollectBO.setTotal(total);
                        payedCollectBOS.add(payedCollectBO);
                    }
                    Double suplementFee2 = payedCollectBOS.stream().filter(p -> p.getSupplementFee() != null).mapToDouble(p -> p.getSupplementFee()).sum();
                    Double carRentalUtilCost2 = payedCollectBOS.stream().filter(p -> p.getCarRentalUtilCost() != null).mapToDouble(p -> p.getCarRentalUtilCost()).sum();
                    Double overWorkTime2 = payedCollectBOS.stream().filter(p -> p.getOverWorkTime() != null).mapToDouble(p -> p.getOverWorkTime()).sum();
                    Double overWorkCost2 = payedCollectBOS.stream().filter(p -> p.getOverWorkCost() != null).mapToDouble(p -> p.getOverWorkCost()).sum();
                    Double parkCost2 = payedCollectBOS.stream().filter(p -> p.getParkCost() != null).mapToDouble(p -> p.getParkCost()).sum();
                    Double roadCost2 = payedCollectBOS.stream().filter(p -> p.getRoadCost() != null).mapToDouble(p -> p.getRoadCost()).sum();
                    Double mealCost2 = payedCollectBOS.stream().filter(p -> p.getMealCost() != null).mapToDouble(p -> p.getMealCost()).sum();
                    payedCollectBO2.setSupplementFee(suplementFee2);
                    payedCollectBO2.setCarRentalUtilCost(carRentalUtilCost2);
                    payedCollectBO2.setOverWorkCost(overWorkCost2);
                    payedCollectBO2.setOverWorkTime(overWorkTime2);
                    payedCollectBO2.setParkCost(parkCost2);
                    payedCollectBO2.setRoadCost(roadCost2);
                    payedCollectBO2.setMealCost(mealCost2);
                    payedCollectBOS.add(payedCollectBO2);
                }
            }
        }
        return payedCollectBOS;
    }


    @Override
    public byte[] exportExcel(CollectIntervalType collectIntervalType, CollectType
            collectType, ExportCollectPayedTO to) throws SerException {
        if (collectIntervalType == null) {
            throw new SerException("导出的汇总类型不能为空！");
        }

        List<DispatchCarInfo> proejctList = null;
        if (collectType == CollectType.AREA) {
            //分组查询地区、项目组、项目
            proejctList = super.findBySql("select area , project_group , project ,1 from dispatchcar_basicinfo group by area , project_group , project ",
                    DispatchCarInfo.class, new String[]{"area", "group", "project"});
        } else {
            //分组查询地区、项目组、项目
            proejctList = super.findBySql("select driver , project_group , project ,1 from dispatchcar_basicinfo group by driver , project_group , project ",
                    DispatchCarInfo.class, new String[]{"area", "group", "project"});
        }

        List<DispatchCollectBO> returnList = new ArrayList<DispatchCollectBO>();

        //遍历项目组
        for (DispatchCarInfo proejct : proejctList) {
            DispatchCollectBO bo = new DispatchCollectBO();
            bo.setDriver(proejct.getDriver());
            bo.setArea(proejct.getArea());
            bo.setGroup(proejct.getGroup());
            bo.setProject(proejct.getProject());

            findByType(proejct.getArea(), proejct.getGroup(), proejct.getProject(), bo, collectIntervalType, to);

            returnList.add(bo);
        }

        List<DispatchcarInfoCollectSetExcel> toList = new ArrayList<DispatchcarInfoCollectSetExcel>();
        for (DispatchCollectBO model : returnList) {
            DispatchcarInfoCollectSetExcel excel = new DispatchcarInfoCollectSetExcel();
            BeanUtils.copyProperties(model, excel);
            toList.add(excel);
        }
        Excel excel = new Excel(0, 2);
        byte[] bytes = ExcelUtil.clazzToExcel(toList, excel);
        return bytes;

    }


    public void findByType(String area, String group, String project, DispatchCollectBO bo, CollectIntervalType collectIntervalType, ExportCollectPayedTO to) throws SerException {
        //查询指定汇总类型的出车情况(昨日/上周/上月/上季度/上年度)
        DispatchCarInfoDTO currentDTO = new DispatchCarInfoDTO();
        currentDTO.getConditions().add(Restrict.eq("area", area));
        currentDTO.getConditions().add(Restrict.eq("group", group));
        currentDTO.getConditions().add(Restrict.eq("project", project));
        //查询指定汇总类型的出车情况(本日/本周/本月/本季度/本年度)
        DispatchCarInfoDTO lastDTO = new DispatchCarInfoDTO();
        lastDTO.getConditions().add(Restrict.eq("area", area));
        lastDTO.getConditions().add(Restrict.eq("group", group));
        lastDTO.getConditions().add(Restrict.eq("project", project));

        switch (collectIntervalType) {
            case DAY:
                setDayCondition(currentDTO, lastDTO, bo, CollectIntervalType.DAY, to);
                findByCondition(currentDTO, lastDTO, bo);
                break;
            case WEEK:
                setCondition(currentDTO, lastDTO, bo, CollectIntervalType.WEEK, to);
                findByCondition(currentDTO, lastDTO, bo);
                break;
            case MONTH:
                setCondition(currentDTO, lastDTO, bo, CollectIntervalType.MONTH, to);
                findByCondition(currentDTO, lastDTO, bo);
                break;
            case QUARTER:
                setCondition(currentDTO, lastDTO, bo, CollectIntervalType.QUARTER, to);
                findByCondition(currentDTO, lastDTO, bo);
                break;
            case YEAR:
                setCondition(currentDTO, lastDTO, bo, CollectIntervalType.YEAR, to);
                findByCondition(currentDTO, lastDTO, bo);
                break;
        }
    }

    //查询本天、昨天出车情况
    public void setDayCondition(DispatchCarInfoDTO currentDTO, DispatchCarInfoDTO lastDTO, DispatchCollectBO bo, CollectIntervalType collectIntervalType, ExportCollectPayedTO to) {
        LocalDate today = DateUtil.parseDate(to.getExportDay());
        LocalDate yesterday = today.minusDays(1);
        currentDTO.getConditions().add(Restrict.like("dispatchDate", today.toString()));
        lastDTO.getConditions().add(Restrict.like("dispatchDate", yesterday.toString()));
    }


    //查询周、月、季、年
    public void setCondition(DispatchCarInfoDTO currentDTO, DispatchCarInfoDTO lastDTO, DispatchCollectBO bo, CollectIntervalType collectIntervalType, ExportCollectPayedTO to) throws SerException {
        LocalDate currentStarDay = null;
        LocalDate currentEndDay = null;

        LocalDate lastStarDay = null;
        LocalDate lastEndDay = null;

        switch (collectIntervalType) {
            case WEEK:
//                currentStarDay = DateUtil.getStartWeek();
//                currentEndDay = DateUtil.getEndWeek();
                LocalDate startDate = DateUtil.parseDate(to.getWeekDay());
                LocalDate endDate = null;
                Integer startWeek = startDate.getDayOfWeek().getValue();
                switch (startWeek) {
                    case 1:
                        endDate = startDate.minusDays(2);
                        break;
                    case 2:
                        endDate = startDate.minusDays(3);
                        break;
                    case 3:
                        endDate = startDate.minusDays(4);
                        break;
                    case 4:
                        endDate = startDate.minusDays(5);
                        break;
                    case 5:
                        endDate = startDate.minusDays(6);
                        break;
                    case 6:
                        endDate = startDate;
                        break;
                    case 7:
                        endDate = startDate.minusDays(1);
                        break;
                }
                currentStarDay = startDate;
                currentEndDay = endDate;
                LocalDate lastStartDate = DateUtil.parseDate(to.getWeekDay()).minusWeeks(1);
                LocalDate lastEndDate = null;
                Integer lastStartWeek = lastStartDate.getDayOfWeek().getValue();
                switch (lastStartWeek) {
                    case 1:
                        lastEndDate = lastStartDate.minusDays(2);
                        break;
                    case 2:
                        lastEndDate = lastStartDate.minusDays(3);
                        break;
                    case 3:
                        lastEndDate = lastStartDate.minusDays(4);
                        break;
                    case 4:
                        lastEndDate = lastStartDate.minusDays(5);
                        break;
                    case 5:
                        lastEndDate = lastStartDate.minusDays(6);
                        break;
                    case 6:
                        lastEndDate = lastStartDate;
                        break;
                    case 7:
                        lastEndDate = lastStartDate.minusDays(1);
                        break;
                }
                lastStarDay = lastStartDate;
                lastEndDay = lastEndDate;
                break;
            case MONTH:
//                currentStarDay = DateUtil.getStartMonth();
//                currentEndDay = DateUtil.getEndMonth();
//                lastStarDay = currentStarDay.minusMonths(1);
//                lastEndDay = currentEndDay.minusMonths(1);
                String month = to.getMonth();
                String year = to.getYear();
                if (month.length() == 1) {
                    String startDate1 = year + " 0" + month + "-" + "01";
                    currentStarDay = DateUtil.parseDate(startDate1);
                    Integer nextMonth = Integer.valueOf(month) + 1;
                    if (nextMonth < 10) {
                        String nextDate = year + " 0" + nextMonth + "-" + "01";
                        LocalDate nextDay = DateUtil.parseDate(nextDate);
                        currentEndDay = nextDay.minusDays(1);
                    } else {
                        String nextDate = year + " " + nextMonth + "-" + "01";
                        LocalDate nextDay = DateUtil.parseDate(nextDate);
                        currentEndDay = nextDay.minusDays(1);
                    }

                    Integer month2 = Integer.valueOf(to.getMonth()) - 1;
                    if (month2 == 0) {
                        String startDate2 = (Integer.valueOf(year) - 1) + " 12" + "01";
                        lastStarDay = DateUtil.parseDate(startDate2);
                        String endDate2 = year + "01" + "01";
                        lastEndDay = DateUtil.parseDate(endDate2).minusDays(1);
                    } else {
                        String startDate2 = year + " 0" + month2 + "-" + "01";
                        lastStarDay = DateUtil.parseDate(startDate2);
                        String endDate2 = year + " 0" + month2;
                        lastEndDay = DateUtil.parseDate(endDate2).minusDays(1);
                    }
                } else {
                    String startDate1 = year + " " + month + "-" + "1";
                    Integer nextMonth = Integer.valueOf(month) + 1;
                    currentStarDay = DateUtil.parseDate(startDate1);
                    if (nextMonth > 12) {
                        String nextDate = (Integer.valueOf(year) + 1) + " 01" + "-" + "01";
                        LocalDate nextDay = DateUtil.parseDate(nextDate);
                        currentEndDay = nextDay.minusDays(1);
                    } else {
                        String nextDate = year + " " + nextMonth + "-" + "1";
                        LocalDate nextDay = DateUtil.parseDate(nextDate);
                        currentEndDay = nextDay.minusDays(1);
                    }

                    Integer month2 = Integer.valueOf(to.getMonth()) - 1;
                    String startDate2 = year + " " + month2 + "-" + "01";
                    lastStarDay = DateUtil.parseDate(startDate2);
                    String endDate2 = year + " " + month2;
                    lastEndDay = DateUtil.parseDate(endDate2).minusDays(1);
                }

                break;
            case QUARTER:
                Date date = new Date();
                date.getMonth();
//                currentStarDay = DateUtil.getStartDayOfMonth(getQuarterStart(date.getMonth()));
//                currentEndDay = DateUtil.getEndDaYOfMonth(getQuarterEnd(date.getMonth()));
//                lastStarDay = currentStarDay.minusMonths(3);
//                lastEndDay = currentEndDay.minusMonths(3);
                currentStarDay = DateUtil.getStartDayOfMonth(getQuarterStartBy(to.getQuarter()));
                currentEndDay = DateUtil.getEndDaYOfMonth(getQuarterEndBy(to.getQuarter()));
                lastStarDay = currentStarDay.minusMonths(3);
                lastEndDay = currentEndDay.minusMonths(3);

                break;
            case YEAR:
//                currentStarDay = DateUtil.getStartYear();
//                currentEndDay = DateUtil.getEndYear();
                currentStarDay = DateUtil.parseDate(to.getYear() + " 01" + "-01");
                currentEndDay = DateUtil.parseDate(Integer.valueOf(to.getYear() + 1) + " 01" + "-01").minusDays(1);
                lastStarDay = currentStarDay.minusYears(1);
                lastEndDay = currentEndDay.minusYears(1);
                break;
        }

        LocalDate[] currentCondition = new LocalDate[]{currentStarDay, currentEndDay};
        LocalDate[] lastCondition = new LocalDate[]{lastStarDay, lastEndDay};

        currentDTO.getConditions().add(Restrict.between("dispatchDate", currentCondition));
        lastDTO.getConditions().add(Restrict.between("dispatchDate", lastCondition));
    }

    @Override
    public List<CollectDispatchcarBO> countCar(CollectDispatchcarDTO dto) throws SerException {
        List<DispatchCarInfo> dispatchCarInfos = super.findAll();
        LocalDate startDate = DateUtil.parseDate(dto.getStartTime());
        LocalDate endDate = DateUtil.parseDate(dto.getEndTime());
        LocalDate startMonth = DateUtil.getStartDayOfMonth(startDate.getYear(), startDate.getMonth().getValue());
        LocalDate endMonth = DateUtil.getEndDaYOfMonth(endDate.getYear(), endDate.getMonth().getValue());
        LocalDate startYear = DateUtil.parseDate(startDate.getYear() + " 01-01");
        LocalDate endYear = DateUtil.parseDate(endDate.getYear() + " 12-31");
        LocalDate[] monthDates = new LocalDate[]{startMonth, endMonth};
        LocalDate[] dayDates = new LocalDate[]{startDate, endDate};
        LocalDate[] yearDates = new LocalDate[]{startYear, endYear};
        List<CollectDispatchcarBO> collectDispatchcarBOS = new ArrayList<>();
        Set<String> date = new TreeSet<>();
        for (DispatchCarInfo dispatchCarInfo : dispatchCarInfos) {
            DispatchCarInfoDTO dispatchCarInfoDTO = new DispatchCarInfoDTO();
            CollectDispatchcarBO collectDispatchcarBO = new CollectDispatchcarBO("", "", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
            List<DispatchCarInfo> dispatchCarInfos1 = new ArrayList<>();
            if (dto.getCollectDispatchcarType() != null) {
                switch (dto.getCollectDispatchcarType()) {
                    case DRIVER:
                        dispatchCarInfoDTO.getConditions().add(Restrict.eq("driver", dispatchCarInfo.getDriver()));
                        if (dto.getCollectDateType() != null) {
                            switch (dto.getCollectDateType()) {
                                case DAY:
                                    dispatchCarInfoDTO.getConditions().add(Restrict.between("dispatchDate", dayDates));
                                    dispatchCarInfos1 = super.findByCis(dispatchCarInfoDTO);
                                    //排除重复的日期
                                    for (DispatchCarInfo dispatchCarInfo1 : dispatchCarInfos1) {
                                        date.add(dispatchCarInfo1.getDispatchDate().toString());
                                    }
                                    for (String date1 : date) {
                                        DispatchCarInfoDTO dispatchCarInfoDTO1 = new DispatchCarInfoDTO();
                                        dispatchCarInfoDTO1.getConditions().add(Restrict.eq("dispatchDate", DateUtil.parseDate(date1)));
                                        dispatchCarInfoDTO1.getConditions().add(Restrict.eq("driver", dispatchCarInfo.getDriver()));
                                        List<DispatchCarInfo> dispatchCarInfos2 = super.findByCis(dispatchCarInfoDTO1);
                                        collectDispatch(dispatchCarInfos2, collectDispatchcarBO);
                                        collectDispatchcarBO.setName(dispatchCarInfo.getDriver());
                                        collectDispatchcarBO.setDate(dispatchCarInfo.getDispatchDate().toString());
                                    }
                                    break;
                                case MONTH:
                                    dispatchCarInfoDTO.getConditions().add(Restrict.between("dispatchDate", monthDates));
                                    dispatchCarInfos1 = super.findByCis(dispatchCarInfoDTO);
                                    //排除重复的日期
                                    for (DispatchCarInfo dispatchCarInfo1 : dispatchCarInfos1) {
                                        String dispatchDate = dispatchCarInfo1.getDispatchDate().toString();
                                        date.add(dispatchDate.substring(0, dispatchDate.length() - 3));
                                    }
                                    for (String date1 : date) {
                                        String[] files = new String[]{"driver", "dispatchDate", "carRentalCost", "overWorkTime", "overWorkCost", "parkCost", "roadCost", "mealCost", "addOilAmountMoney"};
                                        StringBuilder sql = new StringBuilder("SELECT dispatchDate,carRentalCost,overWorkTime,overWorkCost,parkCost,roadCost,mealCost,addOilAmountMoney FROM dispatchcar_basicinfo" + "WHERE dispatchDate = TO_Date('" + date1 + "','YYYY-MM') and driver = '" + dispatchCarInfo.getDriver() + "'");
                                        List<DispatchCarInfo> dispatchCarInfos2 = super.findBySql(sql.toString(), DispatchCarInfo.class, files);
                                        collectDispatch(dispatchCarInfos2, collectDispatchcarBO);
                                        collectDispatchcarBO.setName(dispatchCarInfo.getDriver());
                                        collectDispatchcarBO.setDate(date1.substring(0, date1.length() - 3) + "年" + Integer.valueOf(date1.substring(date1.length() - 2, date1.length())) + "月");
                                    }
                                    break;
                                case YEAR:
                                    dispatchCarInfoDTO.getConditions().add(Restrict.between("dispatchDate", yearDates));
                                    dispatchCarInfos1 = super.findByCis(dispatchCarInfoDTO);
                                    //排除重复的日期
                                    for (DispatchCarInfo dispatchCarInfo1 : dispatchCarInfos1) {
                                        String dispatchDate = dispatchCarInfo1.getDispatchDate().toString();
                                        date.add(dispatchDate.substring(0, dispatchDate.length() - 6));
                                    }
                                    for (String date1 : date) {
                                        String[] files = new String[]{"driver", "dispatchDate", "carRentalCost", "overWorkTime", "overWorkCost", "parkCost", "roadCost", "mealCost", "addOilAmountMoney"};
                                        StringBuilder sql = new StringBuilder("SELECT dispatchDate,carRentalCost,overWorkTime,overWorkCost,parkCost,roadCost,mealCost,addOilAmountMoney FROM dispatchcar_basicinfo" + "WHERE dispatchDate between '" + date1 + "/01/01 00:00:00 and '" + date1 + "/12/31 23:59:59' and driver = '" + dispatchCarInfo.getDriver() + "'");
                                        List<DispatchCarInfo> dispatchCarInfos2 = super.findBySql(sql.toString(), DispatchCarInfo.class, files);
                                        collectDispatch(dispatchCarInfos2, collectDispatchcarBO);
                                        collectDispatchcarBO.setName(dispatchCarInfo.getDriver());
                                        collectDispatchcarBO.setDate(date1 + "年");
                                    }
                                    break;
                            }
                            collectDispatchcarBOS.add(collectDispatchcarBO);
                        }
                        break;
                    case AREA:
                        dispatchCarInfoDTO.getConditions().add(Restrict.eq("area", dispatchCarInfo.getArea()));
                        if (dto.getCollectDateType() != null) {
                            switch (dto.getCollectDateType()) {
                                case DAY:
                                    dispatchCarInfoDTO.getConditions().add(Restrict.between("dispatchDate", dayDates));
                                    dispatchCarInfos1 = super.findByCis(dispatchCarInfoDTO);
                                    //排除重复的日期
                                    for (DispatchCarInfo dispatchCarInfo1 : dispatchCarInfos1) {
                                        date.add(dispatchCarInfo1.getDispatchDate().toString());
                                    }
                                    for (String date1 : date) {
                                        DispatchCarInfoDTO dispatchCarInfoDTO1 = new DispatchCarInfoDTO();
                                        dispatchCarInfoDTO1.getConditions().add(Restrict.eq("dispatchDate", DateUtil.parseDate(date1)));
                                        dispatchCarInfoDTO1.getConditions().add(Restrict.eq("area", dispatchCarInfo.getArea()));
                                        List<DispatchCarInfo> dispatchCarInfos2 = super.findByCis(dispatchCarInfoDTO1);
                                        collectDispatch(dispatchCarInfos2, collectDispatchcarBO);
                                        collectDispatchcarBO.setName(dispatchCarInfo.getArea());
                                        collectDispatchcarBO.setDate(dispatchCarInfo.getDispatchDate().toString());
                                    }
                                    break;
                                case MONTH:
                                    dispatchCarInfoDTO.getConditions().add(Restrict.between("dispatchDate", monthDates));
                                    dispatchCarInfos1 = super.findByCis(dispatchCarInfoDTO);
                                    //排除重复的日期
                                    for (DispatchCarInfo dispatchCarInfo1 : dispatchCarInfos1) {
                                        String dispatchDate = dispatchCarInfo1.getDispatchDate().toString();
                                        date.add(dispatchDate.substring(0, dispatchDate.length() - 3));
                                    }
                                    for (String date1 : date) {
                                        String[] files = new String[]{"driver", "dispatchDate", "carRentalCost", "overWorkTime", "overWorkCost", "parkCost", "roadCost", "mealCost", "addOilAmountMoney"};
                                        StringBuilder sql = new StringBuilder("SELECT dispatchDate,carRentalCost,overWorkTime,overWorkCost,parkCost,roadCost,mealCost,addOilAmountMoney FROM dispatchcar_basicinfo" + "WHERE dispatchDate = TO_Date('" + date1 + "','YYYY-MM') and area = '" + dispatchCarInfo.getArea() + "'");
                                        List<DispatchCarInfo> dispatchCarInfos2 = super.findBySql(sql.toString(), DispatchCarInfo.class, files);
                                        collectDispatch(dispatchCarInfos2, collectDispatchcarBO);
                                        collectDispatchcarBO.setName(dispatchCarInfo.getArea());
                                        collectDispatchcarBO.setDate(date1.substring(0, date1.length() - 3) + "年" + Integer.valueOf(date1.substring(date1.length() - 2, date1.length())) + "月");
                                    }
                                    break;
                                case YEAR:
                                    dispatchCarInfoDTO.getConditions().add(Restrict.between("dispatchDate", yearDates));
                                    dispatchCarInfos1 = super.findByCis(dispatchCarInfoDTO);
                                    //排除重复的日期
                                    for (DispatchCarInfo dispatchCarInfo1 : dispatchCarInfos1) {
                                        String dispatchDate = dispatchCarInfo1.getDispatchDate().toString();
                                        date.add(dispatchDate.substring(0, dispatchDate.length() - 6));
                                    }
                                    for (String date1 : date) {
                                        String[] files = new String[]{"driver", "dispatchDate", "carRentalCost", "overWorkTime", "overWorkCost", "parkCost", "roadCost", "mealCost", "addOilAmountMoney"};
                                        StringBuilder sql = new StringBuilder("SELECT dispatchDate,carRentalCost,overWorkTime,overWorkCost,parkCost,roadCost,mealCost,addOilAmountMoney FROM dispatchcar_basicinfo dispatchcar" + "WHERE dispatchDate between '" + date1 + "/01/01 00:00:00 and '" + date1 + "/12/31 23:59:59' and area = '" + dispatchCarInfo.getArea() + "'");
                                        List<DispatchCarInfo> dispatchCarInfos2 = super.findBySql(sql.toString(), DispatchCarInfo.class, files);
                                        collectDispatch(dispatchCarInfos2, collectDispatchcarBO);
                                        collectDispatchcarBO.setName(dispatchCarInfo.getArea());
                                        collectDispatchcarBO.setDate(date1 + "年");
                                    }
                                    break;
                            }
                            collectDispatchcarBOS.add(collectDispatchcarBO);
                        }
                        break;
                    case PROJECT:
                        dispatchCarInfoDTO.getConditions().add(Restrict.eq("project", dispatchCarInfo.getProject()));
                        if (dto.getCollectDateType() != null) {
                            switch (dto.getCollectDateType()) {
                                case DAY:
                                    dispatchCarInfoDTO.getConditions().add(Restrict.between("dispatchDate", dayDates));
                                    dispatchCarInfos1 = super.findByCis(dispatchCarInfoDTO);
                                    //排除重复的日期
                                    for (DispatchCarInfo dispatchCarInfo1 : dispatchCarInfos1) {
                                        date.add(dispatchCarInfo1.getDispatchDate().toString());
                                    }
                                    for (String date1 : date) {
                                        DispatchCarInfoDTO dispatchCarInfoDTO1 = new DispatchCarInfoDTO();
                                        dispatchCarInfoDTO1.getConditions().add(Restrict.eq("dispatchDate", DateUtil.parseDate(date1)));
                                        dispatchCarInfoDTO1.getConditions().add(Restrict.eq("project", dispatchCarInfo.getProject()));
                                        List<DispatchCarInfo> dispatchCarInfos2 = super.findByCis(dispatchCarInfoDTO1);
                                        collectDispatch(dispatchCarInfos2, collectDispatchcarBO);
                                        collectDispatchcarBO.setName(dispatchCarInfo.getProject());
                                        collectDispatchcarBO.setDate(dispatchCarInfo.getDispatchDate().toString());
                                    }
                                    break;
                                case MONTH:
                                    dispatchCarInfoDTO.getConditions().add(Restrict.between("dispatchDate", monthDates));
                                    dispatchCarInfos1 = super.findByCis(dispatchCarInfoDTO);
                                    //排除重复的日期
                                    for (DispatchCarInfo dispatchCarInfo1 : dispatchCarInfos1) {
                                        String dispatchDate = dispatchCarInfo1.getDispatchDate().toString();
                                        date.add(dispatchDate.substring(0, dispatchDate.length() - 3));
                                    }
                                    for (String date1 : date) {
                                        String[] files = new String[]{"driver", "dispatchDate", "carRentalCost", "overWorkTime", "overWorkCost", "parkCost", "roadCost", "mealCost", "addOilAmountMoney"};
                                        StringBuilder sql = new StringBuilder("SELECT dispatchDate,carRentalCost,overWorkTime,overWorkCost,parkCost,roadCost,mealCost,addOilAmountMoney FROM dispatchcar_basicinfo" + "WHERE dispatchDate = TO_Date('" + date1 + "','YYYY-MM') and project = '" + dispatchCarInfo.getProject() + "'");
                                        List<DispatchCarInfo> dispatchCarInfos2 = super.findBySql(sql.toString(), DispatchCarInfo.class, files);
                                        collectDispatch(dispatchCarInfos2, collectDispatchcarBO);
                                        collectDispatchcarBO.setName(dispatchCarInfo.getProject());
                                        collectDispatchcarBO.setDate(date1.substring(0, date1.length() - 3) + "年" + Integer.valueOf(date1.substring(date1.length() - 2, date1.length())) + "月");
                                    }
                                    break;
                                case YEAR:
                                    dispatchCarInfoDTO.getConditions().add(Restrict.between("dispatchDate", yearDates));
                                    dispatchCarInfos1 = super.findByCis(dispatchCarInfoDTO);
                                    //排除重复的日期
                                    for (DispatchCarInfo dispatchCarInfo1 : dispatchCarInfos1) {
                                        String dispatchDate = dispatchCarInfo1.getDispatchDate().toString();
                                        date.add(dispatchDate.substring(0, dispatchDate.length() - 6));
                                    }
                                    for (String date1 : date) {
                                        String[] files = new String[]{"driver", "dispatchDate", "carRentalCost", "overWorkTime", "overWorkCost", "parkCost", "roadCost", "mealCost", "addOilAmountMoney"};
                                        StringBuilder sql = new StringBuilder("SELECT dispatchDate,carRentalCost,overWorkTime,overWorkCost,parkCost,roadCost,mealCost,addOilAmountMoney FROM dispatchcar_basicinfo" + "WHERE dispatchDate between '" + date1 + "/01/01 00:00:00 and '" + date1 + "/12/31 23:59:59' and project = '" + dispatchCarInfo.getProject() + "'");
                                        List<DispatchCarInfo> dispatchCarInfos2 = super.findBySql(sql.toString(), DispatchCarInfo.class, files);
                                        collectDispatch(dispatchCarInfos2, collectDispatchcarBO);
                                        collectDispatchcarBO.setName(dispatchCarInfo.getProject());
                                        collectDispatchcarBO.setDate(date1 + "年");
                                    }
                                    break;
                            }
                            collectDispatchcarBOS.add(collectDispatchcarBO);
                        }
                        break;
                }
            }

        }
        return collectDispatchcarBOS;
    }

    public void collectDispatch(List<DispatchCarInfo> dispatchCarInfos1, CollectDispatchcarBO collectDispatchcarBO) throws SerException {

        Double rentcarUnitCost = 0.0;
        Double overTimeWork = 0.0;
        Double overTimeWorkPrice = 0.0;
        Double overTimeWorkUnitCost = 0.0;
        Double parkCost = 0.0;
        Double roadCost = 0.0;
        Double mealCost = 0.0;
        Double totalOne = 0.0;
        Double addOilAmountMoney = 0.0;
        Double totalTwo = 0.0;

        rentcarUnitCost = dispatchCarInfos1.stream().filter(p -> p.getCarRentalCost() != null).mapToDouble(p -> p.getCarRentalCost()).sum();
        overTimeWork = dispatchCarInfos1.stream().filter(p -> p.getOverWorkTime() != null).mapToDouble(p -> p.getOverWorkTime()).sum();
        overTimeWorkPrice = dispatchCarInfos1.stream().filter(p -> p.getOverWorkCost() != null).mapToDouble(p -> p.getOverWorkCost()).sum();
        overTimeWorkUnitCost = overTimeWorkPrice / overTimeWork;
        parkCost = dispatchCarInfos1.stream().filter(p -> p.getParkCost() != null).mapToDouble(p -> p.getParkCost()).sum();
        roadCost = dispatchCarInfos1.stream().filter(p -> p.getRoadCost() != null).mapToDouble(p -> p.getRoadCost()).sum();
        mealCost = dispatchCarInfos1.stream().filter(p -> p.getMealCost() != null).mapToDouble(p -> p.getMealCost()).sum();
        totalOne = rentcarUnitCost + overTimeWorkPrice + parkCost + roadCost + mealCost;
        addOilAmountMoney = dispatchCarInfos1.stream().filter(p -> p.getAddOilAmountMoney() != null).mapToDouble(p -> p.getAddOilAmountMoney()).sum();
        totalTwo = totalOne + addOilAmountMoney;

        collectDispatchcarBO.setRentcarUnitCost(rentcarUnitCost);
        collectDispatchcarBO.setOverTimeWork(overTimeWork);
        collectDispatchcarBO.setOverTimeWorkPriceUnitCost(overTimeWorkUnitCost);
        collectDispatchcarBO.setOverTimeWorkPrice(overTimeWorkPrice);
        collectDispatchcarBO.setParkCost(parkCost);
        collectDispatchcarBO.setRoadCost(roadCost);
        collectDispatchcarBO.setMealCost(mealCost);
        collectDispatchcarBO.setTotalOne(totalOne);
        collectDispatchcarBO.setAddOilAmountMoney(addOilAmountMoney);
        collectDispatchcarBO.setTotalTwo(totalTwo);
    }
}