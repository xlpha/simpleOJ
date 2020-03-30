package com.simpleoj.repositories;

import com.simpleoj.models.db.TestCase;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestCaseRepository extends CrudRepository<TestCase, Long> {
    @Query(nativeQuery = true)
    List<TestCase> findByProblemId(@Param("problem_id") Long problemId);
}