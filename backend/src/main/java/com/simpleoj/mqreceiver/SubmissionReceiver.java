package com.simpleoj.mqreceiver;

import com.simpleoj.judgecore.Judge;
import com.simpleoj.judgecore.languageconfig.*;
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

    private final LanguageConfig cConfig = new CConfig();
    private final LanguageConfig cppConfig = new CppConfig();
    private final LanguageConfig javaConfig = new JavaConfig();
    private final LanguageConfig pythonConfig = new PythonConfig();

    @RabbitListener(queues = "submission")
    public void handleSubmitMsg(String msg) {
        if (msg == null || msg.isEmpty()) return;
        long id = Long.parseLong(msg);
        Submission submission = submissionRepository.findById(id).orElse(null);
        if (submission == null) return;
        submission.setStatus(Submission.STATUS_RUNNING);
        submissionRepository.save(submission);
        Problem problem = problemRepository.findById(submission.getProblemId()).orElse(null);
        if (problem == null) return;
        List<TestCase> testCases = testCaseRepository.findByProblemId(problem.getId());

        LanguageConfig config;
        switch (submission.getLanguage()) {
            case Submission.LANGUAGE_C:
                config = cConfig;
                break;
            case Submission.LANGUAGE_CPP:
                config = cppConfig;
                break;
            case Submission.LANGUAGE_JAVA:
                config = javaConfig;
                break;
            case Submission.LANGUAGE_Python:
                config = pythonConfig;
                break;
            default:
                return;
        }

        Judge judge = new Judge(submission, problem, testCases, baseDir, config);
        judge.process(submissionRepository);
    }

}
