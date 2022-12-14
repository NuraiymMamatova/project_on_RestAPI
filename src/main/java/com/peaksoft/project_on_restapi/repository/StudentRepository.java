package com.peaksoft.project_on_restapi.repository;

import com.peaksoft.project_on_restapi.model.entity.Instructor;
import com.peaksoft.project_on_restapi.model.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query("select s from Student s where s.email = :email")
    Student findByEmail(String email);
    @Query("select s from Student s where s.group.id = :groupId")
    List<Student> getAllStudentsByGroupId(Long groupId);
}