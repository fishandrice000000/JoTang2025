package com.example.java_web_learning.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.java_web_learning.pojo.Student;
import com.example.java_web_learning.service.StudentService;


@RestController("/student")
public class StudentController {

    // Controller调用Service层
    @Autowired
    private StudentService stuService; 
    
    // 新增学生
    @PostMapping("/insert")
    public Integer insertStudent(@RequestBody Student stu) {
        System.out.print("Controller: Insert student: " + stu);
        return stuService.insertStudent(stu);
    }

    // 删除学生
    @PostMapping("/delete")
    public Integer deleteStudent(@RequestBody Long id) {
        System.out.print("Controller: Delete student:" + id);
        return stuService.deleteStudent(id);
    }

    // 更新学生
    @PostMapping("/update")
    public Integer updateStudent(@RequestBody Student stu) {
        System.out.print("Controller: Update student:" + stu.getStudentId());
        return stuService.updateStudent(stu);
    }

    // 按ID查询学生
    @PostMapping("/selectById")
    public Student selectById(@RequestBody Long id) {
        Student stu = stuService.selectById(id);
        System.out.print("Controller: The student is" + stu);
        return stu;
    }

    // 查询所有学生
    @GetMapping("/selectAll")
    public List<Student> selectAll() {
        List<Student> stus = stuService.selectAll();
        System.out.print("Controller: All Students are:" + stus);
        return stus;
    }
}
