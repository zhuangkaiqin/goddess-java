package com.bjike.goddess.people3.vo;

import com.bjike.goddess.people3.entity.Student;

import java.util.HashSet;
import java.util.Set;

/**
 * 测试班级类表现层对象
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-09 11:20 ]
 * @Description: [ 测试班级类表现层对象 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public class ClassVO {

    /**
     * id
     */
    private String id;
    /**
     * 班级编号
     */
    private String ClassNum;

    /**
     * 班级专业
     */
    private String ClassType;


    private Set<Student> studentSet = new HashSet<>();

    public Set<Student> getStudentSet() {
        return studentSet;
    }

    public void setStudentSet(Set<Student> studentSet) {
        this.studentSet = studentSet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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