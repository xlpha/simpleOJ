package com.simpleoj.repositories;

import com.simpleoj.models.db.Problem;
import org.springframework.data.repository.CrudRepository;

public interface ProblemRepository extends CrudRepository<Problem, Long> {
}