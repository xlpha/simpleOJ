package com.simpleoj.service;

import com.simpleoj.models.db.Submission;
import com.simpleoj.repositories.SubmissionRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@CacheConfig(cacheNames = "submit")
public class SubmissionService {
    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Cacheable
    public Submission getSubmissionById(@PathVariable Long id) {
        return submissionRepository.findById(id).orElse(null);
    }

    @CachePut(key = "#submission.id")
    public Submission postSubmission(@RequestBody Submission submission) {
        submission.setStatus(Submission.STATUS_QUEUE);
        Submission ret = submissionRepository.save(submission);
        rabbitTemplate.convertAndSend("simpleOJ", "", ret.getId().toString());
        return ret;
    }
}
