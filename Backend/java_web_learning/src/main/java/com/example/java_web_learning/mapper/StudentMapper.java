package com.example.java_web_learning.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.example.java_web_learning.pojo.Student;

@Mapper // 让SpringBoot知道这是一个Mapper接口, 也就是Service来操作Repository的途径
@Repository    // 将SpringBoot知道这个接口属于Repository层, 是一个Bean
public interface StudentMapper {

    // 新增学生信息
    public Integer insertStudent(Student student);

    // 删除学生信息
    public Integer deleteStudent(Long id);

    // 更新学生信息
    public Integer updateStudent(Student student);

    // 按id查询某个学生
    public Student selectById(Long id);

    // 查询所有学生
    public List<Student> selectAll();
}
