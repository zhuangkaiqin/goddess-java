package com.bjike.goddess.people3.entity;

import com.bjike.goddess.common.api.entity.BaseEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


/**
 * 测试班级类
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-09 11:20 ]
 * @Description: [ 测试班级类 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@Entity
@Table(name = "people3_class")
public class Class extends BaseEntity {

    @OneToMany(mappedBy = "aClass", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Set<Student> studentSet = new HashSet<>();
    /**
     * 班级编号
     */
    @Column(name = "ClassNum", nullable = false, columnDefinition = "VARCHAR(255)   COMMENT '班级编号'")
    private String ClassNum;

    /**
     * 班级专业
     */
    @Column(name = "ClassType", nullable = false, columnDefinition = "VARCHAR(255)   COMMENT '班级专业'")
    private String ClassType;


    public Set<Student> getStudentSet() {
        return studentSet;
    }

    public void setStudentSet(Set<Student> studentSet) {
        this.studentSet = studentSet;
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

    @Override
    public String toString() {
        return "Class{" +
                "studentSet=" + studentSet +
                ", ClassNum='" + ClassNum + '\'' +
                ", ClassType='" + ClassType + '\'' +
                '}';
    }
}