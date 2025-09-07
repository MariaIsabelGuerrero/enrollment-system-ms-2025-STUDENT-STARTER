package com.champlain.courseservice.businesslayer;

import com.champlain.courseservice.dataaccesslayer.Course;
import com.champlain.courseservice.dataaccesslayer.CourseRepository;
import com.champlain.courseservice.exceptionhandling.exceptions.NotFoundException;
import com.champlain.courseservice.presentationlayer.CourseRequestModel;
import com.champlain.courseservice.presentationlayer.CourseResponseModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CourseServiceUnitTest {

    @InjectMocks
    private CourseServiceImpl courseService;

    @Mock
    private CourseRepository courseRepository;



    Course course1 = Course.builder()
            .id(1)
            .courseId(UUID.randomUUID().toString())
            .courseNumber("cat-420")
            .courseName("Web Services")
            .numHours(45)
            .numCredits(3.0)
            .department("Computer Science")
            .build();

    Course course2 = Course.builder()
            .id(2)
            .courseId(UUID.randomUUID().toString())
            .courseNumber("cat-421")
            .courseName("Advanced Web Services")
            .numHours(45)
            .numCredits(3.0)
            .department("Computer Science")
            .build();

    Course course3 = Course.builder()
            .id(3)
            .courseId(UUID.randomUUID().toString())
            .courseNumber("cat-422")
            .courseName("Web Services Security")
            .numHours(45)
            .numCredits(3.0)
            .department("Computer Science")
            .build();

    @Test
    void addCourse_shouldReturnCourseResponseModel() {
        when(courseRepository.save(any(Course.class)))
                .thenReturn(Mono.just(course1));

        CourseRequestModel courseRequestModel = new CourseRequestModel(
                "cat-420",
                "Web Services Testing",
                45,
                0.0,
                "Computer Science"
        );

        Mono<CourseResponseModel> result = courseService.addCourse(Mono.just(courseRequestModel));

        StepVerifier
                .create(result)
                .expectNextMatches(courseResponseModel -> {
                    assertNotNull(courseResponseModel.courseId());
                    assertEquals(courseResponseModel.courseNumber(), courseRequestModel.courseNumber());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void whenGetAllCourses_thenReturnAllCourses() {
        when(courseRepository.findAll())
                .thenReturn(Flux.just(course1, course2, course3));

        Flux<CourseResponseModel> result = courseService.getAllCourses();

        StepVerifier
                .create(result)
                .expectNextMatches(courseResponseModel -> {
                    assertNotNull(courseResponseModel.courseId());
                    assertEquals(courseResponseModel.courseNumber(), course1.getCourseNumber());
                    return true;
                })
                .expectNextMatches(courseResponseModel -> courseResponseModel.courseNumber().equals("cat-421"))
                .expectNextMatches(courseResponseModel -> courseResponseModel.courseNumber().equals("cat-422"))
                .verifyComplete();
    }

    @Test
    void getCourseByCourseId_withExistingId_thenReturnCourseResponseModel() {
        // Arrange
        when(courseRepository.findCourseByCourseId(course1.getCourseId()))
                .thenReturn(Mono.just(course1));

        // Act
        Mono<CourseResponseModel> result = courseService.getCourseByCourseId(course1.getCourseId());

        // Assert
        StepVerifier
                .create(result)
                .expectNextMatches(courseResponseModel -> {
                    assertEquals(course1.getCourseId(), courseResponseModel.courseId());
                    assertEquals(course1.getCourseNumber(), courseResponseModel.courseNumber());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void getCourseByCourseId_withNonExistingId_thenThrowNotFoundException() {
        // Arrange
        String nonExistingId = UUID.randomUUID().toString();
        when(courseRepository.findCourseByCourseId(nonExistingId))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier
                .create(courseService.getCourseByCourseId(nonExistingId))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void updateCourseByCourseId_withExistingCourseId_thenReturnUpdatedCourseResponseModel() {
        // Arrange
        CourseRequestModel updateRequest = new CourseRequestModel(
                "cat-420-updated",
                "Updated Web Services",
                60,
                4.0,
                "Computer Science"
        );

        Course updatedCourse = Course.builder()
                .id(course1.getId())
                .courseId(course1.getCourseId())
                .courseNumber(updateRequest.courseNumber())
                .courseName(updateRequest.courseName())
                .numHours(updateRequest.numHours())
                .numCredits(updateRequest.numCredits())
                .department(updateRequest.department())
                .build();

        when(courseRepository.findCourseByCourseId(course1.getCourseId()))
                .thenReturn(Mono.just(course1));
        when(courseRepository.save(any(Course.class)))
                .thenReturn(Mono.just(updatedCourse));

        // Act
        Mono<CourseResponseModel> result = courseService.updateCourseByCourseId(
                Mono.just(updateRequest), course1.getCourseId());

        // Assert
        StepVerifier
                .create(result)
                .expectNextMatches(courseResponseModel -> {
                    assertEquals(updateRequest.courseNumber(), courseResponseModel.courseNumber());
                    assertEquals(updateRequest.courseName(), courseResponseModel.courseName());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void updateCourseByCourseId_withNonExistingCourseId_thenThrowNotFoundException() {
        // Arrange
        String nonExistingId = UUID.randomUUID().toString();
        CourseRequestModel updateRequest = new CourseRequestModel(
                "cat-999", "Non-existing", 45, 3.0, "Computer Science");

        when(courseRepository.findCourseByCourseId(nonExistingId))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier
                .create(courseService.updateCourseByCourseId(Mono.just(updateRequest), nonExistingId))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void deleteCourseByCourseId_withExistingCourseId_ReturnsDeletedCourseId() {
        // Arrange
        when(courseRepository.findCourseByCourseId(course1.getCourseId()))
                .thenReturn(Mono.just(course1));
        when(courseRepository.delete(course1))
                .thenReturn(Mono.empty());

        // Act
        Mono<CourseResponseModel> result = courseService.deleteCourseByCourseId(course1.getCourseId());

        // Assert
        StepVerifier
                .create(result)
                .expectNextMatches(courseResponseModel -> {
                    assertEquals(course1.getCourseId(), courseResponseModel.courseId());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void deleteCourseByCourseId_withNonExistingCourseId_thenThrowNotFoundException() {
        // Arrange
        String nonExistingId = UUID.randomUUID().toString();
        when(courseRepository.findCourseByCourseId(nonExistingId))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier
                .create(courseService.deleteCourseByCourseId(nonExistingId))
                .expectError(NotFoundException.class)
                .verify();
    }
}