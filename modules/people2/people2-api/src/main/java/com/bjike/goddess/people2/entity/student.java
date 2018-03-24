package com.bjike.goddess.people2.entity;

import com.bjike.goddess.common.api.entity.BaseEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


/**
 * 测试学生类
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-05 05:18 ]
 * @Description: [ 测试学生类 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@Entity
@Table(name = "people2_student")
public class student extends BaseEntity {


    /**
     *      一对多
     *      OneToMany
     *          mappedBy ( 主表 )student Set list
     *          cascade 级联属性 (ALL 新增级联,更新级联,删除级联,刷新级联)
     *          fetch 加载模式
     *              LAZY,EAGER
     *
     */
    @OneToMany(mappedBy = "student", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Set<Book> bookSet = new HashSet<>();

    public Set<Book> getBookSet() {
        return bookSet;
    }

    public void setBookSet(Set<Book> bookSet) {
        this.bookSet = bookSet;
    }

    /**
     * 学生学号
     */
    @Column(name = "StuNum", nullable = false, columnDefinition = "VARCHAR(255)   COMMENT '学生学号'")
    private String StuNum;

    /**
     * 学生姓名
     */
    @Column(name = "StuName", nullable = false, columnDefinition = "VARCHAR(255)   COMMENT '学生姓名'")
    private String StuName;

    /**
     * 学生年龄
     */
    @Column(name = "StuAge", nullable = false, columnDefinition = "VARCHAR(255)   COMMENT '学生年龄'")
    private String StuAge;

    /**
     * 学生性别
     */
    @Column(name = "StuSex", nullable = false, columnDefinition = "VARCHAR(255)   COMMENT '学生性别'")
    private String StuSex;


    public String getStuNum() {
        return StuNum;
    }

    public void setStuNum(String StuNum) {
        this.StuNum = StuNum;
    }

    public String getStuName() {
        return StuName;
    }

    public void setStuName(String StuName) {
        this.StuName = StuName;
    }

    public String getStuAge() {
        return StuAge;
    }

    public void setStuAge(String StuAge) {
        this.StuAge = StuAge;
    }

    public String getStuSex() {
        return StuSex;
    }

    public void setStuSex(String StuSex) {
        this.StuSex = StuSex;
    }
}