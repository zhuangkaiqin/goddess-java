package com.bjike.goddess.people4.vo;

/**
 * 测试班级表现层对象
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-19 10:22 ]
 * @Description: [ 测试班级表现层对象 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public class aClassVO {

    /**
     * id
     */
    private String id;
    /**
     * 班级编号
     */
    private String ClaNum;

    /**
     * 专业名称
     */
    private String ClaType;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClaNum() {
        return ClaNum;
    }

    public void setClaNum(String ClaNum) {
        this.ClaNum = ClaNum;
    }

    public String getClaType() {
        return ClaType;
    }

    public void setClaType(String ClaType) {
        this.ClaType = ClaType;
    }
}