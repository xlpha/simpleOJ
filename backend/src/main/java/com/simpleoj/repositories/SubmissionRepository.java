package com.simpleoj.repositories;

import com.simpleoj.models.db.Submission;
import org.springframework.data.repository.CrudRepository;

public interface SubmissionRepository extends CrudRepository<Submission, Long> {
}
