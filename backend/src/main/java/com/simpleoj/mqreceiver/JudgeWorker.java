package com.simpleoj.mqreceiver;

import com.simpleoj.judgecore.IWorker;
import com.simpleoj.models.db.Problem;
import com.simpleoj.models.db.Submission;
import com.simpleoj.models.db.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JudgeWorker {
    private Submission submission;
    private Problem problem;
    private List<TestCase> testCases;
    private String baseDir;
    private IWorker worker;

    public void process() {
        String cwd = baseDir + File.separator + submission.getId();
        new File(cwd).mkdirs();
        try {
            worker.save(cwd, submission);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        boolean ok = worker.compile(cwd, submission);
        if (!ok) return;
        try {
            worker.run(cwd, problem, testCases, submission);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JudgeWorker(Submission submission, Problem problem, List<TestCase> testCases, String baseDir, IWorker worker) {
        this.submission = submission;
        this.problem = problem;
        this.testCases = testCases;
        this.baseDir = baseDir;
        this.worker = worker;
    }
}
