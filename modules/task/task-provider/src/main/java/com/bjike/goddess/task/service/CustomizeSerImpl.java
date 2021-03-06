package com.bjike.goddess.task.service;

import com.bjike.goddess.common.api.dto.Restrict;
import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.jpa.service.ServiceImpl;
import com.bjike.goddess.common.utils.bean.BeanTransform;
import com.bjike.goddess.common.utils.date.DateUtil;
import com.bjike.goddess.task.bo.CustomizeBO;
import com.bjike.goddess.task.dto.CustomizeDTO;
import com.bjike.goddess.task.entity.Customize;
import com.bjike.goddess.task.enums.NoticeType;
import com.bjike.goddess.task.enums.SummaryType;
import com.bjike.goddess.task.enums.TimeType;
import com.bjike.goddess.task.quartz.TaskDaySession;
import com.bjike.goddess.task.quartz.TaskSession;
import com.bjike.goddess.task.to.CustomizeTO;
import com.bjike.goddess.taskallotment.api.ProjectAPI;
import com.bjike.goddess.taskallotment.api.TableAPI;
import com.bjike.goddess.taskallotment.bo.ProjectBO;
import com.bjike.goddess.user.api.UserAPI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: [liguiqin]
 * @Date: [2017-09-25 17:05]
 * @Description: [ ]
 * @Version: [1.0.0]
 * @Copy: [com.bjike]
 */
@Service
public class CustomizeSerImpl extends ServiceImpl<Customize, CustomizeDTO> implements CustomizeSer {
    @Autowired
    private UserAPI userAPI;
    @Autowired
    private ScheduleSer scheduleSer;
    @Autowired
    private ProjectAPI projectAPI;
    @Autowired
    private TableAPI tableAPI;


    @Override
    public List<CustomizeBO> list(CustomizeDTO dto) throws SerException {
        List<CustomizeBO> bos = BeanTransform.copyProperties(super.findByPage(dto), CustomizeBO.class);
        if (null != bos) {
            for (CustomizeBO bo : bos) {
                bo.setCollectName(super.findById(bo.getId()).getName());
                ProjectBO project = projectAPI.findByID(bo.getProjectId());
                if (null != project) {
                    bo.setProject(project.getProject());
                }
                bo.setTables(tableAPI.names(bo.getTablesId().split(",")));
            }
        }
        return bos;
    }

    @Override
    public Long count(CustomizeDTO dto) throws SerException {
        return super.count(dto);
    }

    @Override
    public void add(CustomizeTO to) throws SerException {
        String nickname = userAPI.currentUser().getUsername();
        validated(to);
        Customize customize = BeanTransform.copyProperties(to, Customize.class, "tables", "fields");
        customize.setTablesId(StringUtils.join(to.getTables(), ","));
        customize.setFields(StringUtils.join(to.getFields(), ","));
        customize.setUser(nickname);
        customize.setName(to.getCollectName());
        customize.setLastTime(LocalDateTime.now());
        super.save(customize);
        if (customize.getEnable()) {
            TaskSession.put(customize.getId(), customize);
        }
    }

    @Override
    public void edit(CustomizeTO to) throws SerException {
        Customize entity=super.findById(to.getId());
        validated(to);
        Customize customize = BeanTransform.copyProperties(to, Customize.class, "tables", "fields");
        customize.setTablesId(StringUtils.join(to.getTables(), ","));
        customize.setFields(StringUtils.join(to.getFields(), ","));
        customize.setName(to.getCollectName());
        customize.setLastTime(LocalDateTime.now());
        BeanUtils.copyProperties(customize,entity,"id","user","createTime");
        entity.setModifyTime(LocalDateTime.now());
        super.update(entity);
        if (entity.getEnable()) {
            TaskSession.put(entity.getId(), entity);
        }
    }

    @Override
    public void enable(String id, boolean enable) throws SerException {
        Customize customize = super.findById(id);
        if (null != customize) {
            if (enable) {
                Customize c = TaskSession.get(customize.getId());
                if (null == c) {
                    TaskSession.put(customize.getId(), customize);
                }
            } else {
                TaskSession.remove(customize.getId());
            }
            customize.setEnable(enable);
            super.update(customize);
        } else {
            throw new SerException("找不到该记录");
        }

    }

    private void validated(CustomizeTO to) throws SerException {
        if (!to.getSummaryType().equals(SummaryType.ALL)) {
            if (StringUtils.isBlank(to.getSummaryTarget())) {
                throw new SerException("请指定汇总部门或者人员");
            }
        }
        if (!to.getNoticeType().equals(NoticeType.ALL.ALL)) {
            if (StringUtils.isBlank(to.getNoticeTarget())) {
                throw new SerException("请指定提醒部门或者人员");
            }
        }try {
            Integer.parseInt(to.getTimeVal());
        }catch (Exception e){
            throw new SerException("定时时间间隔值必须为整数数字");
        }
    }

    @Transactional
    @Override
    public void executeTask() throws SerException {
        List<Customize> customizes = queryTask(); //任务查询
        for (Customize customize : customizes) {
            if (isInvoking(customize)) { //是否可调用
                //查询调用
                try {
                    scheduleSer.customizeCollect(customize);
                } catch (Exception e) { //不进行异常处理
                    e.printStackTrace();
                }
                LocalDateTime now = LocalDateTime.now();
                String sql = "UPDATE task_customize SET lastTime='%s' WHERE id='%s'";
                super.executeSql(String.format(sql, DateUtil.dateToString(now), customize.getId()));
                customize.setLastTime(now);
                TaskSession.put(customize.getId(), customize);
            }
        }
    }


    /**
     * 查询任务
     *
     * @return
     * @throws SerException
     */
    private boolean first = true;

    private List<Customize> queryTask() throws SerException {
        List<Customize> customizes = new ArrayList<>();
        if (null != TaskSession.sessions()) {
            Map<String, Customize> map = TaskSession.sessions().asMap();
            for (Map.Entry<String, Customize> entry : map.entrySet()) {
                customizes.add(entry.getValue());
            }
        } else if (first) { //仅查询一次
            first = false;
            CustomizeDTO dto = new CustomizeDTO();
            dto.getConditions().add(Restrict.eq("enable", true));
            customizes = super.findByCis(dto);
            for (Customize customize : customizes) {
                TaskSession.put(customize.getId(), customize);
            }
        }
        return customizes;
    }

    /**
     * 是否可调用
     *
     * @param customize
     * @return
     */
    private boolean isInvoking(Customize customize) throws SerException {
        TimeType type = customize.getTimeType();
        String timeVal = customize.getTimeVal();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime last = customize.getLastTime();
        if (!type.equals(TimeType.EVERYDAY)) { //每隔时间段
            int second = 0;
            try {
                Integer.parseInt(timeVal);
                if (type.equals(TimeType.MINUTE)) {
                    second = Integer.parseInt(timeVal) * 60;
                } else if (type.equals(TimeType.HOUR)) {
                    second = Integer.parseInt(timeVal) * 60 * 60;
                }
                if (second < ChronoUnit.SECONDS.between(last, now)) {
                    return true;
                }
            }catch (Exception e){
                return false;
            }
        } else { //每天
            String time = DateUtil.dateToString(LocalTime.now());//截取到分钟
            time = StringUtils.substringBeforeLast(time, ":");
            //每天某个时间调用一次,TaskDaySession为空则表示没执行过
            if (time.equals(timeVal) && TaskDaySession.get(customize.getId()) == null) {
                //缓存两分钟后销毁(time与timeVal会在一分钟内多次匹配,TaskDaySession只要保存有该id,表示已经执行过)
                TaskDaySession.put(customize.getId(), time);
                return true;
            }
        }
        return false;
    }

}
