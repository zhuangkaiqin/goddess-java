package com.bjike.goddess.people2.bo;

import com.bjike.goddess.common.api.bo.BaseBO;

/**
 * 测试图书类业务传输对象
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-05 05:18 ]
 * @Description: [ 测试图书类业务传输对象 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public class BookBO extends BaseBO {

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