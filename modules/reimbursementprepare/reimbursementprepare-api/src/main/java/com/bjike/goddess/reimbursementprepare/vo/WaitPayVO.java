package com.bjike.goddess.reimbursementprepare.vo;

import com.bjike.goddess.reimbursementprepare.enums.PayStatus;

/**
 * 等待付款表现层对象
 *
 * @Author: [ chenjunhao ]
 * @Date: [ 2017-07-03 03:01 ]
 * @Description: [ 等待付款表现层对象 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public class WaitPayVO {

    /**
     * id
     */
    private String id;
    /**
     * 时间
     */
    private String lendDate;

    /**
     * 科目
     */
    private String firstSubject;

    /**
     * 项目组
     */
    private String projectGroup;

    /**
     * 支付金额
     */
    private Double lendMoney;

    /**
     * 是否付款
     */
    private String payCondition;

    /**
     * 付款时间
     */
    private String payDate;

    /**
     * 付款人
     */
    private String payer;

    /**
     * 付款状态
     */
    private PayStatus payStatus;

    /**
     * 地区
     */
    private String area;

    /**
     * 识别类型
     */
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLendDate() {
        return lendDate;
    }

    public void setLendDate(String lendDate) {
        this.lendDate = lendDate;
    }

    public String getFirstSubject() {
        return firstSubject;
    }

    public void setFirstSubject(String firstSubject) {
        this.firstSubject = firstSubject;
    }

    public String getProjectGroup() {
        return projectGroup;
    }

    public void setProjectGroup(String projectGroup) {
        this.projectGroup = projectGroup;
    }

    public Double getLendMoney() {
        return lendMoney;
    }

    public void setLendMoney(Double lendMoney) {
        this.lendMoney = lendMoney;
    }

    public String getPayCondition() {
        return payCondition;
    }

    public void setPayCondition(String payCondition) {
        this.payCondition = payCondition;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public PayStatus getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(PayStatus payStatus) {
        this.payStatus = payStatus;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}