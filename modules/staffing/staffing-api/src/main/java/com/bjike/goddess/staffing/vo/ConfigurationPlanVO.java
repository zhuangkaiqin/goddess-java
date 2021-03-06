package com.bjike.goddess.staffing.vo;

import com.bjike.goddess.staffing.bo.PlanSonBO;

import java.util.ArrayList;
import java.util.List;

/**
 * 人数配置-计划表现层对象
 *
 * @Author: [ chenjunhao ]
 * @Date: [ 2017-07-29 10:33 ]
 * @Description: [ 人数配置-计划表现层对象 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public class ConfigurationPlanVO {

    /**
     * id
     */
    private String id;
    /**
     * 分类
     */
    private String classify;

    /**
     * 权重
     */
    private String classifyWeight;

    /**
     * 人数合计
     */
    private Integer total;

    /**
     * 人数占比
     */
    private String proportion;

    /**
     * 占比标准
     */
    private String standard;

    /**
     * 子表集合
     */
    private List<PlanSonVO> sons;

    public List<PlanSonVO> getSons() {
        return sons;
    }

    public void setSons(List<PlanSonVO> sons) {
        this.sons = sons;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public String getClassifyWeight() {
        return classifyWeight;
    }

    public void setClassifyWeight(String classifyWeight) {
        this.classifyWeight = classifyWeight;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getProportion() {
        return proportion;
    }

    public void setProportion(String proportion) {
        this.proportion = proportion;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}