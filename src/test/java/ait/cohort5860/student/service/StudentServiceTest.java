package ait.cohort5860.student.service;

import ait.cohort5860.configuration.ServiceConfiguration;
import ait.cohort5860.student.dao.StudentRepository;
import ait.cohort5860.student.dto.ScoreDto;
import ait.cohort5860.student.dto.StudentCredentialsDto;
import ait.cohort5860.student.dto.StudentDto;
import ait.cohort5860.student.dto.StudentUpdateDto;
import ait.cohort5860.student.dto.exceptions.NotFoundException;
import ait.cohort5860.student.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


// AAA - Arrange, Act, Assert - подготовка, действие, подтверждение.

@ContextConfiguration(classes = {ServiceConfiguration.class})
@SpringBootTest
public class StudentServiceTest {
    private final long studentId = 1000L;
    private final String name = "John";
    private final String password = "<PASSWORD>";
    private Student student;

    @Autowired
    private ModelMapper modelMapper;

    @MockitoBean
    private StudentRepository studentRepository;

    private StudentService studentService;

    @BeforeEach
    public void setUp() {
        student = new Student(studentId, name, password);
        studentService = new StudentServiceImpl(studentRepository, modelMapper);
    }

    @Test
    void testAddStudentWhenStudentDoesNotExist() {
        // Arrange
        StudentCredentialsDto dto = new StudentCredentialsDto(studentId, name, password);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        // Act
        boolean result = studentService.addStudent(dto);

        // Assert
        assertTrue(result);
    }

    @Test
    void testAddStudentWhenStudentExist() {
        // Arrange
        StudentCredentialsDto dto = new StudentCredentialsDto(studentId, name, password);
        when(studentRepository.existsById(dto.getId())).thenReturn(true);
        // Act
        boolean result = studentService.addStudent(dto);
        // Assert
        assertFalse(result);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void testFindStudentWhenStudentExist() {
        // Arrange
        when(studentRepository.findById(studentId)).thenReturn(Optional.ofNullable(student));

        // Act
        StudentDto studentDto = studentService.findStudent(studentId);

        // Assert
        assertNotNull(studentDto);
        assertEquals(studentId, studentDto.getId());
    }

    @Test
    void testFindStudentWhenStudentNotExist() {
        // Arrange
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> studentService.findStudent(studentId));
    }

    @Test
    void testRemoveStudent() {
        // Arrange
        when(studentRepository.findById(studentId)).thenReturn(Optional.ofNullable(student));

        // Act
        StudentDto studentDto = studentService.removeStudent(studentId);

        // Assert
        assertNotNull(studentDto);
        assertEquals(studentId, studentDto.getId());
        verify(studentRepository, times(1)).deleteById(studentId);
    }

    @Test
    void testUpdateStudent() {
        String newName = "newName";
        when(studentRepository.findById(studentId)).thenReturn(Optional.ofNullable(student));
        StudentUpdateDto dto = new StudentUpdateDto("newName", null);

        StudentCredentialsDto studentCredentialsDto = studentService.updateStudent(studentId, dto);

        assertNotNull(studentCredentialsDto);
        assertEquals(studentId, studentCredentialsDto.getId());
        assertEquals(newName, studentCredentialsDto.getName());
        assertEquals(password, studentCredentialsDto.getPassword());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void testAddScore() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.ofNullable(student));
        String examName = "Math";
        int score = 90;
        ScoreDto scoreDto = new ScoreDto(examName, score);

        Boolean result = studentService.addScore(studentId, scoreDto);

        assertTrue(result);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void testFindStudentsByName() {
        when(studentRepository.findByNameIgnoreCase(name)).thenReturn(Stream.of(student));

        List<StudentDto> result = studentService.findStudentsByName(name);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(name, result.get(0).getName());
    }

    @Test
    void testGetStudentsQuantityByNames() {
        Set<String> names = Set.of("John", "Peter");
        when(studentRepository.countByNameInIgnoreCase(names)).thenReturn(2L);

        Long result = studentService.countStudentsByNames(names);

        assertEquals(2L, result);
    }

    @Test
    void testFindStudentsByExamMinScore() {
        String examName = "Math";
        int score = 90;
        when(studentRepository.findByExamAndScoresGreaterThan(examName, score)).thenReturn(Stream.of(student));

        List<StudentDto> result = studentService.findStudentsByExamNameMinScore(examName, score);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(name, result.get(0).getName());
    }

}
