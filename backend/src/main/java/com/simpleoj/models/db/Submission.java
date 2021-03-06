package com.simpleoj.models.db;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "t_submission")
public class Submission implements Serializable {
    public final static int LANGUAGE_C = 0;
    public final static int LANGUAGE_CPP = 1;
    public final static int LANGUAGE_JAVA = 2;
    public final static int LANGUAGE_Python = 3;

    public final static int STATUS_QUEUE = -1;      //等待评测中
    public final static int STATUS_RUNNING = -2;    //正在评测中
    public final static int STATUS_SERVER_INTERNAL_ERROR = -3;  //服务器内部错误
    public final static int STATUS_Accept = 0;
    public final static int STATUS_CompileError = 1;
    public final static int STATUS_RuntimeError = 2;
    public final static int STATUS_TimeLimitExceed = 3;
    public final static int STATUS_MemoryLimitExceed = 4;
    public final static int STATUS_WrongAnswer = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "problem_id")
    private Long problemId;

    @Column(name = "language")
    private Integer language;

    @Column(name = "code")
    private String code;

    @Column(name = "status")
    private Integer status;

    @Column(name = "runtime")
    private Long runtime;

    @Column(name = "memory")
    private Long memory;

    @Column(name = "error")
    private String error;

    @Column(name = "input")
    private String input;

    @Column(name = "output")
    private String output;

    @Column(name = "expected")
    private String expected;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Date updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}