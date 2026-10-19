package repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import model.Student;
import validation.StudentValidator;

public class StudentTextFile implements IFileHelper<Student> {

    private final Path path;

    public StudentTextFile(String filePath) {
        this.path = Paths.get(Objects.requireNonNull(filePath, "File path must not be null."));
    }

    // Line format: <ID>, <Name>, <Major>, <GPA>
    // Invalid lines are skipped and reported in warnings (this class never prints).
    @Override
    public List<Student> load(List<String> warnings) throws IOException {
        Objects.requireNonNull(warnings, "Warning list must not be null.");
        List<Student> students = new ArrayList<>();
        if (!Files.exists(path)) {
            return students;
        }
        for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
            if (line.trim().isEmpty()) {
                continue;
            }
            Student student = parseLine(line);
            if (student == null) {
                warnings.add("Skipped invalid student line: " + line);
            } else {
                students.add(student);
            }
        }
        return students;
    }

    @Override
    public void save(List<Student> list) throws IOException {
        Objects.requireNonNull(list, "Student list must not be null.");
        List<String> lines = new ArrayList<>();
        for (Student s : list) {
            lines.add(s.getId() + ", " + s.getName() + ", " + s.getMajor() + ", " + s.getGpa());
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    // Returns null if the line breaks any Student constraint
    private Student parseLine(String line) {
        String[] p = line.split(",", -1);
        if (p.length != 4) {
            return null;
        }
        String id = p[0].trim();
        String name = p[1].trim();
        String major = p[2].trim();
        String gpa = p[3].trim();
        if (!StudentValidator.isValid(id, name, major, gpa)) {
            return null;
        }
        return new Student(id, name, major, Double.parseDouble(gpa));
    }
}
