package com.bjike.goddess.people4.entity;

import com.bjike.goddess.common.api.entity.BaseEntity;

import javax.persistence.*;


/**
 * 测试学生
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-19 10:07 ]
 * @Description: [ 测试学生 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@Entity
@Table(name = "people4_student")
public class Student extends BaseEntity {

    @ManyToOne(cascade = {CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinColumn(name = "ClaNum")
    private aClass aClass;
    /**
     * 学生学号
     */
    @Column(name = "stuNum", nullable = false, columnDefinition = "VARCHAR(255)   COMMENT '学生学号'")
    private String stuNum;

    /**
     * 学生姓名
     */
    @Column(name = "stuName", nullable = false, columnDefinition = "VARCHAR(255)   COMMENT '学生姓名'")
    private String stuName;

    /**
     * 班级编号
     */
    @Column(name = "classNum", nullable = false, columnDefinition = "VARCHAR(255)   COMMENT '班级编号'")
    private String classNum;


    public com.bjike.goddess.people4.entity.aClass getaClass() {
        return aClass;
    }

    public void setaClass(com.bjike.goddess.people4.entity.aClass aClass) {
        this.aClass = aClass;
    }

    public String getStuNum() {
        return stuNum;
    }

    public void setStuNum(String stuNum) {
        this.stuNum = stuNum;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public String getClassNum() {
        return classNum;
    }

    public void setClassNum(String classNum) {
        this.classNum = classNum;
    }
}