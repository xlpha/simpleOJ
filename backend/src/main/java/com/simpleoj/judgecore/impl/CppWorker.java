package com.simpleoj.judgecore.impl;

import com.simpleoj.judgecore.IWorker;
import com.simpleoj.models.db.Problem;
import com.simpleoj.models.db.Submission;
import com.simpleoj.models.db.TestCase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CppWorker implements IWorker {

    @Override
    public void save(String cwd, Submission submission) throws IOException {
        Files.write(Paths.get(cwd, "main.cpp"), submission.getCode().getBytes());
    }

    @Override
    public boolean compile(String cwd, Submission submission) throws RuntimeException {

        return false;
    }

    @Override
    public void run(String cwd, Problem problem, List<TestCase> testCases, Submission submission) throws RuntimeException {

    }
}
