package com.bjike.goddess.people2.service;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.api.service.Ser;
import com.bjike.goddess.people2.bo.BookBO;
import com.bjike.goddess.people2.dto.BookDTO;
import com.bjike.goddess.people2.entity.Book;
import com.bjike.goddess.people2.to.BookTO;

import java.util.List;

/**
 * 测试图书类业务接口
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-05 05:18 ]
 * @Description: [ 测试图书类业务接口 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public interface BookSer extends Ser<Book, BookDTO> {
    /**
     * 获取图书表中的信息
     *
     * @return
     * @throws SerException
     */
    default List<BookBO> findBookAll() throws SerException {
        return null;
    }

    /**
     * 根据图书编号查找图书
     *
     * @param id
     * @return
     * @throws SerException
     */
    default BookBO findBookByNum(String bookNum) throws SerException {
        return null;
    }

    /**
     * 根据学生编号查找图书信息
     *
     * @param StuNum
     * @return
     * @throws SerException
     */
    default List<BookBO> findBookByStuNum(String StuNum) throws SerException {
        return null;
    }

    /**
     * 根据学号和学生年龄查找图书信息
     *
     * @param StuNum
     * @param StuAge
     * @return
     * @throws SerException
     */
    default List<BookBO> findBookByStuNumandStuAge(String StuNum, String StuAge) throws SerException {
        return null;
    }

    /**
     * 添加一个图书
     *
     * @return
     * @throws SerException
     */
    default BookBO addBook(BookTO BookTO) throws SerException {
        return null;
    }

    /**
     * 更新一个图书
     *
     * @return
     * @throws SerException
     */
    default BookBO updateBook(BookTO BookTO) throws SerException {
        return null;
    }

    /**
     * 删除一个图书
     *
     * @throws SerException
     */
    default void deleteBook(String id) throws SerException {

    }
}