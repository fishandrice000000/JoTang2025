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

    Long studentId;
    String name;

    public enum Gender {
        male, female
    }

    Short age;
}
