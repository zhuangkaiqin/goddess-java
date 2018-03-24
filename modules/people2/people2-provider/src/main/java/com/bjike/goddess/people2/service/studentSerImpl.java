package com.bjike.goddess.people2.service;

import com.bjike.goddess.common.api.dto.Restrict;
import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.jpa.service.ServiceImpl;
import com.bjike.goddess.common.utils.bean.BeanTransform;
import com.bjike.goddess.people2.bo.studentBO;
import com.bjike.goddess.people2.dto.studentDTO;
import com.bjike.goddess.people2.entity.Book;
import com.bjike.goddess.people2.entity.student;
import com.bjike.goddess.people2.to.studentTO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 测试学生类业务实现
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-05 05:18 ]
 * @Description: [ 测试学生类业务实现 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@CacheConfig(cacheNames = "people2SerCache")
@Service
public class studentSerImpl extends ServiceImpl<student, studentDTO> implements studentSer {
    @Override
    public List<studentBO> findStuAll() throws SerException {
        List<student> students = super.findAll();
        return BeanTransform.copyProperties(students,studentBO.class);
    }

    @Override
    public studentBO findStuByNum(String id) throws SerException {
        studentDTO dto = new studentDTO();
        dto.getConditions().add(Restrict.eq("StuNum",id));
        dto.getSorts().add("StuNum=desc");

        List<student> students = super.findByCis(dto);
        return BeanTransform.copyProperties(students.get(0),studentBO.class);
    }

    @Override
    public studentBO addStu(studentTO stuTO) throws SerException {
        student students = BeanTransform.copyProperties(stuTO,student.class,true);
        List<Book> books = BeanTransform.copyProperties(stuTO.getBookSet(),Book.class);
//        students.setBookSet(books);

        Set result = new HashSet(books);
        students.setBookSet(result);


        System.out.print(books);
        return BeanTransform.copyProperties(super.save(students),studentBO.class);
    }

    @Override
    public studentBO updateStu(studentTO stuTO) throws SerException {
        student students = super.findById(stuTO.getId());
        BeanTransform.copyProperties(stuTO,students,true);
        super.update(students);
        return BeanTransform.copyProperties(students,studentBO.class);
    }

    @Override
    public void deleteStu(String id) throws SerException {
        super.remove(id);
    }
}