package com.bjike.goddess.projectprocing.to;

import com.bjike.goddess.common.api.to.BaseTO;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 项目实施
 *
 * @Author: [ tanghaixiang ]
 * @Date: [ 2017-03-31 02:33 ]
 * @Description: [ 项目实施 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public class ProjectCarryTO extends BaseTO {

    public interface TESTAddAndEdit{}

    /**
     * 合同外部项目名称
     */
    @NotBlank(groups = {ProjectCarryTO.TESTAddAndEdit.class},message = "合同外部项目名称不能为空")
    private String outerName;

    /**
     * 内部项目名称
     */
    @NotBlank(groups = {ProjectCarryTO.TESTAddAndEdit.class},message = "内部项目名称不能为空")
    private String innerName;

    /**
     * 合同签订情况
     */
    @NotBlank(groups = {ProjectCarryTO.TESTAddAndEdit.class},message = "合同签订情况不能为空")
    private String signCondition;

    /**
     * 立项情况
     */
    @NotBlank(groups = {ProjectCarryTO.TESTAddAndEdit.class},message = "立项情况不能为空")
    private String signProject;

    /**
     * 派工单编号
     */
    @NotBlank(groups = {ProjectCarryTO.TESTAddAndEdit.class},message = "派工单编号不能为空")
    private String depatchNum;

    /**
     * 地区
     */
    @NotBlank(groups = {ProjectCarryTO.TESTAddAndEdit.class},message = "地区不能为空")
    private String area;

    /**
     * 业务类型
     */
    @NotBlank(groups = {ProjectCarryTO.TESTAddAndEdit.class},message = "业务类型不能为空")
    private String businessType;

    /**
     * 业务方向科目
     */
    @NotBlank(groups = {ProjectCarryTO.TESTAddAndEdit.class},message = "业务方向科目不能为空")
    private String businessSubject;

    /**
     * 所属项目组
     */
    private String projectGroup;

    /**
     * 项目规模
     */
    private String projectSize;

    /**
     * BBU规模
     */
    private String bbuSize;

    /**
     * RRU规模
     */
    private String rruSize;

    /**
     * 已开通站点数
     */
    private String openStationPoints;

    /**
     * 已开通BBU数
     */
    private String openBbuPoints;

    /**
     * 已开通RRU数
     */
    private String openRruPoints;

    /**
     * 未开通站点
     */
    private String noOpenPoints;

    /**
     * 未开通BBU数
     */
    private String noOpenBbuPoints;

    /**
     * 未开通RRU数
     */
    private String noOpenRruPoints;

    /**
     * 完工数
     */
    private String completeNum;

    /**
     * 未完工数
     */
    private String noCompleteNum;

    /**
     * 是否初验
     */
    @NotBlank(groups = {ProjectCarryTO.TESTAddAndEdit.class},message = "是否初验不能为空")
    private String startTestCondition;

    /**
     * 是否终验
     */
    @NotBlank(groups = {ProjectCarryTO.TESTAddAndEdit.class},message = "是否终验不能为空")
    private String finalTestCondition;

    /**
     * 备注
     */
    private String remark;

    /**
     * 商务合同外部项目名称id
     */
    private String outerNameId;

    /**
     * 市场信息记录内部项目名称id
     */
    private String innerNameId;

    /**
     * 市场信息收集公司业务/发展方向id
     */
    private String businessId;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改时间
     */
    private String modifyTime;


    public String getOuterName() {
        return outerName;
    }

    public void setOuterName(String outerName) {
        this.outerName = outerName;
    }

    public String getInnerName() {
        return innerName;
    }

    public void setInnerName(String innerName) {
        this.innerName = innerName;
    }

    public String getSignCondition() {
        return signCondition;
    }

    public void setSignCondition(String signCondition) {
        this.signCondition = signCondition;
    }

    public String getSignProject() {
        return signProject;
    }

    public void setSignProject(String signProject) {
        this.signProject = signProject;
    }

    public String getDepatchNum() {
        return depatchNum;
    }

    public void setDepatchNum(String depatchNum) {
        this.depatchNum = depatchNum;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessSubject() {
        return businessSubject;
    }

    public void setBusinessSubject(String businessSubject) {
        this.businessSubject = businessSubject;
    }

    public String getProjectGroup() {
        return projectGroup;
    }

    public void setProjectGroup(String projectGroup) {
        this.projectGroup = projectGroup;
    }

    public String getProjectSize() {
        return projectSize;
    }

    public void setProjectSize(String projectSize) {
        this.projectSize = projectSize;
    }

    public String getBbuSize() {
        return bbuSize;
    }

    public void setBbuSize(String bbuSize) {
        this.bbuSize = bbuSize;
    }

    public String getRruSize() {
        return rruSize;
    }

    public void setRruSize(String rruSize) {
        this.rruSize = rruSize;
    }

    public String getOpenStationPoints() {
        return openStationPoints;
    }

    public void setOpenStationPoints(String openStationPoints) {
        this.openStationPoints = openStationPoints;
    }

    public String getOpenBbuPoints() {
        return openBbuPoints;
    }

    public void setOpenBbuPoints(String openBbuPoints) {
        this.openBbuPoints = openBbuPoints;
    }

    public String getOpenRruPoints() {
        return openRruPoints;
    }

    public void setOpenRruPoints(String openRruPoints) {
        this.openRruPoints = openRruPoints;
    }

    public String getNoOpenPoints() {
        return noOpenPoints;
    }

    public void setNoOpenPoints(String noOpenPoints) {
        this.noOpenPoints = noOpenPoints;
    }

    public String getNoOpenBbuPoints() {
        return noOpenBbuPoints;
    }

    public void setNoOpenBbuPoints(String noOpenBbuPoints) {
        this.noOpenBbuPoints = noOpenBbuPoints;
    }

    public String getNoOpenRruPoints() {
        return noOpenRruPoints;
    }

    public void setNoOpenRruPoints(String noOpenRruPoints) {
        this.noOpenRruPoints = noOpenRruPoints;
    }

    public String getCompleteNum() {
        return completeNum;
    }

    public void setCompleteNum(String completeNum) {
        this.completeNum = completeNum;
    }

    public String getNoCompleteNum() {
        return noCompleteNum;
    }

    public void setNoCompleteNum(String noCompleteNum) {
        this.noCompleteNum = noCompleteNum;
    }

    public String getStartTestCondition() {
        return startTestCondition;
    }

    public void setStartTestCondition(String startTestCondition) {
        this.startTestCondition = startTestCondition;
    }

    public String getFinalTestCondition() {
        return finalTestCondition;
    }

    public void setFinalTestCondition(String finalTestCondition) {
        this.finalTestCondition = finalTestCondition;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOuterNameId() {
        return outerNameId;
    }

    public void setOuterNameId(String outerNameId) {
        this.outerNameId = outerNameId;
    }

    public String getInnerNameId() {
        return innerNameId;
    }

    public void setInnerNameId(String innerNameId) {
        this.innerNameId = innerNameId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }
}