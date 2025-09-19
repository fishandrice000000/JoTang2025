package com.example.java_web_learning;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.java_web_learning.mapper.StudentMapper;
import com.example.java_web_learning.pojo.Student;

@SpringBootTest
class JavaWebLearningApplicationTests {

	@Autowired
	private StudentMapper studentMapper;

	@Test
	public void testListStudent() {
		List<Student> studentList = studentMapper.list();
		for (Student stu : studentList) {
			System.out.println(stu);
		}
	}

}
