package com.bjike.goddess.people2.action.people2;

import com.bjike.goddess.common.api.exception.ActException;
import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.api.restful.Result;
import com.bjike.goddess.common.consumer.restful.ActResult;
import com.bjike.goddess.people2.api.BookAPI;
import com.bjike.goddess.people2.bo.BookBO;
import com.bjike.goddess.people2.to.BookTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试图书类
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-05 05:18 ]
 * @Description: [ 测试图书类 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@RestController
@RequestMapping("Book")
public class BookAction {
    @Autowired
    private BookAPI BookAPI;

    /**
     * 增加图书
     *
     * @param bookTO
     * @return
     * @throws ActException
     */
    @PostMapping("v1/addBook")
    public Result addBook(BookTO bookTO) throws ActException {
        try {
            Boolean result = (null != BookAPI.addBook(bookTO));
            return ActResult.initialize(result);
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }

    /**
     * 删除图书
     *
     * @param id
     * @return
     * @throws ActException
     */
    @DeleteMapping("v1/deleteBook/{id}")
    public Result deleteBook(@PathVariable String id) throws ActException {
        try {
            BookAPI.deleteBook(id);
            return new ActResult("删除成功");
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }

    /**
     * 查找全部图书
     *
     * @return
     * @throws ActException
     */
    @GetMapping("v1/findBookall")
    public Result findBookAll() throws ActException {
        try {
            List<BookBO> BookBOs = BookAPI.findBookAll();
            return ActResult.initialize(BookBOs);
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }

    }

    /**
     * 根据图书id查找图书
     *
     * @param stuNum
     * @return
     * @throws ActException
     */
    @PostMapping("v1/findBookByNum/{bookNum}")
    public Result findBookByNum(@PathVariable String bookNum) throws ActException {
        try {
            BookBO bookBO = BookAPI.findBookByNum(bookNum);
            return ActResult.initialize(bookBO);
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }

    /**
     * 根据学生学号查找图书信息,学生有几本书
     *
     * @param stuNum
     * @return
     * @throws ActException
     */
    @PostMapping("v1/findBookByStuNum/{stuNum}")
    public Result findBookByStuNum(@PathVariable String stuNum) throws ActException{
        try {
            List<BookBO> bookBo = BookAPI.findBookByStuNum(stuNum);
            return ActResult.initialize(bookBo);
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }

    /**
     * 根据学号和学生年龄查找图书信息
     *
     * @param stuNum
     * @param stuAge
     * @return
     * @throws ActException
     */
    @PostMapping("v1/findBookByStuNumandStuAge/{stuNum}/{stuAge}")
    public Result findBookByStuNumandStuAge(@PathVariable String stuNum,@PathVariable String stuAge) throws ActException{
        try {
            List<BookBO> bookBo = BookAPI.findBookByStuNumandStuAge(stuNum,stuAge);
            return ActResult.initialize(bookBo);
        } catch (SerException se){
            throw new ActException(se.getMessage());
        }
    }
    /**
     * 更新一个图书信息
     *
     * @param BookTO
     * @return
     * @throws ActException
     */
    @PostMapping("v1/updateBook")
    public Result updateBook(BookTO BookTO) throws ActException {
        try {
            BookBO BookBO = BookAPI.updateBook(BookTO);
            return ActResult.initialize(BookBO);
        } catch (SerException se) {
            throw new ActException(se.getMessage());
        }
    }

}