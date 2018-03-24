package com.bjike.goddess.people4.bo;

import com.bjike.goddess.common.api.bo.BaseBO;
import com.bjike.goddess.people4.entity.Student;

import java.util.Set;

/**
 * 测试班级业务传输对象
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-19 10:22 ]
 * @Description: [ 测试班级业务传输对象 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public class aClassBO extends BaseBO {

    /**
     * 班级编号
     */
    private String ClaNum;

    /**
     * 专业名称
     */
    private String ClaType;

    private Set<Student> students;

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }

    public String getClaNum() {
        return ClaNum;
    }

    public void setClaNum(String ClaNum) {
        this.ClaNum = ClaNum;
    }

    public String getClaType() {
        return ClaType;
    }

    public void setClaType(String ClaType) {
        this.ClaType = ClaType;
    }
}