package com.bjike.goddess.people2.entity;

import com.bjike.goddess.common.api.entity.BaseEntity;

import javax.persistence.*;


/**
 * 测试图书类
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-05 05:18 ]
 * @Description: [ 测试图书类 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@Entity
@Table(name = "people2_Book")
public class Book extends BaseEntity {

    /**
     * 图书编号
     */
    @Column(name = "BookNum", nullable = false, columnDefinition = "VARCHAR(255)   COMMENT '图书编号'")
    private String BookNum;

    /**
     * 图书名称
     */
    @Column(name = "BookName", nullable = false, columnDefinition = "VARCHAR(255)   COMMENT '图书名称'")
    private String BookName;

    /**
     * 学生学号  StuNum ----- stuNum
     * @Column 一列
     *  口头上的关联
     *      映射到数据库
     *      主表 学生表
     *  stuNum 6
     *
     *      图书表
     *    stuNum  7
     *
     *
     *
     */
    @Column(name = "StuNum", nullable = false, columnDefinition = "VARCHAR(255)   COMMENT '图书名称'")
    private String StuNum;

    /**
     *  ManyToOne
     *
     *      JoinColumn( 外键 )
     *
     *
     */
    @ManyToOne(cascade = {CascadeType.ALL} , fetch = FetchType.EAGER)
    @JoinColumn(name = "stuId")
    private student student;

    public com.bjike.goddess.people2.entity.student getStudent() {
        return student;
    }

    public void setStudent(com.bjike.goddess.people2.entity.student student) {
        this.student = student;
    }

    public String getStuNum() {
        return StuNum;
    }

    public void setStuNum(String stuNum) {
        this.StuNum = stuNum;
    }

    public String getBookNum() {
        return BookNum;
    }

    public void setBookNum(String bookNum) {
        BookNum = bookNum;
    }

    public String getBookName() {
        return BookName;
    }

    public void setBookName(String bookName) {
        BookName = bookName;
    }
}