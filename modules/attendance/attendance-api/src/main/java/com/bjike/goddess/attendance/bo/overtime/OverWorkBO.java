package com.bjike.goddess.attendance.bo.overtime;

import com.bjike.goddess.attendance.enums.AuditStatus;
import com.bjike.goddess.common.api.bo.BaseBO;

import javax.persistence.Column;

/**
 * 加班业务传输对象
 *
 * @Author: [ tanghaixiang ]
 * @Date: [ 2017-10-10 10:32 ]
 * @Description: [ 加班业务传输对象 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public class OverWorkBO extends BaseBO {

    /**
     * 地区
     */
    private String area;

    /**
     * 任务下达人
     */
    private String tasker;

    /**
     * 加班人员
     */
    private String overWorker;

    /**
     * 加班类型
     */
    private String overType;

    /**
     * 部门
     */
    private String depart;

    /**
     * 职位
     */
    private String position;

    /**
     * 开始时间
     */
    private String overStartTime;

    /**
     * 结束时间
     */
    private String overEndTime;

    /**
     * 加班时长
     */
    private Double overLong;

    /**
     * 是否午休
     */
    private Boolean noonBreakOr;

    /**
     * 工作内容
     */
    private String workContent;

    /**
     * 完成情况
     */
    private String completeCon;

    /**
     * 可休天数
     */
    private Double relaxDay;

    /**
     * 负责人(审批人)
     */
    private String charger;

    /**
     * 审核意见
     */
    private String auditAdvice;

    /**
     * 审核状态
     */
    private AuditStatus auditStatus;


    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getTasker() {
        return tasker;
    }

    public void setTasker(String tasker) {
        this.tasker = tasker;
    }

    public String getOverWorker() {
        return overWorker;
    }

    public void setOverWorker(String overWorker) {
        this.overWorker = overWorker;
    }

    public String getOverType() {
        return overType;
    }

    public void setOverType(String overType) {
        this.overType = overType;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getOverStartTime() {
        return overStartTime;
    }

    public void setOverStartTime(String overStartTime) {
        this.overStartTime = overStartTime;
    }

    public String getOverEndTime() {
        return overEndTime;
    }

    public void setOverEndTime(String overEndTime) {
        this.overEndTime = overEndTime;
    }

    public Double getOverLong() {
        return overLong;
    }

    public void setOverLong(Double overLong) {
        this.overLong = overLong;
    }

    public Boolean getNoonBreakOr() {
        return noonBreakOr;
    }

    public void setNoonBreakOr(Boolean noonBreakOr) {
        this.noonBreakOr = noonBreakOr;
    }

    public String getWorkContent() {
        return workContent;
    }

    public void setWorkContent(String workContent) {
        this.workContent = workContent;
    }

    public String getCompleteCon() {
        return completeCon;
    }

    public void setCompleteCon(String completeCon) {
        this.completeCon = completeCon;
    }

    public Double getRelaxDay() {
        return relaxDay;
    }

    public void setRelaxDay(Double relaxDay) {
        this.relaxDay = relaxDay;
    }

    public String getCharger() {
        return charger;
    }

    public void setCharger(String charger) {
        this.charger = charger;
    }

    public String getAuditAdvice() {
        return auditAdvice;
    }

    public void setAuditAdvice(String auditAdvice) {
        this.auditAdvice = auditAdvice;
    }

    public AuditStatus getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(AuditStatus auditStatus) {
        this.auditStatus = auditStatus;
    }
}