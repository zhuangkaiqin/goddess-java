package com.bjike.goddess.people3.dto;

import com.bjike.goddess.common.api.dto.BaseDTO;

/**
 * 测试学生类数据传输对象
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-09 11:13 ]
 * @Description: [ 测试学生类数据传输对象 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public class StudentDTO extends BaseDTO {
    /**
     * 班级编号
     */
    private String classNum;

    public String getClassNum() {
        return classNum;
    }

    public void setClassNum(String classNum) {
        this.classNum = classNum;
    }
}