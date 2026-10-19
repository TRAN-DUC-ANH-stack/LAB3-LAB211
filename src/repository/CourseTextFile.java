package repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import model.Course;

public class CourseTextFile implements IFileHelper<Course> {

    private final Path path;
    private final Function<String, Course> parser; // CourseManager::parseLine (RF')

    public CourseTextFile(String filePath, Function<String, Course> parser) {
        this.path = Paths.get(Objects.requireNonNull(filePath, "File path must not be null."));
        this.parser = Objects.requireNonNull(parser, "Line parser must not be null.");
    }

    // Line format: <Course ID>, <Student ID>, <Course Name>, <Weeks>, <dd/MM/yyyy>
    // Invalid lines are skipped and reported in warnings (this class never prints).
    @Override
    public List<Course> load(List<String> warnings) throws IOException {
        Objects.requireNonNull(warnings, "Warning list must not be null.");
        List<Course> courses = new ArrayList<>();
        if (!Files.exists(path)) {
            return courses;
        }
        for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
            if (line.trim().isEmpty()) {
                continue;
            }
            Course course = parser.apply(line);
            if (course == null) {
                warnings.add("Skipped invalid course line: " + line);
            } else {
                courses.add(course);
            }
        }
        return courses;
    }

    @Override
    public void save(List<Course> list) throws IOException {
        Objects.requireNonNull(list, "Course list must not be null.");
        List<String> lines = new ArrayList<>();
        for (Course c : list) {
            lines.add(c.getCourseId() + ", " + c.getStudentId() + ", " + c.getCourseName() + ", "
                    + c.getDurationWeeks() + ", " + c.getStartDate().format(Course.DATE_FORMAT));
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
    }
}
