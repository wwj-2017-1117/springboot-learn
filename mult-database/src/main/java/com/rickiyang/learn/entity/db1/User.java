package com.rickiyang.learn.entity.db1;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private Long id;

    private String name;

    private Integer age;

    private Integer sex;

    private String phone;

}