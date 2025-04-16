package com.demo.springbootinterface.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author tfan
 * @Description
 * @Date 2025/4/16 18:05
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User implements Serializable {
    private Long id;
    private String username;
    private String password;
}
