package com.bjike.goddess.people2.vo;

import com.bjike.goddess.people2.entity.Book;

import java.util.Set;

/**
 * 测试学生类表现层对象
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-05 05:18 ]
 * @Description: [ 测试学生类表现层对象 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public class studentVO {

    private Set<Book> bookSet;

    public Set<Book> getBookSet() {
        return bookSet;
    }

    public void setBookSet(Set<Book> bookSet) {
        this.bookSet = bookSet;
    }

    /**
     * id
     */
    private String id;
    /**
     * 学生学号
     */
    private String StuNum;

    /**
     * 学生姓名
     */
    private String StuName;

    /**
     * 学生年龄
     */
    private String StuAge;

    /**
     * 学生性别
     */
    private String StuSex;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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