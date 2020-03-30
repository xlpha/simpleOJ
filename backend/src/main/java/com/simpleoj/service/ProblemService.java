package com.simpleoj.service;

import com.simpleoj.models.db.Problem;
import com.simpleoj.repositories.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@CacheConfig(cacheNames = "problem")
public class ProblemService {

    @Autowired
    private ProblemRepository problemRepository;

    @Cacheable
    public Iterable<Problem> getProblems() {
        return problemRepository.findAll();
    }

    @Cacheable
    public Problem getProblemById(@PathVariable Long id) {
        return problemRepository.findById(id).orElse(null);
    }

}
