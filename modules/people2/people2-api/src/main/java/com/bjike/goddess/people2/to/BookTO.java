package com.bjike.goddess.people2.to;

import com.bjike.goddess.common.api.to.BaseTO;

/**
 * 测试图书类
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-05 05:18 ]
 * @Description: [ 测试图书类 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public class BookTO extends BaseTO {

    /**
     * 书本编号
     */
    private String BookNum;

    /**
     * 书本名称
     */
    private String BookName;

    /**
     * 学生编号
     */
    private String stuNum;

    private studentTO studentTO;

    public com.bjike.goddess.people2.to.studentTO getStudentTO() {
        return studentTO;
    }

    public void setStudentTO(com.bjike.goddess.people2.to.studentTO studentTO) {
        this.studentTO = studentTO;
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

    public String getStuNum() {
        return stuNum;
    }

    public void setStuNum(String stuNum) {
        this.stuNum = stuNum;
    }
}