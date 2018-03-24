package com.bjike.goddess.people4.to;

import com.bjike.goddess.common.api.to.BaseTO;

/**
 * 测试学生
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-19 10:07 ]
 * @Description: [ 测试学生 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public class StudentTO extends BaseTO {

    private com.bjike.goddess.people4.entity.aClass aClass;
    /**
     * 学生学号
     */
    private String stuNum;

    /**
     * 学生姓名
     */
    private String stuName;

    /**
     * 班级编号
     */
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