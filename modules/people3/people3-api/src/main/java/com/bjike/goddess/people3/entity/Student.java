package com.bjike.goddess.people3.entity;

import com.bjike.goddess.common.api.entity.BaseEntity;

import javax.persistence.*;


/**
 * 测试学生类
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-09 11:13 ]
 * @Description: [ 测试学生类 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@Entity
@Table(name = "people3_student")
public class Student extends BaseEntity {

    @ManyToOne(cascade = {CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinColumn(name = "ClassId")
    private Class aClass;

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
     * 学生年龄
     */
    @Column(name = "stuAge", nullable = false, columnDefinition = "VARCHAR(255)   COMMENT '学生年龄'")
    private String stuAge;

    /**
     * 班级编号
     */
    @Column(name = "classNum", nullable = false, columnDefinition = "VARCHAR(255)   COMMENT '班级编号'")
    private String classNum;


    public Class getaClass() {
        return aClass;
    }

    public void setaClass(Class aClass) {
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

    public String getStuAge() {
        return stuAge;
    }

    public void setStuAge(String stuAge) {
        this.stuAge = stuAge;
    }

    public String getClassNum() {
        return classNum;
    }

    public void setClassNum(String classNum) {
        this.classNum = classNum;
    }

    @Override
    public String toString() {
        return "Student{" +
                "aClass=" + aClass +
                ", stuNum='" + stuNum + '\'' +
                ", stuName='" + stuName + '\'' +
                ", stuAge='" + stuAge + '\'' +
                ", classNum='" + classNum + '\'' +
                '}';
    }
}