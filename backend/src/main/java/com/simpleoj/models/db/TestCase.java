package com.simpleoj.models.db;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "t_test_case")
public class TestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "problem_id")
    private Long problemId;

    @Column(name = "input")
    private String input;

    @Column(name = "expected")
    private String expected;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
}