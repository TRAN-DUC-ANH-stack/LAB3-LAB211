package service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import model.Course;
import repository.IFileHelper;
import validation.CourseValidator;

// Business logic only: this class never prints. Problems are returned (boolean, null, empty list,
// warning list, error message) and the Menu decides what to show.
public class CourseManager {

    private final Map<String, Course> courses = new LinkedHashMap<>();
    private final IFileHelper<Course> fileHelper;
    private final StudentManager studentManager; // to check that a Student ID exists
    private boolean dirty = false;               // unsaved changes (function 12)

    public CourseManager(IFileHelper<Course> fileHelper, StudentManager studentManager) {
        this.fileHelper = Objects.requireNonNull(fileHelper, "File helper must not be null.");
        this.studentManager = Objects.requireNonNull(studentManager, "Student manager must not be null.");
    }

    public boolean isDirty() {
        return dirty;
    }

    public List<Course> getAll() {
        return new ArrayList<>(courses.values());
    }

    // cse201 and CSE201 are the same course, so the ID is upper-cased before it is used as a key
    private static String key(String courseId) {
        return courseId.toUpperCase(Locale.ROOT);
    }

    public boolean exists(String courseId) {
        return courseId != null && courses.containsKey(key(courseId));
    }

    // C - function 6: false if the course is null, Course ID is duplicated or Student ID does not exist
    public boolean add(Course course) {
        if (course == null || exists(course.getCourseId()) || !studentManager.exists(course.getStudentId())) {
            return false;
        }
        courses.put(key(course.getCourseId()), course);
        dirty = true;
        return true;
    }

    // RF - function 7: all courses of one student (empty list if none or if the ID is null)
    public List<Course> findByStudent(String studentId) {
        List<Course> result = new ArrayList<>();
        if (studentId == null) {
            return result;
        }
        for (Course c : courses.values()) {
            if (c.getStudentId().equalsIgnoreCase(studentId)) {
                result.add(c);
            }
        }
        return result;
    }

    // Function 9: a student with courses cannot be deleted
    public boolean hasCoursesOf(String studentId) {
        return !findByStudent(studentId).isEmpty();
    }

    // Function 8: total weeks of all courses of one student
    public int totalDurationOf(String studentId) {
        int total = 0;
        for (Course c : findByStudent(studentId)) {
            total += c.getDurationWeeks();
        }
        return total;
    }

    // RF' - split one line of Courses.txt into a Course, null if the line is null or invalid.
    // Expected: CourseID, StudentID, CourseName, Weeks, dd/MM/yyyy (exactly 5 fields).
    // The "future date" rule is not checked here because old courses may already have started.
    public static Course parseLine(String line) {
        if (line == null) {
            return null;
        }
        String[] p = line.split(",", -1);
        if (p.length != 5) {
            return null;
        }
        String courseId = p[0].trim();
        String studentId = p[1].trim();
        String name = p[2].trim();
        String weeks = p[3].trim();
        String date = p[4].trim();
        if (CourseValidator.firstError(courseId, studentId, name, weeks) != null) {
            return null;
        }
        try {
            LocalDate start = LocalDate.parse(date, Course.DATE_FORMAT);
            return new Course(courseId, studentId, name, Integer.parseInt(weeks), start);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // L - call studentManager.load() first. Skips duplicated Course IDs and unknown students.
    // Returns the warnings (skipped lines / duplicates) for the Menu to display.
    public List<String> load() throws IOException {
        List<String> warnings = new ArrayList<>();
        List<Course> loaded = Objects.requireNonNull(fileHelper.load(warnings), "Loaded data must not be null.");
        courses.clear();
        for (Course c : loaded) {
            if (exists(c.getCourseId())) {
                warnings.add("Skipped course " + c.getCourseId() + ": duplicate Course ID.");
            } else if (!studentManager.exists(c.getStudentId())) {
                warnings.add("Skipped course " + c.getCourseId()
                        + ": Student ID " + c.getStudentId() + " does not exist.");
            } else {
                courses.put(key(c.getCourseId()), c);
            }
        }
        dirty = false;
        return warnings;
    }

    public String validateForSave() {
        int position = 0;
        for (Course c : courses.values()) { // a Map cannot hold two courses with the same ID
            position++;
            String where = "Invalid course at position " + position + " (" + c.getCourseId() + "): ";
            String error = CourseValidator.firstError(c.getCourseId(), c.getStudentId(),
                    c.getCourseName(), String.valueOf(c.getDurationWeeks()));
            if (error != null) {
                return where + error;
            }
            if (!studentManager.exists(c.getStudentId())) {
                return where + "Student ID " + c.getStudentId() + " does not exist.";
            }
        }
        return null;
    }

    // S - nothing is written unless all the data is valid (the Menu calls validateForSave() first)
    public void save() throws IOException {
        String error = validateForSave();
        if (error != null) {
            throw new IllegalStateException("Cannot save: " + error);
        }
        fileHelper.save(getAll());
        dirty = false;

    }
}
