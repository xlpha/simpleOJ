package com.simpleoj.judgecore;

import com.simpleoj.models.db.Problem;
import com.simpleoj.models.db.Submission;
import com.simpleoj.models.db.TestCase;

import java.io.IOException;
import java.util.List;

public interface IWorker {
    void save(String cwd, Submission submission) throws IOException;

    boolean compile(String cwd, Submission submission);

    void run(String cwd, Problem problem, List<TestCase> testCases, Submission submission) throws RuntimeException, IOException;

}
