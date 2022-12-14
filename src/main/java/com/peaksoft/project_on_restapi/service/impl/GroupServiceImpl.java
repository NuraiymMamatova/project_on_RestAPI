package com.peaksoft.project_on_restapi.service.impl;

import com.peaksoft.project_on_restapi.converter.request.GroupRequestConverter;
import com.peaksoft.project_on_restapi.converter.response.GroupResponseConverter;
import com.peaksoft.project_on_restapi.dto.request.GroupRequest;
import com.peaksoft.project_on_restapi.dto.response.CourseResponse;
import com.peaksoft.project_on_restapi.dto.response.GroupResponse;
import com.peaksoft.project_on_restapi.dto.response.UserResponse;
import com.peaksoft.project_on_restapi.model.entity.Course;
import com.peaksoft.project_on_restapi.model.entity.Group;
import com.peaksoft.project_on_restapi.model.entity.Instructor;
import com.peaksoft.project_on_restapi.model.entity.Student;
import com.peaksoft.project_on_restapi.repository.CourseRepository;
import com.peaksoft.project_on_restapi.repository.GroupRepository;
import com.peaksoft.project_on_restapi.service.CourseService;
import com.peaksoft.project_on_restapi.service.GroupService;
import com.peaksoft.project_on_restapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    private final CourseRepository courseRepository;

    private final UserService userService;

    private final GroupRequestConverter groupRequestConverter;

    private final GroupResponseConverter groupResponseConverter;

    @Override
    public GroupResponse saveGroup(Long courseId, GroupRequest groupRequest) {
        Course course = courseRepository.findById(courseId).get();
        Group group = groupRequestConverter.saveGroup(groupRequest);
        course.addGroup(group);
        group.addCourse(course);
        groupRepository.save(group);
        return groupResponseConverter.viewGroup(group);
    }

    @Override
    public GroupResponse deleteGroupById(Long groupId) {
        Group group = groupRepository.findById(groupId).get();
        //
        List<Student> students = group.getStudents();
        Long count = students.stream().count();
        for (Course course : group.getCourses()) {
            Long count1 = course.getCompany().getCount();
            count1 -= count;
            course.getCompany().setCount(count1);
            for (Instructor instructor : course.getInstructors()) {
                Long count2 = instructor.getCount();
                count2 -= count;
                instructor.setCount(count2);
            }
        }
        for (Student student : group.getStudents()) {
            UserResponse user = userService.findUserByEmail(student.getEmail());
            userService.deleteUserById(Long.valueOf(user.getId()));
        }
        //
        groupRepository.delete(group);
        return groupResponseConverter.viewGroup(group);
    }

    @Override
    public GroupResponse updateGroup(Long groupId, GroupRequest groupRequest) {
        Group group = groupRepository.findById(groupId).get();
        groupRequestConverter.update(group, groupRequest);
        return groupResponseConverter.viewGroup(groupRepository.save(group));
    }

    @Override
    public GroupResponse findGroupById(Long groupId) {
        Group group = groupRepository.findById(groupId).get();
        return groupResponseConverter.viewGroup(group);
    }

    @Override
    public List<GroupResponse> viewAllGroups() {
        return groupResponseConverter.viewAllGroup(groupRepository.findAll());
    }

    @Override
    public void assignGroupToCourse(Long groupId, Long courseId) {
        if (groupId != null) {
            Group group = groupRepository.findById(groupId).get();
            if (courseId != null) {
                Course course = courseRepository.findById(courseId).get();
                group.addCourse(course);
                course.addGroup(group);
                courseRepository.save(course);
            }else {
                System.out.println("course id is null");
            }
        }else {
            System.out.println("group id is null");
        }

    }

}
