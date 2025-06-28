package ait.cohort5860.student.dao;

import ait.cohort5860.student.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface StudentRepository extends MongoRepository<Student, Long> {
    Stream<Student> findByNameIgnoreCase(String name);

    @Query("{'scores.?0': {'$gt': ?1}}")
    Stream<Student> findByExamAndScoresGreaterThan(String examName, Integer score);

    Long countByNameInIgnoreCase(Set<String> names);
}
