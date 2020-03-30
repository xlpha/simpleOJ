package com.simpleoj.mqreceiver;

import com.simpleoj.judgecore.IWorker;
import com.simpleoj.judgecore.impl.JavaWorker;
import com.simpleoj.models.db.Problem;
import com.simpleoj.models.db.Submission;
import com.simpleoj.models.db.TestCase;
import com.simpleoj.repositories.ProblemRepository;
import com.simpleoj.repositories.SubmissionRepository;
import com.simpleoj.repositories.TestCaseRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubmissionReceiver {
    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Value("${simple_oj.basedir}")
    private String baseDir;

    @RabbitListener(queues = "submission")
    public void handleSubmitMsg(String msg) {
        long id = Long.parseLong(msg);
        Submission submission = submissionRepository.findById(id).orElse(null);
        if (submission == null) return;
        submission.setStatus(Submission.STATUS_RUNNING);
        Problem problem = problemRepository.findById(submission.getProblemId()).orElse(null);
        if (problem == null) return;
        List<TestCase> testCases = testCaseRepository.findByProblemId(problem.getId());

        IWorker worker;
        switch (submission.getLanguage()) {
            case Submission.LANGUAGE_JAVA:
                worker = new JavaWorker();
                break;
            default:
                return;
        }

        JudgeWorker judgeWorker = new JudgeWorker(submission, problem, testCases, baseDir, worker);
        judgeWorker.process();

//        submission.setStatus(Submission.STATUS_AC);
        submissionRepository.save(submission);
    }

    private void process() {

    }
}
