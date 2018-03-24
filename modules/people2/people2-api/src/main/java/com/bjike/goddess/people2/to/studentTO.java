package com.bjike.goddess.people2.to;

import com.bjike.goddess.common.api.to.BaseTO;

import java.util.List;

/**
 * 测试学生类
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-05 05:18 ]
 * @Description: [ 测试学生类 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public class studentTO extends BaseTO {

    private List<BookTO> books;

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


    public List<BookTO> getBookSet() {
        return books;
    }

    public void setBookSet(List<BookTO> bookSet) {
        this.books = bookSet;
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