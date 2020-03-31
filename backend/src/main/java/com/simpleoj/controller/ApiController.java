package com.simpleoj.controller;

import com.simpleoj.models.db.Problem;
import com.simpleoj.models.db.Submission;
import com.simpleoj.repositories.SubmissionRepository;
import com.simpleoj.service.ProblemService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api")
public class ApiController {

    @Autowired
    private ProblemService problemService;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping(path = "/problem")
    public Iterable<Problem> getProblems() {
        return problemService.getProblems();
    }

    @GetMapping(path = "/problem/{id}")
    public Problem getProblemById(@PathVariable Long id) {
        return problemService.getProblemById(id);
    }

    @GetMapping(path = "/submission/{id}")
    public Submission getSubmissionById(@PathVariable Long id) {

        return submissionRepository.findById(id).orElse(null);
    }

    @PostMapping(path = "/submission")
    public Submission postSubmission(@RequestBody Submission submission) {
        submission.setStatus(Submission.STATUS_QUEUE);
        Submission ret = submissionRepository.save(submission);
        rabbitTemplate.convertAndSend("simpleOJ", "", ret.getId().toString());
        return ret;
    }
}
