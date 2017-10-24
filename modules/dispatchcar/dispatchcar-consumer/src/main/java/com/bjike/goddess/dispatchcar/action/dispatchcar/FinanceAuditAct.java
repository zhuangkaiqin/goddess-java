package com.bjike.goddess.dispatchcar.action.dispatchcar;

import com.bjike.goddess.common.api.dto.Restrict;
import com.bjike.goddess.common.api.entity.ADD;
import com.bjike.goddess.common.api.exception.ActException;
import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.api.restful.Result;
import com.bjike.goddess.common.consumer.action.BaseFileAction;
import com.bjike.goddess.common.consumer.restful.ActResult;
import com.bjike.goddess.common.utils.bean.BeanTransform;
import com.bjike.goddess.dispatchcar.api.DispatchCarInfoAPI;
import com.bjike.goddess.dispatchcar.dto.DispatchCarInfoDTO;
import com.bjike.goddess.dispatchcar.enums.FindType;
import com.bjike.goddess.dispatchcar.to.CheckChangeCarTO;
import com.bjike.goddess.dispatchcar.to.DispatchCarInfoTO;
import com.bjike.goddess.dispatchcar.vo.AuditDetailVO;
import com.bjike.goddess.dispatchcar.vo.AuditResultVO;
import com.bjike.goddess.dispatchcar.vo.DispatchCarInfoVO;
import com.bjike.goddess.storage.api.FileAPI;
import com.bjike.goddess.storage.to.FileInfo;
import com.bjike.goddess.storage.vo.FileVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 账务核对
 *
 * @Author: [Jason]
 * @Date: [17-4-14 上午11:53]
 * @Description: []
 * @Version: [1.0.0]
 * @Copy: [com.bjike]
 */
@RestController
@RequestMapping("finance")
public class FinanceAuditAct extends BaseFileAction{

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
            dto.getConditions().add(Restrict.eq("findType",FindType.FINANCEAUDIT));
            List<DispatchCarInfoVO> voList = BeanTransform.copyProperties(dispatchCarInfoAPI.pageList(dto), DispatchCarInfoVO.class, request);
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
     * 根据id查询出车记录
     *
     * @param id 出车记录id
     * @return class DispatchCarInfoVO
     * @version v1
     */
    @GetMapping("v1/find/{id}")
    public Result findById(@PathVariable String id, HttpServletRequest request) throws ActException {
        try {
            DispatchCarInfoVO vo = BeanTransform.copyProperties(dispatchCarInfoAPI.findDetail(id), DispatchCarInfoVO.class, request);
            return ActResult.initialize(vo);
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 查询总记录数
     *
     * @param dto 查询条件
     * @version v1
     */
    @GetMapping("v1/count")
    public Result count(DispatchCarInfoDTO dto) throws ActException {
        try {
            dto.getConditions().add(Restrict.eq("findType", FindType.FINANCEAUDIT));
            Long count = dispatchCarInfoAPI.count(dto);
            return ActResult.initialize(count);
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 收到票据
     * @param id 账务核对id
     * @param isCorrect 核对依据是否无误
     * @throws ActException
     * @version v1
     */
    @PostMapping("v1/receive")
    public Result receivePaper(@RequestParam String id,@RequestParam Boolean isCorrect) throws ActException{
        try {
            dispatchCarInfoAPI.receivePaper(id,isCorrect);
            return new ActResult("操作成功");
        }catch (SerException e){
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 核对分析
     * @param to 核对内容
     * @throws ActException
     * @version v1
     */
    @PostMapping("v1/financialSugg")
    public Result financialSugg(@Validated(ADD.class) CheckChangeCarTO to, @Validated(ADD.class) DispatchCarInfoTO dispatchCarInfoTO,HttpServletRequest request) throws ActException{
        try {
            dispatchCarInfoAPI.financialSugg(dispatchCarInfoTO,to);
            return new ActResult("核对成功");
        }catch (SerException e){
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

//
//    /**
//     * 单据审核
//     *
//     * @param id                 出车记录id
//     * @param auditReceiptSugg   审核意见
//     * @param receiveReceiptDate 签收日期
//     * @param auditReceiptResult 审核结果
//     * @version v1
//     */
//    @PostMapping("v1/receipt")
//    public Result receiptAudit(@RequestParam String id, @RequestParam String auditReceiptSugg, @RequestParam String receiveReceiptDate, @RequestParam Boolean auditReceiptResult) throws ActException {
//        try {
//            dispatchCarInfoAPI.receiptAudit(id, auditReceiptSugg, receiveReceiptDate, auditReceiptResult);
//            return new ActResult("审核成功");
//        } catch (SerException e) {
//            throw new ActException(e.getMessage());
//        }
//    }

}
