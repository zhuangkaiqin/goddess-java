package com.bjike.goddess.moneyside.to;

import com.bjike.goddess.common.api.to.BaseTO;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 收益分配额
 *
 * @Author: [ xiazhili ]
 * @Date: [ 2017-06-07 09:28 ]
 * @Description: [ 收益分配额 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public class IncomeQuotaTO extends BaseTO {
    public interface TestAdd{}
    public interface TestEdit{}
    /**
     * 投资人
     */
    @NotBlank(groups = {IncomeQuotaTO.TestAdd.class,IncomeQuotaTO.TestEdit.class},message = "投资人不能为空")
    private String investor;

    /**
     * 项目名称
     */
    @NotBlank(groups = {IncomeQuotaTO.TestAdd.class,IncomeQuotaTO.TestEdit.class},message = "项目名称不能为空")
    private String innerProject;

    /**
     * 一次分配比例(%)
     */
    @NotBlank(groups = {IncomeQuotaTO.TestAdd.class,IncomeQuotaTO.TestEdit.class},message = "一次分配比例(%)不能为空")
    private Double primaryProportion;

    /**
     * 入资到账时间比例(%)
     */
    @NotBlank(groups = {IncomeQuotaTO.TestAdd.class,IncomeQuotaTO.TestEdit.class},message = "入资到账时间比例(%)不能为空")
    private Double proportionGroup;

    /**
     * 入资信用度比例(%)
     */
    @NotBlank(groups = {IncomeQuotaTO.TestAdd.class,IncomeQuotaTO.TestEdit.class},message = "入资信用度比例(%)不能为空")
    private Double creditScale;

    /**
     * 其他
     */
    @NotBlank(groups = {IncomeQuotaTO.TestAdd.class,IncomeQuotaTO.TestEdit.class},message = "其他不能为空")
    private String other;

    /**
     * 投资分配额(该项目的累计投资金额*投资分配比例)
     */
    private Double investmentAllocation;

    /**
     * 风险控制保证金额（该项目的累计投资金额*风险控制保证金比例）
     */
    private Double riskControlGuaranteeAmount;

    /**
     * 总分配额（该项目的累计投资金额*总分配比例）
     */
    private Double totalQuota;

    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改时间
     */
    private String modifyTime;


    public String getInvestor() {
        return investor;
    }

    public void setInvestor(String investor) {
        this.investor = investor;
    }

    public String getInnerProject() {
        return innerProject;
    }

    public void setInnerProject(String innerProject) {
        this.innerProject = innerProject;
    }

    public Double getPrimaryProportion() {
        return primaryProportion;
    }

    public void setPrimaryProportion(Double primaryProportion) {
        this.primaryProportion = primaryProportion;
    }

    public Double getProportionGroup() {
        return proportionGroup;
    }

    public void setProportionGroup(Double proportionGroup) {
        this.proportionGroup = proportionGroup;
    }

    public Double getCreditScale() {
        return creditScale;
    }

    public void setCreditScale(Double creditScale) {
        this.creditScale = creditScale;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public Double getInvestmentAllocation() {
        return investmentAllocation;
    }

    public void setInvestmentAllocation(Double investmentAllocation) {
        this.investmentAllocation = investmentAllocation;
    }

    public Double getRiskControlGuaranteeAmount() {
        return riskControlGuaranteeAmount;
    }

    public void setRiskControlGuaranteeAmount(Double riskControlGuaranteeAmount) {
        this.riskControlGuaranteeAmount = riskControlGuaranteeAmount;
    }

    public Double getTotalQuota() {
        return totalQuota;
    }

    public void setTotalQuota(Double totalQuota) {
        this.totalQuota = totalQuota;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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