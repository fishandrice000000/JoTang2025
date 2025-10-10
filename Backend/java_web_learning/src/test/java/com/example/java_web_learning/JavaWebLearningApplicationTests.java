package com.example.java_web_learning;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.java_web_learning.controller.StudentController;
import com.example.java_web_learning.pojo.Student;

@SpringBootTest
@Transactional // 确保每个测试方法完成后，数据库操作都会被回滚
class JavaWebLearningApplicationTests {

    @Autowired
    private StudentController studentController;

    private Student testStudent;

    @BeforeEach
    public void setup() {
        // 在每个测试方法执行前，准备好测试数据
        // 这样做可以确保每个测试都是在干净的环境中运行
        testStudent = new Student(100L, "泰斯特", Student.Gender.male, (short) 18);
        studentController.insertStudent(testStudent);
    }

    @Test
    void testSelectAllStudents() {
        // Arrange (准备) - setup方法已经准备好数据

        // Act (执行)
        List<Student> studentList = studentController.selectAll();

        // Assert (断言)
        assertNotNull(studentList, "查询结果列表不应为null");
        assertFalse(studentList.isEmpty(), "查询结果列表不应为空");
        // 无法精确断言所有学生，因为其他测试也可能插入数据
        // 但我们可以断言我们插入的测试学生存在
        assertTrue(studentList.stream().anyMatch(s -> s.getStudentId().equals(testStudent.getStudentId())),
                "查询结果中应包含测试学生");
    }

    @Test
    void testSelectStudentById() {
        // Arrange (准备) - setup方法已经准备好数据

        // Act (执行)
        Student foundStudent = studentController.selectById(testStudent.getStudentId());

        // Assert (断言)
        assertNotNull(foundStudent, "按ID查询到的学生不应为null");
        assertEquals(testStudent.getStudentId(), foundStudent.getStudentId(), "学生ID应匹配");
        assertEquals(testStudent.getName(), foundStudent.getName(), "学生姓名应匹配");
        assertEquals(testStudent.getAge(), foundStudent.getAge(), "学生年龄应匹配");
    }

    @Test
    void testUpdateStudent() {
        // Arrange (准备)
        Short newAge = (short) 20;
        Student updatedStudent = new Student(
                testStudent.getStudentId(), "苔丝特", Student.Gender.female, newAge
        );

        // Act (执行)
        studentController.updateStudent(updatedStudent);
        Student foundStudent = studentController.selectById(updatedStudent.getStudentId());

        // Assert (断言)
        assertNotNull(foundStudent, "更新后查询到的学生不应为null");
        assertEquals(newAge, foundStudent.getAge(), "学生年龄应已更新");
        assertEquals("苔丝特", foundStudent.getName(), "学生姓名应已更新");
        assertEquals(Student.Gender.female, foundStudent.getGender(), "学生性别应已更新");
    }

    @Test
    void testDeleteStudent() {
        // Arrange (准备) - setup方法已经准备好数据

        // Act (执行)
        studentController.deleteStudent(testStudent.getStudentId());
        Student deletedStudent = studentController.selectById(testStudent.getStudentId());

        // Assert (断言)
        assertNull(deletedStudent, "删除后学生对象应为null");
    }
}