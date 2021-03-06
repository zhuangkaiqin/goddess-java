package com.bjike.goddess.marketactivitymanage.action.marketactivitymanage;

import com.bjike.goddess.assemble.api.ModuleAPI;
import com.bjike.goddess.common.api.entity.ADD;
import com.bjike.goddess.common.api.entity.EDIT;
import com.bjike.goddess.common.api.exception.ActException;
import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.api.restful.Result;
import com.bjike.goddess.common.consumer.action.BaseFileAction;
import com.bjike.goddess.common.consumer.interceptor.login.LoginAuth;
import com.bjike.goddess.common.consumer.restful.ActResult;
import com.bjike.goddess.common.utils.bean.BeanTransform;
import com.bjike.goddess.common.utils.excel.Excel;
import com.bjike.goddess.common.utils.excel.ExcelUtil;
import com.bjike.goddess.market.api.MarketInfoAPI;
import com.bjike.goddess.marketactivitymanage.api.CustomerInfoAPI;
import com.bjike.goddess.marketactivitymanage.api.MarketServeApplyAPI;
import com.bjike.goddess.marketactivitymanage.bo.CustomerInfoBO;
import com.bjike.goddess.marketactivitymanage.bo.MarketServeApplyBO;
import com.bjike.goddess.marketactivitymanage.bo.MarketServeApplyDetailBO;
import com.bjike.goddess.marketactivitymanage.dto.MarketServeApplyDTO;
import com.bjike.goddess.marketactivitymanage.excel.MarketServeApplyImprotExcel;
import com.bjike.goddess.marketactivitymanage.to.*;
import com.bjike.goddess.marketactivitymanage.type.AuditType;
import com.bjike.goddess.marketactivitymanage.vo.CustomerInfoVO;
import com.bjike.goddess.marketactivitymanage.vo.MarketServeApplyDetailVO;
import com.bjike.goddess.marketactivitymanage.vo.MarketServeApplyVO;
import com.bjike.goddess.projectmarketfee.api.CostAnalysisAPI;
import com.bjike.goddess.storage.api.FileAPI;
import com.bjike.goddess.storage.to.FileInfo;
import com.bjike.goddess.storage.vo.FileVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 市场招待申请
 *
 * @Author: [ sunfengtao ]
 * @Date: [ 2017-03-18T10:37:08.048 ]
 * @Description: [ 市场招待申请 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@RestController
@RequestMapping("marketserveapply")
public class MarketServeApplyAct extends BaseFileAction {

    @Autowired
    private MarketServeApplyAPI marketServeApplyAPI;

    @Autowired
    private CustomerInfoAPI customerInfoAPI;
    @Autowired
    private MarketInfoAPI marketInfoAPI;
    @Autowired
    private ModuleAPI moduleAPI;
    @Autowired
    private CostAnalysisAPI costAnalysisAPI;
    @Autowired
    private FileAPI fileAPI;

    /**
     * 功能导航权限
     * @param guidePermissionTO 导航类型数据
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/guidePermission")
    public Result guidePermission(@Validated(GuidePermissionTO.TestAdd.class) GuidePermissionTO guidePermissionTO, BindingResult bindingResult, HttpServletRequest request) throws ActException {
        try {

            Boolean isHasPermission = marketServeApplyAPI.guidePermission(guidePermissionTO);
            if(! isHasPermission ){
                //int code, String msg
                return new ActResult(0,"没有权限",false );
            }else{
                return new ActResult(0,"有权限",true );
            }
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }
    /**
     * 根据id查询市场招待申请
     *
     * @param id 市场招待申请唯一标识
     * @return class MarketServeApplyVO
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/marketserveapply/{id}")
    public Result findById(@PathVariable String id, HttpServletRequest request) throws ActException {
        try {
            MarketServeApplyBO bo = marketServeApplyAPI.getOne(id);
            MarketServeApplyVO vo = BeanTransform.copyProperties(bo, MarketServeApplyVO.class, request);
            return ActResult.initialize(vo);
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 计算总数量
     *
     * @param dto 市场招待申请dto
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/count")
    public Result count(@Validated MarketServeApplyDTO dto, BindingResult result) throws ActException {
        try {
            Long count = marketServeApplyAPI.count(dto);
            return ActResult.initialize(count);
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 获取列表
     *
     * @param dto 市场招待申请dto
     * @return class MarketServeApplyVO
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/list")
    public Result list(@Validated MarketServeApplyDTO dto, BindingResult result, HttpServletRequest request) throws ActException {
        try {
            List<MarketServeApplyBO> boList = marketServeApplyAPI.list(dto);
            List<MarketServeApplyVO> voList = BeanTransform.copyProperties(boList, MarketServeApplyVO.class, request);
            return ActResult.initialize(voList);
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 根据id查询市场招待申请详情
     *
     * @param id
     * @return class MarketServeApplyVO
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/findDetailById/{id}")
    public Result findDetailById(@PathVariable String id, HttpServletRequest request) throws ActException {
        try {
            MarketServeApplyDetailBO bo = marketServeApplyAPI.checkDetails(id);
            MarketServeApplyDetailVO vo = BeanTransform.copyProperties(bo, MarketServeApplyDetailVO.class, request);
            return ActResult.initialize(vo);
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 添加市场招待申请
     *
     * @param to 市场招待申请to信息
     * @return class MarketServeApplyVO
     * @throws ActException
     * @version v1
     */
    @LoginAuth
    @PostMapping("v1/add")
    public Result add(@Validated(value = {ADD.class}) MarketServeApplyTO to, HttpServletRequest request, BindingResult result) throws ActException {
        try {
            MarketServeApplyBO bo = marketServeApplyAPI.save(to);
            MarketServeApplyVO vo = BeanTransform.copyProperties(bo, MarketServeApplyVO.class, request);
            return ActResult.initialize(vo);
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 根据id删除市场招待申请
     *
     * @param id 市场招待申请唯一标识
     * @throws ActException
     * @version v1
     */
    @LoginAuth
    @DeleteMapping("v1/delete/{id}")
    public Result delete(@PathVariable String id) throws ActException {
        try {
            marketServeApplyAPI.remove(id);
            return new ActResult("delete success!");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 编辑市场招待申请
     *
     * @param to 市场招待申请to信息
     * @throws ActException
     * @version v1
     */
    @LoginAuth
    @PutMapping("v1/edit")
    public Result edit(@Validated(value = {EDIT.class}) MarketServeApplyTO to, BindingResult result) throws ActException {
        try {
            marketServeApplyAPI.update(to);
            return new ActResult("edit success!");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 运营商务部资金模块意见
     *
     * @param id                市场招待申请唯一标识
     * @param fundModuleOpinion 运营商务部资金模块意见
     * @throws ActException
     * @version v1
     */
    @LoginAuth
    @PutMapping("v1/fundModuleOpinion/{id}")
    public Result fundModuleOpinion(@PathVariable String id, @RequestParam(value = "fundModuleOpinion") String fundModuleOpinion) throws ActException {
        try {
            marketServeApplyAPI.fundModuleOpinion(id, fundModuleOpinion);
            return new ActResult("fundModuleOpinion success!");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 决策层审核意见
     *
     * @param id                    市场招待申请唯一标识
     * @param executiveAuditOpinion 决策层审核意见
     * @throws ActException
     * @version v1
     */
    @LoginAuth
    @PutMapping("v1/executiveOpinion/{id}")
    public Result executiveOpinion(@PathVariable String id, @RequestParam(value = "executiveAuditOpinion") AuditType executiveAuditOpinion) throws ActException {
        try {
            marketServeApplyAPI.executiveOpinion(id, executiveAuditOpinion);
            return new ActResult("executiveOpinion success!");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 添加客户信息
     *
     * @param to 客户信息to
     * @throws ActException
     * @version v1
     */
    @LoginAuth
    @PostMapping("v1/addCustomerInfo")
    public Result addClientInfo(@Validated({ADD.class}) CustomerInfoTO to, BindingResult result) throws ActException {
        try {
            marketServeApplyAPI.addClientInfo(to);
            return ActResult.initialize("addcustomerinfo success!");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 编辑客户信息
     *
     * @param to 客户信息to
     * @throws ActException
     * @version v1
     */
    @LoginAuth
    @PostMapping("v1/editCustomerInfo")
    public Result editClientInfo(@Validated(value = {CustomerInfoTO.EditCustomer.class}) CustomerInfoTO to, BindingResult result) throws ActException {
        try {
            marketServeApplyAPI.editClientInfo(to);
            return ActResult.initialize("editClientInfo success!");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }


    /**
     * 查看客户信息
     *
     * @param id 市场活动申请唯一标识
     * @return class CustomerInfoVO
     * @throws ActException
     * @version v1
     */
    @LoginAuth
    @GetMapping("v1/checkCustomerInfo/{id}")
    public Result checkCustomerInfo(@PathVariable String id, HttpServletRequest request) throws ActException {
        try {
            List<CustomerInfoBO> listBO = customerInfoAPI.findByMarketServeId(id);
            List<CustomerInfoVO> listVO = BeanTransform.copyProperties(listBO, CustomerInfoVO.class, request);
            return ActResult.initialize(listVO);
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 上传附件
     *
     * @version v1
     */
    @LoginAuth
    @PostMapping("v1/uploadFile/{id}")
    public Result uploadFile(@PathVariable String id, HttpServletRequest request) throws ActException {
        try {
            //跟前端约定好 ，文件路径是列表id
            // /id/....
            String path = "/" + id;
            List<InputStream> inputStreams = getInputStreams(request, path);
            fileAPI.upload(inputStreams);
            return new ActResult("upload success");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 文件附件列表
     *
     * @param id id
     * @return class FileVO
     * @version v1
     */
    @GetMapping("v1/listFile/{id}")
    public Result list(@PathVariable String id, HttpServletRequest request) throws ActException {
        try {
            //跟前端约定好 ，文件路径是列表id
            String path = "/" + id;
            FileInfo fileInfo = new FileInfo();
            fileInfo.setPath(path);
            Object storageToken = request.getAttribute("storageToken");
            fileInfo.setStorageToken(storageToken.toString());
            List<FileVO> files = BeanTransform.copyProperties(fileAPI.list(fileInfo), FileVO.class);
            return ActResult.initialize(files);
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 文件下载
     *
     * @param path 文件路径
     * @version v1
     */
    @GetMapping("v1/downloadFile")
    public Result download(@RequestParam String path, HttpServletRequest request, HttpServletResponse response) throws ActException {
        try {
            //该文件的路径
            FileInfo fileInfo = new FileInfo();
            Object storageToken = request.getAttribute("storageToken");
            fileInfo.setStorageToken(storageToken.toString());
            fileInfo.setPath(path);
            String filename = StringUtils.substringAfterLast(fileInfo.getPath(), "/");
            byte[] buffer = fileAPI.download(fileInfo);
            writeOutFile(response, buffer, filename);
            return new ActResult("download success");
        } catch (Exception e) {
            throw new ActException(e.getMessage());
        }

    }

    /**
     * 删除文件或文件夹
     *
     * @param siginManageDeleteFileTO 多文件信息路径
     * @version v1
     */
    @LoginAuth
    @PostMapping("v1/deleteFile")
    public Result delFile(@Validated(SiginManageDeleteFileTO.TestDEL.class) SiginManageDeleteFileTO siginManageDeleteFileTO, HttpServletRequest request) throws SerException {
        if (null != siginManageDeleteFileTO.getPaths() && siginManageDeleteFileTO.getPaths().length >= 0) {
            Object storageToken = request.getAttribute("storageToken");
            fileAPI.delFile(storageToken.toString(), siginManageDeleteFileTO.getPaths());
        }
        return new ActResult("delFile success");
    }

    /**
     * 导出Excel
     *
     * @param areas 地区
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @version v1
     */
    @LoginAuth
    @GetMapping("v1/exportExcel")
    public Result exportExcel(String[] areas, @RequestParam String startTime, @RequestParam String endTime, HttpServletResponse response) throws ActException {
        try {
            String fileName = "市场活动申请.xlsx";
            super.writeOutFile(response, marketServeApplyAPI.exportExcel(areas, startTime, endTime), fileName);
            return new ActResult("导出成功");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        } catch (IOException e1) {
            throw new ActException(e1.getMessage());
        }
    }

    private Boolean convertWhetherTemporaryServe(String WhetherTemporaryServe) throws ActException {
        Boolean bool = true;
        if (null == WhetherTemporaryServe) {
            throw new ActException("临时招待状态填写不正确,导入失败,正确填写方式（是/否）");
        } else {
            if (WhetherTemporaryServe.equals("是")) {
                bool = true;
            } else if (WhetherTemporaryServe.equals("否")) {
                bool = false;
            } else {
                throw new ActException("临时招待状态填写不正确,导入失败,正确填写方式（是/否）");
            }
        }
        return bool;
    }

    private AuditType convertExecutiveAuditOpinion(String type) throws ActException {
        AuditType status;
        if (null != type) {
            switch (type) {
                case "未通过":
                    status = AuditType.NONE;
                    break;
                case "通过":
                    status = AuditType.ALLOWED;
                    break;
                case "拒绝":
                    status = AuditType.DENIED;
                default:
                    throw new ActException("审核意见填写不正确,导入失败,正确填写方式（未通过/通过/拒绝）");
            }
            return status;
        } else {
            return null;
        }
    }


    /**
     * 导入Excel
     *
     * @param request 注入HttpServletRequest对象
     * @version v1
     */
    @LoginAuth
    @PostMapping("v1/importExcel")
    public Result importExcel(HttpServletRequest request) throws ActException {
        try {
            List<InputStream> inputStreams = super.getInputStreams(request);
            InputStream is = inputStreams.get(1);
            Excel excel = new Excel(0, 1);
            List<MarketServeApplyImprotExcel> tos = ExcelUtil.excelToClazz(is, MarketServeApplyImprotExcel.class, excel);
            List<MarketServeApplyImprotTO> tocs = new ArrayList<>();
            for (MarketServeApplyImprotExcel str : tos) {
                MarketServeApplyImprotTO marketServeApplyImprotTO = BeanTransform.copyProperties(str, MarketServeApplyImprotTO.class, "planActivityTiming", "whetherTemporaryServe", "executiveAuditOpinion");
                marketServeApplyImprotTO.setPlanActivityTiming(String.valueOf(str.getPlanActivityTiming()).replace("T", " ").substring(0, 19));
                marketServeApplyImprotTO.setWhetherTemporaryServe(convertWhetherTemporaryServe(str.getWhetherTemporaryServe()));
                marketServeApplyImprotTO.setExecutiveAuditOpinion(convertExecutiveAuditOpinion(str.getExecutiveAuditOpinion()));
                tocs.add(marketServeApplyImprotTO);
            }
            //注意序列化
            marketServeApplyAPI.importExcel(tocs);
            return new ActResult("导入成功");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * excel模板下载
     *
     * @des 下载市场活动申请模板
     * @version v1
     */
    @GetMapping("v1/templateExport")
    public Result templateExport(HttpServletResponse response) throws ActException {
        try {
            String fileName = "市场活动申请模板.xlsx";
            super.writeOutFile(response, marketServeApplyAPI.templateExport(), fileName);
            return new ActResult("导出成功");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        } catch (IOException e1) {
            throw new ActException(e1.getMessage());
        }
    }

    /**
     * 查询所有市场活动申请的地区
     *
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/findApplyAreas")
    public Result findApplyAreas() throws ActException {
        try {
            List<String> areas = new ArrayList<>();
            areas = marketServeApplyAPI.findAllAreas();
            return ActResult.initialize(areas);
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }
    /**
     * 添加编辑中项目名称下拉值
     *
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/findMarket/projectname")
    public Result findMarketPname() throws ActException {
        try {
            List<String> projectName = new ArrayList<>();
            if(moduleAPI.isCheck("market")){
                projectName = marketInfoAPI.getProjectName();
            }
            return ActResult.initialize(projectName);
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }
    /**
     * 添加编辑功能的预计费用下拉值
     *
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/findProject/predictCharge")
    public Result findProjectPcharge() throws ActException {
        try {
            List<Double> predictCharge = new ArrayList<>();
            if(moduleAPI.isCheck("projectmarketfee")){
                predictCharge = costAnalysisAPI.allExMarketCost();
            }
            return ActResult.initialize(predictCharge);
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }
}