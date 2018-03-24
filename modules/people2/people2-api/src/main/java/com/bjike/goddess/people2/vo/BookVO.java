package com.bjike.goddess.people2.vo;

/**
 * 测试图书类表现层对象
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-05 05:18 ]
 * @Description: [ 测试图书类表现层对象 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public class BookVO {

    /**
     * id
     */
    private String id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
