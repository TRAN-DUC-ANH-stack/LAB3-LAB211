package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Objects;

public class Course {

    public static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final String courseId;
    private final String studentId;
    private final String courseName;
    private final int durationWeeks;
    private final LocalDate startDate;

    public Course(String courseId, String studentId, String courseName,
                  int durationWeeks, LocalDate startDate) {
        this.courseId = Objects.requireNonNull(courseId, "Course ID must not be null.");
        this.studentId = Objects.requireNonNull(studentId, "Student ID must not be null.");
        this.courseName = Objects.requireNonNull(courseName, "Course name must not be null.");
        this.durationWeeks = durationWeeks;
        this.startDate = Objects.requireNonNull(startDate, "Start date must not be null.");
    }

    public String getCourseId() {
        return courseId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getDurationWeeks() {
        return durationWeeks;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Course other = (Course) o;
        return courseId.equalsIgnoreCase(other.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId.toUpperCase(Locale.ROOT));
    }
    
    @Override
    public String toString() {
        return String.format("%-10s | %-25s | %3d weeks | %s",
                courseId, courseName, durationWeeks, startDate.format(DATE_FORMAT));
    }
}
