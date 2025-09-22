package com.example.java_web_learning.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.java_web_learning.pojo.Student;

@Mapper // 让SpringBoot知道这是一个Mapper接口, 也就是Service来操作Repository的途径, Spring Boot会将其视为一个Bean
public interface StudentMapper {

    // 新增学生信息
    Integer insertStudent(Student student);

    // 删除学生信息
    Integer deleteStudent(Long studentId);

    // 更新学生信息
    Integer updateStudent(Student student);

    // 按id查询某个学生
    Student selectById(Long studentId);

    // 查询所有学生
    List<Student> selectAll();
}
