package com.example.java_web_learning.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.java_web_learning.mapper.StudentMapper;
import com.example.java_web_learning.pojo.Student;

@Service
public class StudentService {

    // Service层通过Mapper接口操作Repository层
    @Autowired
    private StudentMapper stuMapper;

    // 新增学生
    public Integer insertStudent(Student stu) {
        System.out.print("Service: Insert student: " + stu);
        return stuMapper.insertStudent(stu);
    }

    // 删除学生
    public Integer deleteStudent(Long id) {
        System.out.print("Service: Delete student:" + id);
        return stuMapper.deleteStudent(id);
    }

    // 更新学生
    public Integer updateStudent(Student stu) {
        System.out.print("Service: Update student:" + stu.getStudentId());
        return stuMapper.updateStudent(stu);
    }

    // 按ID查询学生
    public Student selectById(Long id) {
        Student stu = stuMapper.selectById(id);
        System.out.print("Service: The student is" + stu);
        return stu;
    }

    // 查询所有学生
    public List<Student> selectAll() {
        List<Student> stus = stuMapper.selectAll();
        System.out.print("Service: All Students are:" + stus);
        return stus;
    }
}
