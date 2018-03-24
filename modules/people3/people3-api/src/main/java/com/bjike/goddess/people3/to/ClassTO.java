package com.bjike.goddess.people3.to;

import com.bjike.goddess.common.api.to.BaseTO;
import com.bjike.goddess.people3.entity.Student;

import java.util.List;

/**
 * 测试班级类
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-09 11:20 ]
 * @Description: [ 测试班级类 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public class ClassTO extends BaseTO {

    /**
     * 班级编号
     */
    private String ClassNum;

    /**
     * o
     * 班级专业
     */
    private String ClassType;

    private List<Student> students;

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public String getClassNum() {
        return ClassNum;
    }

    public void setClassNum(String ClassNum) {
        this.ClassNum = ClassNum;
    }

    public String getClassType() {
        return ClassType;
    }

    public void setClassType(String ClassType) {
        this.ClassType = ClassType;
    }
}