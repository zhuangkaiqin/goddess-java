package com.bjike.goddess.people.entity;

import com.bjike.goddess.common.api.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * 测试学生类
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-02 11:32 ]
 * @Description: [ 测试学生类 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@Entity
@Table(name = "people_student")
public class student extends BaseEntity {

    /**
     * 学生id
     */
    @Column(name = "stuId", nullable = false, columnDefinition = "VARCHAR(255)   COMMENT '学生id'")
    private String stuId;

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
     * 学生性别
     */
    @Column(name = "stuSex", nullable = false, columnDefinition = "VARCHAR(255)   COMMENT '学生性别'")
    //@Cascade()
    private String stuSex;


    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
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

    public String getStuSex() {
        return stuSex;
    }

    public void setStuSex(String stuSex) {
        this.stuSex = stuSex;
    }
}