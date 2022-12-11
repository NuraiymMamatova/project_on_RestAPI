package com.peaksoft.project_on_restapi.service.impl;

import com.peaksoft.project_on_restapi.converter.request.StudentRequestConverter;
import com.peaksoft.project_on_restapi.converter.response.StudentResponseConverter;
import com.peaksoft.project_on_restapi.dto.request.StudentRequest;
import com.peaksoft.project_on_restapi.dto.response.StudentResponse;
import com.peaksoft.project_on_restapi.model.entity.Course;
import com.peaksoft.project_on_restapi.model.entity.Group;
import com.peaksoft.project_on_restapi.model.entity.Instructor;
import com.peaksoft.project_on_restapi.model.entity.Student;
import com.peaksoft.project_on_restapi.repository.GroupRepository;
import com.peaksoft.project_on_restapi.repository.StudentRepository;
import com.peaksoft.project_on_restapi.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    private final GroupRepository groupRepository;

    private final StudentRequestConverter studentRequestConverter;

    private final StudentResponseConverter studentResponseConverter;


    @Override
    public StudentResponse saveStudent(StudentRequest studentRequest) {
        Student student = studentRequestConverter.saveStudent(studentRequest);
        studentRepository.save(student);
        return studentResponseConverter.viewStudent(student);
    }

    @Override
    public StudentResponse saveStudent(Long groupId, StudentRequest studentRequest) {
        Student student = studentRequestConverter.saveStudent(studentRequest);
        Group group = groupRepository.findById(groupId).get();
        //
        for (Course course : group.getCourses()) {
            course.getCompany().plus();
        }
        for (Course course : group.getCourses()) {
            for (Instructor instructor : course.getInstructors()) {
                instructor.plus();
            }
        }
        //
        group.addStudents(student);
        student.setGroup(group);
        studentRepository.save(student);
        return studentResponseConverter.viewStudent(student);
    }

    @Override
    public StudentResponse deleteStudentById(Long studentId) {
        Student student = studentRepository.findById(studentId).get();
        //
        for (Course course : student.getGroup().getCourses()) {
            course.getCompany().minus();
        }
        for (Course course : student.getGroup().getCourses()) {
            for (Instructor instructor : course.getInstructors()) {
                instructor.minus();
            }
        }
        // 
        studentRepository.delete(student);
        return studentResponseConverter.viewStudent(student);
    }

    @Override
    public StudentResponse updateStudent(Long studentId, StudentRequest studentRequest) {
        Student student = studentRepository.findById(studentId).get();
        studentRequestConverter.update(student, studentRequest);
        return studentResponseConverter.viewStudent(studentRepository.save(student));
    }

    @Override
    public StudentResponse findStudentById(Long studentId) {
        Student student = studentRepository.findById(studentId).get();
        return studentResponseConverter.viewStudent(student);
    }

    @Override
    public List<StudentResponse> viewAllStudents() {
        return studentResponseConverter.viewAllStudent(studentRepository.findAll());
    }

    @Override
    public List<Student> getAllStudents(Long id) {
        return null;
    }

    @Override
    public void assignStudentToGroup(Long studentId, Long groupId) {
        if (studentId != null) {
            Student student = studentRepository.findById(studentId).get();
            if (groupId != null) {
                Group group = groupRepository.findById(groupId).get();
                student.setGroup(group);
                group.addStudents(student);
                groupRepository.save(group);
                studentRepository.save(student);
            }
        }
    }
}
