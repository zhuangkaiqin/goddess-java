package com.bjike.goddess.people.bo;

import com.bjike.goddess.common.api.bo.BaseBO;

/**
 * 测试学生类业务传输对象
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-02 11:32 ]
 * @Description: [ 测试学生类业务传输对象 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public class studentBO extends BaseBO {

    /**
     * 学生id
     */
    private String stuId;

    /**
     * 学生姓名
     */
    private String stuName;

    /**
     * 学生年龄
     */
    private String stuAge;

    /**
     * 学生性别
     */
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