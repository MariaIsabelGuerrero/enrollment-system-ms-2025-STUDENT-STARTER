package com.champlain.courseservice.mapper;

import com.champlain.courseservice.dataaccesslayer.Course;
import com.champlain.courseservice.presentationlayer.CourseRequestModel;
import com.champlain.courseservice.presentationlayer.CourseResponseModel;

import java.util.UUID;

public class EntityModelMapper {

    public static CourseResponseModel toModel(Course course){
        return new CourseResponseModel(
                course.getCourseId(),
                course.getCourseNumber(),
                course.getCourseName(),
                course.getNumHours(),
                course.getNumCredits(),
                course.getDepartment()
        );
    }

    public static Course toEntity(CourseRequestModel courseRequestModel){
        var course = new Course();
        course.setCourseId(generateUUIDString());
        course.setCourseNumber(courseRequestModel.courseNumber());
        course.setCourseName(courseRequestModel.courseName());
        course.setNumHours(courseRequestModel.numHours());
        course.setNumCredits(courseRequestModel.numCredits());
        course.setDepartment(courseRequestModel.department());
        return course;

    }

    public static String generateUUIDString(){
        return UUID.randomUUID().toString();
    }
}