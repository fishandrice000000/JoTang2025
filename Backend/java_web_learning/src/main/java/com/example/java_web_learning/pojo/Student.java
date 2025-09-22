package com.example.java_web_learning.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Student {

    public enum Gender {
        male, female
    }

    private Long studentId;
    private String name;
    private Gender gender;
    private Short age;
}
