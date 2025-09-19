package com.example.java_web_learning.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.java_web_learning.pojo.Student;

@RestController
public class ArgumentsTestController {

    @RequestMapping("/")
    public String studentPOJO(Student student) {
        System.out.print(student);
        return "OK!";
    }
}