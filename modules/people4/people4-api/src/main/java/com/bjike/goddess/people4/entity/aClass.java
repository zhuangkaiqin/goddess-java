package com.bjike.goddess.people4.entity;

import com.bjike.goddess.common.api.entity.BaseEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


/**
 * 测试班级
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-19 10:22 ]
 * @Description: [ 测试班级 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@Entity
@Table(name = "people4_aclass")
public class aClass extends BaseEntity {

    @OneToMany(mappedBy = "aClass", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Set<Student> studentSet = new HashSet<>();
    /**
     * 班级编号
     */
    @Column(name = "ClaNum", nullable = false, columnDefinition = "VARCHAR(255)   COMMENT '班级编号'")
    private String ClaNum;

    /**
     * 专业名称
     */
    @Column(name = "ClaType", nullable = false, columnDefinition = "VARCHAR(255)   COMMENT '专业名称'")
    private String ClaType;

    public Set<Student> getStudentSet() {
        return studentSet;
    }

    public void setStudentSet(Set<Student> studentSet) {
        this.studentSet = studentSet;
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