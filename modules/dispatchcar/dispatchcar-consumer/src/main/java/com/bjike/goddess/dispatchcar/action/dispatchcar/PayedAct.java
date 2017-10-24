package com.bjike.goddess.dispatchcar.action.dispatchcar;

import com.bjike.goddess.common.api.dto.Restrict;
import com.bjike.goddess.common.api.exception.ActException;
import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.api.restful.Result;
import com.bjike.goddess.common.consumer.action.BaseFileAction;
import com.bjike.goddess.common.consumer.interceptor.login.LoginAuth;
import com.bjike.goddess.common.consumer.restful.ActResult;
import com.bjike.goddess.common.utils.bean.BeanTransform;
import com.bjike.goddess.dispatchcar.api.DispatchCarInfoAPI;
import com.bjike.goddess.dispatchcar.bo.PayedCollectBO;
import com.bjike.goddess.dispatchcar.dto.DispatchCarInfoDTO;
import com.bjike.goddess.dispatchcar.enums.FindType;
import com.bjike.goddess.dispatchcar.to.DispatchcarDeleteFileTO;
import com.bjike.goddess.dispatchcar.to.ExportDispatchCarInfoTO;
import com.bjike.goddess.dispatchcar.vo.AuditResultVO;
import com.bjike.goddess.dispatchcar.vo.DispatchCarInfoVO;
import com.bjike.goddess.dispatchcar.vo.PayedCollectVO;
import com.bjike.goddess.storage.api.FileAPI;
import com.bjike.goddess.storage.to.FileInfo;
import com.bjike.goddess.storage.vo.FileVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 已付款记录
 *
 * @Author: [Jason]
 * @Date: [17-4-14 下午2:48]
 * @Description: []
 * @Version: [1.0.0]
 * @Copy: [com.bjike]
 */
@RestController
@RequestMapping("payed")
public class PayedAct extends BaseFileAction{

    @Autowired
    private DispatchCarInfoAPI dispatchCarInfoAPI;

    @Autowired
    private FileAPI fileAPI;

    /**
     * 列表分页查询
     *
     * @param dto 分页条件
     * @return class DispatchCarInfoVO
     * @version v1
     */
    @GetMapping("v1/list")
    public Result pageList(DispatchCarInfoDTO dto, HttpServletRequest request) throws ActException {
        try {
            dto.getConditions().add(Restrict.eq("findType", FindType.PAYED));
            List<DispatchCarInfoVO> voList = BeanTransform.copyProperties(dispatchCarInfoAPI.pageList(dto), DispatchCarInfoVO.class);
            return ActResult.initialize(voList);
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 审核详情
     *
     * @param id 出车记录id
     * @return class AuditResultVO
     * @version v1
     */
    @GetMapping("v1/audit/{id}")
    public Result findAudit(@PathVariable String id, HttpServletRequest request) throws ActException {
        try {
            List<AuditResultVO> voList = BeanTransform.copyProperties(dispatchCarInfoAPI.findAuditResult(id), AuditResultVO.class, request);
            return ActResult.initialize(voList);
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 上传附件
     *
     * @param request 附件内容
     * @param id      出车id
     * @version v1
     */
    @PostMapping("v1/upload/{id}")
    public Result fileUpload(@PathVariable String id, HttpServletRequest request) throws ActException {
        try {
            String path = "/dispatchcar/" + id;
            fileAPI.upload(this.getInputStreams(request, path.toString()));
            return new ActResult("上传成功");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 文件附件列表
     *
     * @param id id 列表id
     * @return class FileVO
     * @version v1
     */
    @GetMapping("v1/listFile/{id}")
    public Result list(@PathVariable String id, HttpServletRequest request) throws ActException {
        try {
            //跟前端约定好 ，文件路径是列表id
            String path = "/dispatchcar/" + id;
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
     * @param dispatchcarDeleteFileTO 多文件信息路径
     * @version v1
     */
    @LoginAuth
    @PostMapping("v1/deleteFile")
    public Result delFile(@Validated(DispatchcarDeleteFileTO.TestDEL.class) DispatchcarDeleteFileTO dispatchcarDeleteFileTO, HttpServletRequest request) throws SerException {
        if (null != dispatchcarDeleteFileTO.getPaths() && dispatchcarDeleteFileTO.getPaths().length >= 0) {
            Object storageToken = request.getAttribute("storageToken");
            fileAPI.delFile(storageToken.toString(), dispatchcarDeleteFileTO.getPaths());
        }
        return new ActResult("delFile success");
    }

    /**
     * 导出Excel
     *
     * @param to 导出条件
     * @version v1
     */
    @GetMapping("v1/exportExcel")
    public Result exportExcel(ExportDispatchCarInfoTO to, HttpServletResponse response) throws ActException {
        try {
            String fileName = "出车记录.xlsx";
            super.writeOutFile(response, dispatchCarInfoAPI.exportExcel(to), fileName);
            return new ActResult("导出成功");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        } catch (IOException e1) {
            throw new ActException(e1.getMessage());
        }
    }


    /**
     * 汇总
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return class PayedCollectVO
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/collectPayed")
    public Result collectPayed(@RequestParam String startTime,@RequestParam String endTime) throws ActException{
        try {
            List<PayedCollectBO> boList = dispatchCarInfoAPI.collectPayed(startTime,endTime);
            List<PayedCollectVO> voList = BeanTransform.copyProperties(boList,PayedCollectVO.class);
            return ActResult.initialize(voList);
        }catch (SerException e){
            throw new ActException(e.getMessage());
        }
    }



}
