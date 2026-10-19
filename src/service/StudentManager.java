package service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import model.Student;
import repository.IFileHelper;
import validation.StudentValidator;

// Business logic only: this class never prints. Problems are returned (boolean, null, empty list,
// warning list, error message) and the Menu decides what to show.
public class StudentManager {

    private final Map<String, Student> students = new LinkedHashMap<>();
    private final IFileHelper<Student> fileHelper;
    private boolean dirty = false; // unsaved changes (function 12)

    public StudentManager(IFileHelper<Student> fileHelper) {
        this.fileHelper = Objects.requireNonNull(fileHelper, "File helper must not be null.");
    }

    public boolean isDirty() {
        return dirty;
    }
    // stu0001 and STU0001 are the same student, so the ID is upper-cased before it is used as a key

    private static String key(String id) {
        return id.toUpperCase(Locale.ROOT);
    }

    // RA - function 1
    public List<Student> getAll() {
        return new ArrayList<>(students.values());
    }

    // R1 - function 3 (ID is compared ignoring case: stu0001 = STU0001). null ID -> not found
    public Student findById(String id) {
        return id == null ? null : students.get(key(id));
    }
    // R1'
    public boolean exists(String id) {
        return findById(id) != null;
    }

    // C - function 2: returns false if the student is null or the ID already exists
    public boolean add(Student student) {
        if (student == null || exists(student.getId())) {
            return false;
        }
        students.put(key(student.getId()), student);
        dirty = true;

        return true;
    }

    // U - function 4: returns false if the ID does not exist
    public boolean updateGpa(String id, double newGpa) {
        Student s = findById(id);
        if (s == null) {
            return false;
        }
        s.setGpa(newGpa);
        dirty = true;
        return true;
    }

    // RF - function 5: students whose major matches the input. The input must match WHOLE words of the
    // major: "Engineering" or "Software" match "Software Engineering", but "ware" or "e" do not.
    // Only UPPER/lower case is ignored, nothing else (spaces must match exactly).
    // null or blank input -> empty list (it must not list every student).
    public List<Student> findByMajor(String major) {
        List<Student> result = new ArrayList<>();
        if (major == null || major.trim().isEmpty()) {
            return result;
        }
        // Surrounding both texts with a space makes "contains" respect word boundaries
        String keyword = " " + major.toLowerCase(Locale.ROOT) + " ";
        for (Student s : students.values()) {
            if ((" " + s.getMajor().toLowerCase(Locale.ROOT) + " ").contains(keyword)) {
                result.add(s);
            }
        }
        return result;
    }

    // RS - function 10: GPA ascending, sorts a copy
    public List<Student> sortByGpa() {
        List<Student> sorted = getAll();
        sorted.sort(Comparator.comparingDouble(Student::getGpa));
        return sorted;
    }

    // D - function 9: the Menu checks "has courses" before calling this
    public boolean remove(String id) {
        Student s = findById(id);
        if (s == null) {
            return false;
        }
        students.remove(key(s.getId()));
        dirty = true;

        return true;
    }

    // L - skips students whose ID is already loaded.
    // Returns the warnings (skipped lines / duplicates) for the Menu to display.
    public List<String> load() throws IOException {
        List<String> warnings = new ArrayList<>();
        List<Student> loaded = Objects.requireNonNull(fileHelper.load(warnings), "Loaded data must not be null.");
        students.clear();
        for (Student s : loaded) {
            if (exists(s.getId())) {
                warnings.add("Skipped student " + s.getId() + ": duplicate Student ID.");
            } else {
                students.put(key(s.getId()), s);
            }
        }
        dirty = false;
        return warnings;
    }

    // Checks every student before saving. Returns the first problem found, or null if all are valid.
    // Uses the same rules as typing a student and as loading a line, so valid data always passes.
    public String validateForSave() {
        int position = 0;
        for (Student s : students.values()) { // a Map cannot hold two students with the same ID
            position++;
            String error = StudentValidator.firstError(
                    s.getId(), s.getName(), s.getMajor(), String.valueOf(s.getGpa()));
            if (error != null) {
                return "Invalid student at position " + position + " (" + s.getId() + "): " + error;
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
