package com.bjike.goddess.taskallotment.action.taskallotment;

import com.bjike.goddess.common.api.entity.ADD;
import com.bjike.goddess.common.api.entity.EDIT;
import com.bjike.goddess.common.api.exception.ActException;
import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.api.restful.Result;
import com.bjike.goddess.common.consumer.action.BaseFileAction;
import com.bjike.goddess.common.consumer.interceptor.login.LoginAuth;
import com.bjike.goddess.common.consumer.restful.ActResult;
import com.bjike.goddess.common.utils.bean.BeanTransform;
import com.bjike.goddess.organize.api.DepartmentDetailAPI;
import com.bjike.goddess.organize.api.PositionDetailUserAPI;
import com.bjike.goddess.storage.api.FileAPI;
import com.bjike.goddess.storage.to.FileInfo;
import com.bjike.goddess.storage.vo.FileVO;
import com.bjike.goddess.taskallotment.api.ProjectAPI;
import com.bjike.goddess.taskallotment.api.TableAPI;
import com.bjike.goddess.taskallotment.api.TaskNodeAPI;
import com.bjike.goddess.taskallotment.bo.*;
import com.bjike.goddess.taskallotment.dto.ProjectDTO;
import com.bjike.goddess.taskallotment.dto.TableDTO;
import com.bjike.goddess.taskallotment.dto.TaskNodeDTO;
import com.bjike.goddess.taskallotment.to.DeleteFileTO;
import com.bjike.goddess.taskallotment.to.GuidePermissionTO;
import com.bjike.goddess.taskallotment.to.TaskNodeTO;
import com.bjike.goddess.taskallotment.vo.*;
import com.bjike.goddess.user.api.UserAPI;
import com.bjike.goddess.user.bo.UserBO;
import com.bjike.goddess.user.vo.UserVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 任务节点
 *
 * @Author: [ chenjunhao ]
 * @Date: [ 2017-09-14 02:10 ]
 * @Description: [ 任务节点 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@RestController
@RequestMapping("tasknode")
public class TaskNodeAction extends BaseFileAction {
    @Autowired
    private TaskNodeAPI taskNodeAPI;
    @Autowired
    private FileAPI fileAPI;
    @Autowired
    private ProjectAPI projectAPI;
    @Autowired
    private TableAPI tableAPI;
    @Autowired
    private PositionDetailUserAPI positionDetailUserAPI;
    @Autowired
    private DepartmentDetailAPI departmentDetailAPI;
    @Autowired
    private UserAPI userAPI;

    /**
     * 功能导航权限
     *
     * @param guidePermissionTO 导航类型数据
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/guidePermission")
    public Result guidePermission(@Validated(GuidePermissionTO.TestAdd.class) GuidePermissionTO guidePermissionTO, BindingResult bindingResult, HttpServletRequest request) throws ActException {
        try {

            Boolean isHasPermission = taskNodeAPI.guidePermission(guidePermissionTO);
            if (!isHasPermission) {
                //int code, String msg
                return new ActResult(0, "没有权限", false);
            } else {
                return new ActResult(0, "有权限", true);
            }
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 列表
     *
     * @param dto 任务节点数据传输
     * @return class ProjectVO
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/list")
    public Result list(ProjectDTO dto, HttpServletRequest request) throws ActException {
        try {
            List<ProjectBO> list = taskNodeAPI.list(dto);
            return ActResult.initialize(BeanTransform.copyProperties(list, ProjectVO.class, request));
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 设置任务节点
     *
     * @param to 任务节点传输对象
     * @return class TaskNodeVO
     * @throws ActException
     * @version v1
     */
    @PostMapping("v1/save")
    public Result save(@Validated(ADD.class) TaskNodeTO to, BindingResult result) throws ActException {
        try {
            taskNodeAPI.save(to);
            return new ActResult("添加成功");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 查看任务节点详细内容（通过id查找）
     *
     * @param id 任务节点id
     * @return class TaskNodeVO
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/taskNode/{id}")
    public Result TaskNode(@PathVariable String id, HttpServletRequest request) throws ActException {
        try {
            TaskNodeBO bo = taskNodeAPI.findByID(id);
            List<FileVO> fileVOS = files(id, request);
            if (fileVOS == null) {
                bo.setAttachment(false);
            } else {
                if (!fileVOS.isEmpty()) {
                    bo.setAttachment(true);
                }
            }
            return ActResult.initialize(BeanTransform.copyProperties(bo, TaskNodeVO.class, request));
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 编辑
     *
     * @param to 任务节点传输对象
     * @throws ActException
     * @version v1
     */
    @PutMapping("v1/edit")
    public Result edit(@Validated(EDIT.class) TaskNodeTO to, BindingResult result) throws ActException {
        try {
            taskNodeAPI.edit(to);
            return new ActResult("编辑成功");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 发起任务
     *
     * @param to to
     * @throws ActException
     * @version v1
     */
    @PutMapping("v1/initiateTask")
    public Result initiateTask(@Validated(TaskNodeTO.INITIATE.class) TaskNodeTO to, BindingResult result) throws ActException {
        try {
            taskNodeAPI.initiateTask(to);
            return new ActResult("发起任务成功");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 添加小任务
     *
     * @param to to
     * @throws ActException
     * @version v1
     */
    @PostMapping("v1/addTask")
    public Result addTask(TaskNodeTO to, BindingResult result) throws ActException {
        try {
            taskNodeAPI.addTask(to);
            return new ActResult("添加小任务成功");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 删除
     *
     * @param id 任务节点id
     * @throws ActException
     * @version v1
     */
    @DeleteMapping("v1/delete/{id}")
    public Result delete(@PathVariable String id) throws ActException {
        try {
            taskNodeAPI.delete(id);
            return new ActResult("删除成功");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 查找总记录数
     *
     * @param dto 任务节点数据传输
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/count")
    public Result count(TaskNodeDTO dto) throws ActException {
        try {
            return ActResult.initialize(taskNodeAPI.count(dto));
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 检测某执行人某天的任务时长是否超过8小时
     *
     * @param to to
     * @throws ActException
     * @version v1
     */
    @PutMapping("v1/checkTime")
    public Result checkTime(TaskNodeTO to) throws ActException {
        try {
            return ActResult.initialize(taskNodeAPI.checkTime(to));
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 我分发的任务
     *
     * @param dto dto
     * @return class TaskNodeVO
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/my/initiate")
    public Result myInitiate(TaskNodeDTO dto, HttpServletRequest request) throws ActException {
        try {
            List<TaskNodeBO> list = taskNodeAPI.myInitiate(dto);
            return ActResult.initialize(BeanTransform.copyProperties(list, TaskNodeVO.class, request));
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 我分发的任务总条数
     *
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/initiate/num")
    public Result myInitiateNum(TaskNodeDTO dto) throws ActException {
        try {
            return ActResult.initialize(taskNodeAPI.myInitiateNum(dto));
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 撤回任务
     *
     * @param id id
     * @throws ActException
     * @version v1
     */
    @DeleteMapping("v1/reback/{id}")
    public Result reback(@PathVariable String id) throws ActException {
        try {
            taskNodeAPI.reback(id);
            return new ActResult("撤回任务成功");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 确认完成
     *
     * @param to to
     * @throws ActException
     * @version v1
     */
    @PutMapping("v1/finish")
    public Result finish(@Validated(TaskNodeTO.CONFIRM.class) TaskNodeTO to,BindingResult result) throws ActException {
        try {
            taskNodeAPI.finish(to);
            return new ActResult("确认完成成功");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 确认未完成
     *
     * @param to to
     * @throws ActException
     * @version v1
     */
    @PutMapping("v1/unFinish")
    public Result unFinish(@Validated(TaskNodeTO.CONFIRM.class) TaskNodeTO to,BindingResult result) throws ActException {
        try {
            taskNodeAPI.unFinish(to);
            return new ActResult("确认未完成成功");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 上报审核通过
     *
     * @param to to
     * @throws ActException
     * @version v1
     */
    @PutMapping("v1/pass")
    public Result pass(TaskNodeTO to) throws ActException {
        try {
            taskNodeAPI.pass(to);
            return new ActResult("上报审核通过成功");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 上报审核不通过
     *
     * @param to to
     * @throws ActException
     * @version v1
     */
    @PutMapping("v1/notPass")
    public Result notPass(TaskNodeTO to) throws ActException {
        try {
            taskNodeAPI.notPass(to);
            return new ActResult("上报审核不通过成功");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 我负责的任务
     *
     * @param dto dto
     * @return class TaskNodeVO
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/myCharge")
    public Result myCharge(TaskNodeDTO dto, HttpServletRequest request) throws ActException {
        try {
            List<TaskNodeBO> list = taskNodeAPI.myCharge(dto);
            return ActResult.initialize(BeanTransform.copyProperties(list, TaskNodeVO.class, request));
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 我负责的任务总条数
     *
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/charge/num")
    public Result myChargeNum(TaskNodeDTO dto) throws ActException {
        try {
            return ActResult.initialize(taskNodeAPI.myChargeNum(dto));
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 分配我负责的任务
     *
     * @param to to
     * @throws ActException
     * @version v1
     */
    @PutMapping("v1/allotment")
    public Result allotment(TaskNodeTO to) throws ActException {
        try {
            taskNodeAPI.allotment(to);
            return new ActResult("分配我负责的任务成功");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 我执行的任务
     *
     * @param dto dto
     * @return class TaskNodeVO
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/myExecute")
    public Result myExecute(TaskNodeDTO dto, HttpServletRequest request) throws ActException {
        try {
            List<TaskNodeBO> list = taskNodeAPI.myExecute(dto);
            return ActResult.initialize(BeanTransform.copyProperties(list, TaskNodeVO.class, request));
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 我执行的任务总条数
     *
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/execute/num")
    public Result myExecuteNum(TaskNodeDTO dto) throws ActException {
        try {
            return ActResult.initialize(taskNodeAPI.myExecuteNum(dto));
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 确认接收任务
     *
     * @param id id
     * @throws ActException
     * @version v1
     */
    @PutMapping("v1/confirm/{id}")
    public Result confirm(@PathVariable String id) throws ActException {
        try {
            taskNodeAPI.confirm(id);
            return new ActResult("确认接收任务成功");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 不确认接收任务
     *
     * @param to to
     * @throws ActException
     * @version v1
     */
    @PutMapping("v1/un/confirm")
    public Result unConfirm(@Validated(TaskNodeTO.UNCONFIRM.class) TaskNodeTO to, BindingResult result) throws ActException {
        try {
            taskNodeAPI.unConfirm(to);
            return new ActResult("不确认接收任务成功");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 上报任务
     *
     * @param to to
     * @throws ActException
     * @version v1
     */
    @PutMapping("v1/report")
    public Result report(@Validated(TaskNodeTO.REPORT.class) TaskNodeTO to, BindingResult result) throws ActException {
        try {
            taskNodeAPI.report(to);
            return new ActResult("上报任务成功");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 再次分发我执行的任务
     *
     * @param to to
     * @throws ActException
     * @version v1
     */
    @PutMapping("v1/initiateAgain")
    public Result initiateAgain(@Validated(TaskNodeTO.AGAIN.class) TaskNodeTO to, BindingResult result) throws ActException {
        try {
            taskNodeAPI.initiateAgain(to);
            return new ActResult("再次分发成功");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 填写任务完成情况
     *
     * @param to to
     * @throws ActException
     * @version v1
     */
    @PutMapping("v1/write")
    public Result write(@Validated(TaskNodeTO.WRITE.class) TaskNodeTO to, BindingResult result) throws ActException {
        try {
            taskNodeAPI.write(to);
            return new ActResult("填写成功");
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 个人汇总
     *
     * @param dto dto
     * @return class PersonCountVO
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/person/count")
    public Result personCount(@Validated(TaskNodeDTO.PERSON.class) TaskNodeDTO dto, BindingResult result, HttpServletRequest request) throws ActException {
        try {
            List<PersonCountBO> list = taskNodeAPI.personCount(dto);
            return ActResult.initialize(BeanTransform.copyProperties(list, PersonCountVO.class, request));
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 人员标准工时汇总
     *
     * @param dto dto
     * @return class TimeCountVO
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/time/count")
    public Result timeCount(@Validated(TaskNodeDTO.COUNT.class) TaskNodeDTO dto, BindingResult result, HttpServletRequest request) throws ActException {
        try {
            List<TimeCountBO> list = taskNodeAPI.timeCount(dto);
            return ActResult.initialize(BeanTransform.copyProperties(list, TimeCountVO.class, request));
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 分配及确认汇总
     *
     * @param dto dto
     * @return class ConfirmCountVO
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/confirm/count")
    public Result confirmCount(@Validated(TaskNodeDTO.COUNT.class) TaskNodeDTO dto, BindingResult result, HttpServletRequest request) throws ActException {
        try {
            List<ConfirmCountBO> list = taskNodeAPI.confirmCount(dto);
            return ActResult.initialize(BeanTransform.copyProperties(list, ConfirmCountVO.class, request));
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 完成情况汇总
     *
     * @param dto dto
     * @return class FinishCaseVO
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/finish/count")
    public Result finishCount(@Validated(TaskNodeDTO.COUNT.class) TaskNodeDTO dto, BindingResult result, HttpServletRequest request) throws ActException {
        try {
            List<FinishCaseBO> list = taskNodeAPI.finishCount(dto);
            return ActResult.initialize(BeanTransform.copyProperties(list, FinishCaseVO.class, request));
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
            String path = "/taskallotment/tasknode/" + id;
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
            return ActResult.initialize(files(id, request));
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    private List<FileVO> files(String id, HttpServletRequest request) throws SerException {
        //跟前端约定好 ，文件路径是列表id
        String path = "/taskallotment/tasknode/" + id;
        FileInfo fileInfo = new FileInfo();
        fileInfo.setPath(path);
        Object storageToken = request.getAttribute("storageToken");
        fileInfo.setStorageToken(storageToken.toString());
        List<FileVO> files = BeanTransform.copyProperties(fileAPI.list(fileInfo), FileVO.class);
        return files;
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
     * @param deleteFileTO 多文件信息路径
     * @version v1
     */
    @LoginAuth
    @PostMapping("v1/deleteFile")
    public Result delFile(@Validated(DeleteFileTO.TestDEL.class) DeleteFileTO deleteFileTO, HttpServletRequest request) throws SerException {
        if (null != deleteFileTO.getPaths() && deleteFileTO.getPaths().length >= 0) {
            Object storageToken = request.getAttribute("storageToken");
            fileAPI.delFile(storageToken.toString(), deleteFileTO.getPaths());
        }
        return new ActResult("delFile success");
    }

    /**
     * 获取所有地区
     *
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/areas")
    public Result areas() throws ActException {
        try {
            return ActResult.initialize(projectAPI.areas());
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 根据地区获取部门
     *
     * @param dto dto
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/departs")
    public Result departs(@Validated(ProjectDTO.DEPART.class) ProjectDTO dto, BindingResult result) throws ActException {
        try {
            return ActResult.initialize(projectAPI.departs(dto));
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 根据部门获取项目名称
     *
     * @param dto dto
     * @return class ProjectVO
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/projects")
    public Result projects(@Validated(ProjectDTO.PROJECT.class) ProjectDTO dto, BindingResult result, HttpServletRequest request) throws ActException {
        try {
            List<ProjectBO> list = projectAPI.projects(dto);
            return ActResult.initialize(BeanTransform.copyProperties(list, ProjectVO.class, request));
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 根据部门获取项目表
     *
     * @param dto dto
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/count/tables")
    public Result countTables(@Validated(ProjectDTO.PROJECT.class) ProjectDTO dto, BindingResult result) throws ActException {
        try {
            List<ProjectBO> list = projectAPI.projects(dto);
            if (null != list) {
                List<String> projectIds = list.stream().map(projectBO -> projectBO.getId()).collect(Collectors.toList());
                if (!projectIds.isEmpty()) {
                    String[] strings = new String[projectIds.size()];
                    strings = projectIds.toArray(strings);
                    TableDTO tableDTO = new TableDTO();
                    tableDTO.setProjectIds(strings);
                    return ActResult.initialize(tableAPI.tables(tableDTO));
                }
            }
            return ActResult.initialize(null);
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 根据项目获取项目表
     *
     * @param dto dto
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/tables")
    public Result tables(@Validated(TableDTO.TABLE.class) TableDTO dto, BindingResult result) throws ActException {
        try {
            return ActResult.initialize(tableAPI.tables(dto));
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 获取用户列表
     *
     * @return class UserVO
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/users")
    public Result users(HttpServletRequest request) throws ActException {
        try {
            List<UserBO> list = positionDetailUserAPI.findUserListInOrgan();
            return ActResult.initialize(BeanTransform.copyProperties(list, UserVO.class, request));
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }

    /**
     * 根据部门获取员工
     *
     * @param dto dto
     * @throws ActException
     * @version v1
     */
    @GetMapping("v1/names")
    public Result names(@Validated(TaskNodeDTO.NAME.class) TaskNodeDTO dto, BindingResult result) throws ActException {
        try {
            Set<String> set = new HashSet<>();
            String[] departIds = dto.getDeparIds();
            for (String s : departIds) {
                Set<String> userIds = departmentDetailAPI.departPersons(s);
                for (String id : userIds) {
                    set.add(userAPI.findNameById(id));
                }
            }
            return ActResult.initialize(set);
        } catch (SerException e) {
            throw new ActException(e.getMessage());
        }
    }
}