package com.bjike.goddess.people2.api;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.people2.bo.BookBO;
import com.bjike.goddess.people2.service.BookSer;
import com.bjike.goddess.people2.to.BookTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试图书类业务接口实现
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-05 05:18 ]
 * @Description: [ 测试图书类业务接口实现 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@Service("BookApiImpl")
public class BookApiImpl implements BookAPI {

    @Autowired
    private BookSer bookSer;

    @Override
    public List<BookBO> findBookAll() throws SerException {
        return bookSer.findBookAll();
    }

    @Override
    public BookBO findBookByNum(String bookNum) throws SerException {
        return bookSer.findBookByNum(bookNum);
    }

    @Override
    public List<BookBO> findBookByStuNum(String StuNum) throws SerException {
        return bookSer.findBookByStuNum(StuNum);
    }

    @Override
    public BookBO addBook(BookTO BookTO) throws SerException {
        return bookSer.addBook(BookTO);
    }

    @Override
    public List<BookBO> findBookByStuNumandStuAge(String StuNum, String StuAge) throws SerException {
        return null;
    }

    @Override
    public BookBO updateBook(BookTO BookTO) throws SerException {
        return bookSer.updateBook(BookTO);
    }

    @Override
    public void deleteBook(String id) throws SerException {
        bookSer.deleteBook(id);
    }
}