package view;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import model.Course;
import model.Student;
import service.CourseManager;
import service.StudentManager;
import validation.CourseValidator;
import validation.DateValidator;
import validation.InputValidator;
import validation.StudentValidator;

public class Menu {

    private static final String NOT_FOUND = "Student ID does not exist!";

    private final InputValidator inputValidator = new InputValidator();
    private final StudentValidator studentValidator = new StudentValidator();
    private final CourseValidator courseValidator = new CourseValidator();
    private final DateValidator dateValidator = new DateValidator();

    private final StudentManager studentManager;
    private final CourseManager courseManager;

    public Menu(StudentManager studentManager, CourseManager courseManager) {
        this.studentManager = Objects.requireNonNull(studentManager, "Student manager must not be null.");
        this.courseManager = Objects.requireNonNull(courseManager, "Course manager must not be null.");
    }

    // ================= Start up and menu =================
    // Students must be loaded before courses (courses are checked against students)
    public void loadData() {
        try {
            printWarnings(studentManager.load());
            System.out.println("Loaded " + studentManager.getAll().size() + " students.");
            printWarnings(courseManager.load());
            System.out.println("Loaded " + courseManager.getAll().size() + " courses.");
        } catch (IOException e) {
            System.out.println("Error while loading data: " + e.getMessage());
        }
    }

    public int showMenu() {
        System.out.println();
        System.out.println("========== STUDENT & SHORT COURSE MANAGEMENT ==========");
        System.out.println(" 1. List all students");
        System.out.println(" 2. Add a new student");
        System.out.println(" 3. Search for a student by ID");
        System.out.println(" 4. Update a student by ID");
        System.out.println(" 5. List all students by major");
        System.out.println(" 6. Add a new course");
        System.out.println(" 7. List all courses by student (grouped)");
        System.out.println(" 8. Calculate total study duration by student ID");
        System.out.println(" 9. Remove a student by ID");
        System.out.println("10. Sort students by GPA");
        System.out.println("11. Save data to files");
        System.out.println("12. Quit program");
        System.out.println("=======================================================");
        return inputValidator.readInt("Enter your choice (1-12): ", 1, 12);
    }

    // ================= Function 1 =================
    public void listAllStudents() {
        List<Student> list = studentManager.getAll();
        if (list.isEmpty()) {
            System.out.println("No students to display.");
            return;
        }
        printStudentTable(list);
        System.out.println("Total: " + list.size() + " student(s).");
    }

    // ================= Function 2 =================
    public void addStudent() {
        System.out.println("--- ADD A NEW STUDENT ---");
        String id = inputValidator.readValid("Student ID (STU0000): ", s -> {
            String error = studentValidator.checkId(s);
            if (error == null && studentManager.exists(s)) {
                return "Student ID already exists.";
            }
            return error;
        });
        String name = inputValidator.readValid("Full name: ", studentValidator::checkName);
        String major = inputValidator.readValid("Major: ", studentValidator::checkMajor);
        double gpa = Double.parseDouble(
                inputValidator.readValid("GPA (0.0 - 4.0): ", studentValidator::checkGpa));

        if (studentManager.add(new Student(id, name, major, gpa))) {
            System.out.println("Student added successfully!");
        } else {
            System.out.println("Failed to add student.");
        }
    }

    // ================= Function 3 =================
    public void searchStudent() {
        Student student = studentManager.findById(inputValidator.readString("Enter Student ID: "));
        if (student == null) {
            System.out.println(NOT_FOUND);
            return;
        }
        printStudentTable(Arrays.asList(student));
    }

    // ================= Function 4 =================
    public void updateStudent() {
        Student student = studentManager.findById(inputValidator.readString("Enter Student ID: "));
        if (student == null) {
            System.out.println(NOT_FOUND);
            return;
        }
        System.out.println("Current: " + student.getId() + " | " + student.getName()
                + " | " + student.getMajor() + " | GPA " + student.getGpa());
        double gpa = Double.parseDouble(inputValidator.readValid("New GPA (0.0 - 4.0): ", studentValidator::checkGpa));
        studentManager.updateGpa(student.getId(), gpa);
        System.out.println("GPA updated successfully!");
    }

    // ================= Function 5 =================
    public void listStudentsByMajor() {
        String major = inputValidator.readString("Enter major: ");
        List<Student> list = studentManager.findByMajor(major);
        if (list.isEmpty()) {
            System.out.println("No students found for major: " + major);
            return;
        }
        printStudentTable(list);
    }

    // ================= Function 6 =================
    public void addCourse() {
        System.out.println("--- ADD A NEW COURSE ---");
        List<Student> students = studentManager.getAll();
        if (students.isEmpty()) {
            System.out.println("No students available. Please add a student first.");
            return;
        }
        System.out.println("Select a student:");
        for (int i = 0; i < students.size(); i++) {
            System.out.println(String.format("%3d. %s - %s",
                    i + 1, students.get(i).getId(), students.get(i).getName()));
        }
        int choice = inputValidator.readInt("Choose (1-" + students.size() + "): ", 1, students.size());
        Student student = students.get(choice - 1);

        String courseId = inputValidator.readValid("Course ID: ", s -> {
            String error = courseValidator.checkCourseId(s);
            if (error == null && courseManager.exists(s)) {
                return "Course ID already exists.";
            }
            return error;
        });
        String name = inputValidator.readValid("Course name: ", courseValidator::checkName);
        int weeks = Integer.parseInt(
                inputValidator.readValid("Duration in weeks: ", courseValidator::checkDuration));
        String dateText = inputValidator.readValid(
                "Start date (" + dateValidator.ACCEPTED_FORMATS + "): ", courseValidator::checkStartDate);
        LocalDate startDate = dateValidator.parse(dateText);

        if (courseManager.add(new Course(courseId, student.getId(), name, weeks, startDate))) {
            System.out.println("Course added successfully!");
        } else {
            System.out.println("Failed to add course.");
        }
    }

    // ================= Function 7 =================
    public void listCoursesByStudent() {
        List<Student> students = studentManager.getAll();
        if (students.isEmpty()) {
            System.out.println("No students to display.");
            return;
        }
        String line = "  " + dashes(65);
        for (Student s : students) {
            System.out.println("[Student: " + s.getId() + " - " + s.getName()
                    + " | Major: " + s.getMajor() + "]");
            System.out.println(line);
            System.out.println(String.format("  %-10s | %-25s | %-9s | %s",
                    "Course ID", "Course Name", "Duration", "Start Date"));
            System.out.println(line);
            List<Course> courses = courseManager.findByStudent(s.getId());
            if (courses.isEmpty()) {
                System.out.println("  (no courses)");
            }
            for (Course c : courses) {
                System.out.println("  " + c);
            }
            System.out.println(line);
            System.out.println();
        }
    }

    // ================= Function 8 =================
    public void calculateTotalDuration() {
        Student student = studentManager.findById(inputValidator.readString("Enter Student ID: "));
        if (student == null) {
            System.out.println(NOT_FOUND);
            return;
        }
        System.out.println("Student: " + student.getId() + " - " + student.getName());
        System.out.println("Registered courses: " + courseManager.findByStudent(student.getId()).size());
        System.out.println("Total study duration: " + courseManager.totalDurationOf(student.getId()) + " weeks");
    }

    // ================= Function 9 =================
    public void removeStudent() {
        Student student = studentManager.findById(inputValidator.readString("Enter Student ID: "));
        if (student == null) {
            System.out.println(NOT_FOUND);
        } else if (courseManager.hasCoursesOf(student.getId())) {
            System.out.println("Cannot delete: Student is assigned to Courses.");
        } else {
            studentManager.remove(student.getId());
            System.out.println("Student removed successfully!");
        }
    }

    // ================= Function 10 =================
    public void sortStudentsByGpa() {
        List<Student> list = studentManager.sortByGpa();
        if (list.isEmpty()) {
            System.out.println("No students to display.");
            return;
        }
        printStudentTable(list);
    }

    // ================= Function 11 =================
    public void saveData() {
        saveToFiles();
    }

    // ================= Function 12 =================
    // Returns true if the program can exit
    public boolean quit() {
        if (studentManager.isDirty() || courseManager.isDirty()) {
            boolean save = inputValidator.readYesNo("Do you want to save the changes before exiting? (Y/N): ");
            if (save) {
                if (!saveToFiles()) {
                    boolean quitAnyway = inputValidator.readYesNo("Saving failed. Quit without saving? (Y/N): ");
                    if (!quitAnyway) {
                        System.out.println("Back to the menu. Your changes are still in memory.");
                        return false;
                    }
                    System.out.println("Changes discarded.");
                }
            } else {
                System.out.println("Changes discarded.");
            }
        }
        System.out.println("Goodbye!");
        return true;
    }

    // ================= Helpers =================
    // Both lists are validated BEFORE any file is written, so an invalid course can never
    // leave Students.txt saved and Courses.txt untouched.
    private boolean saveToFiles() {
        String error = studentManager.validateForSave();
        if (error == null) {
            error = courseManager.validateForSave();
        }
        if (error != null) {
            System.out.println("Cannot save, nothing was written: " + error);
            return false;
        }
        try {
            studentManager.save();
            courseManager.save();
            System.out.println("Data saved successfully to Students.txt and Courses.txt.");
            return true;
        } catch (IOException e) {
            System.out.println("Error while saving data: " + e.getMessage());
            return false;
        }
    }

    // Warnings returned by the managers while loading (skipped lines, duplicates)
    private void printWarnings(List<String> warnings) {
        for (String warning : warnings) {
            System.out.println(warning);
        }
    }

    // One table for functions 1, 3, 5, 10
    private void printStudentTable(List<Student> list) {
        String line = dashes(74);
        System.out.println(String.format("%-10s | %-25s | %-25s | %s", "ID", "Name", "Major", "GPA"));
        System.out.println(line);
        for (Student s : list) {
            System.out.println(s);
        }
        System.out.println(line);
    }

    private String dashes(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append('-');
        }
        return sb.toString();
    }
}
