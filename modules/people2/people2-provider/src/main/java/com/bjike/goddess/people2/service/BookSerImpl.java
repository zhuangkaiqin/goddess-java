package com.bjike.goddess.people2.service;

import com.bjike.goddess.common.api.dto.Restrict;
import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.jpa.service.ServiceImpl;
import com.bjike.goddess.common.utils.bean.BeanTransform;
import com.bjike.goddess.people2.bo.BookBO;
import com.bjike.goddess.people2.bo.studentBO;
import com.bjike.goddess.people2.dto.BookDTO;
import com.bjike.goddess.people2.dto.studentDTO;
import com.bjike.goddess.people2.entity.Book;
import com.bjike.goddess.people2.entity.student;
import com.bjike.goddess.people2.to.BookTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试图书类业务实现
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-05 05:18 ]
 * @Description: [ 测试图书类业务实现 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@CacheConfig(cacheNames = "people2SerCache")
@Service
public class BookSerImpl extends ServiceImpl<Book, BookDTO> implements BookSer {

    @Autowired
    private studentSer studentSer;

    @Override
    public List<BookBO> findBookAll() throws SerException {
        List<Book> books = super.findAll();
        return BeanTransform.copyProperties(books, BookBO.class);
    }

    @Override
    public BookBO findBookByNum(String BookNum) throws SerException {
        BookDTO dto = new BookDTO();
        dto.getConditions().add(Restrict.eq("BookNum", BookNum));
        dto.getSorts().add("BookNum=desc");

        List<Book> books = super.findByCis(dto);
        Book book2 = books.get(0);
        return BeanTransform.copyProperties(book2, BookBO.class);
    }

    @Override
    public List<BookBO> findBookByStuNumandStuAge(String StuNum, String StuAge) throws SerException {

        //先根据StuNum和StuAge获取student类
        studentDTO stuDTO = new studentDTO();
        stuDTO.getConditions().add(Restrict.eq("student.StuNum", StuNum));
        stuDTO.getConditions().add(Restrict.eq("student.StuAge", StuAge));
        stuDTO.getSorts().add("student.StuNum=desc");
        student stu = studentSer.findByCis(stuDTO).get(0);

        //再根据student的stuNum来获取寻找这个图书
        BookDTO bookDTO = new BookDTO();
        bookDTO.getConditions().add(Restrict.eq("StuNum", stu.getStuNum()));
        bookDTO.getSorts().add("StuNum=desc");
        List<Book> books = super.findByCis(bookDTO);


        return BeanTransform.copyProperties(books.get(0), BookBO.class);
    }

    @Override
    public List<BookBO> findBookByStuNum(String StuNum) throws SerException {

        BookDTO dto = new BookDTO();
        dto.getConditions().add(Restrict.eq("StuNum", StuNum));
        dto.getSorts().add("StuNum=desc");
        List<Book> books = super.findByCis(dto);

        return BeanTransform.copyProperties(books.get(0), BookBO.class);
    }

    @Override
    public BookBO addBook(BookTO BookTO) throws SerException {
        String stuNum = BookTO.getStuNum();
        System.out.print("找出学号为BookTO中学号为StuNum的student");


        studentBO studentBO = studentSer.findStuByNum(stuNum);

        student student = BeanTransform.copyProperties(studentBO,student.class);


        //找出学号为BookTO中学号为stuNum的student
//        studentDTO dto = new studentDTO();
//        dto.getConditions().add(Restrict.eq("student.StuNum", stuNum));
//        dto.getSorts().add("stuNum=desc");
//        List<student> students = studentSer.findByCis(dto);
//
//        student student2 = students.get(0);
//
//        System.out.print("添加book的时候也把Student添加进去");
//        //添加book的时候也把Student添加进去
        Book books = BeanTransform.copyProperties(BookTO, Book.class, true);

        return BeanTransform.copyProperties(super.save(books), BookBO.class);
    }

    @Override
    public BookBO updateBook(BookTO BookTO) throws SerException {
        Book books = super.findById(BookTO.getId());
        BeanTransform.copyProperties(BookTO, books, true);
        super.update(books);
        return BeanTransform.copyProperties(books, BookBO.class);
    }

    @Override
    public void deleteBook(String id) throws SerException {
        super.remove(id);
    }
}