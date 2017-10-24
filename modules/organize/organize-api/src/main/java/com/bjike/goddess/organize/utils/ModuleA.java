package com.bjike.goddess.organize.utils;

import com.bjike.goddess.common.api.to.BaseTO;

import java.util.List;


/**
 * 岗位工作明细表-模块表
 *
 * @Author: [ zhuangkaiqin ]
 * @Date: [ 2017-09-12 01:58 ]
 * @Description: [ 岗位工作明细表-模块表 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public class ModuleA extends BaseTO {

    /**
     * 岗位明细id
     */
    private String workDetailsId;

    /**
     * 模块名字
     */
    private String name;

    /**
     * 有无关联
     */
    private Boolean hasConnet;

    /**
     * 通报时间节点
     */
    private String informTimeNode;

    /**
     * 通报形式
     */
    private String notificationForm;

    /**
     * 通报内容模板
     */
    private String notificationContent;

    /**
     * 协助时间节点
     */
    private String timeNode;

    /**
     * 协助函发送形式
     */
    private String letterForm;

    /**
     * 协助内容模板
     */
    private String contentTemplate;

    /**
     * 功能
     */
    private String functions;

    /**
     * 指标数据
     */
    private List<IndicatorA> indicatorAList;
    /**
     * 指标数据长度
     */
    private Integer indicatorALength;

    public String getWorkDetailsId() {
        return workDetailsId;
    }

    public void setWorkDetailsId(String workDetailsId) {
        this.workDetailsId = workDetailsId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getHasConnet() {
        return hasConnet;
    }

    public void setHasConnet(Boolean hasConnet) {
        this.hasConnet = hasConnet;
    }

    public String getInformTimeNode() {
        return informTimeNode;
    }

    public void setInformTimeNode(String informTimeNode) {
        this.informTimeNode = informTimeNode;
    }

    public String getNotificationForm() {
        return notificationForm;
    }

    public void setNotificationForm(String notificationForm) {
        this.notificationForm = notificationForm;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }

    public String getTimeNode() {
        return timeNode;
    }

    public void setTimeNode(String timeNode) {
        this.timeNode = timeNode;
    }

    public String getLetterForm() {
        return letterForm;
    }

    public void setLetterForm(String letterForm) {
        this.letterForm = letterForm;
    }

    public String getContentTemplate() {
        return contentTemplate;
    }

    public void setContentTemplate(String contentTemplate) {
        this.contentTemplate = contentTemplate;
    }

    public String getFunctions() {
        return functions;
    }

    public void setFunctions(String functions) {
        this.functions = functions;
    }

    public List<IndicatorA> getIndicatorAList() {
        return indicatorAList;
    }

    public void setIndicatorAList(List<IndicatorA> indicatorAList) {
        this.indicatorAList = indicatorAList;
    }

    public Integer getIndicatorALength() {
        return indicatorALength;
    }

    public void setIndicatorALength(Integer indicatorALength) {
        this.indicatorALength = indicatorALength;
    }
}