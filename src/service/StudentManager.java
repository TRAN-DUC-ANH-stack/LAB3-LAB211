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

public class StudentManager {

    private final StudentValidator studentValidator = new StudentValidator();
    private final Map<String, Student> students = new LinkedHashMap<>();
    private final IFileHelper<Student> fileHelper;
    private boolean dirty = false; // unsaved changes (function 12)

    public StudentManager(IFileHelper<Student> fileHelper) {
        this.fileHelper = Objects.requireNonNull(fileHelper, "File helper must not be null.");
    }

    public boolean isDirty() {
        return dirty;
    }

    private static String key(String id) {
        return id.toUpperCase(Locale.ROOT);
    }

    // RA - function 1
    public List<Student> getAll() {
        return new ArrayList<>(students.values());
    }

    // R1 - function 3 
    public Student findById(String id) {
        return id == null ? null : students.get(key(id));
    }

    // R1'
    public boolean exists(String id) {
        return findById(id) != null;
    }

    // C - function 2: 
    public boolean add(Student student) {
        if (student == null || exists(student.getId())) {
            return false;
        }
        students.put(key(student.getId()), student);
        dirty = true;

        return true;
    }

    // U - function 4: 
    public boolean updateGpa(String id, double newGpa) {
        Student s = findById(id);
        if (s == null) {
            return false;
        }
        s.setGpa(newGpa);
        dirty = true;
        return true;
    }

    // RF - function 5:
    public List<Student> findByMajor(String major) {
        List<Student> result = new ArrayList<>();
        if (major == null || major.trim().isEmpty()) {
            return result;
        }
        String keyword = " " + major.toLowerCase(Locale.ROOT) + " ";
        for (Student s : students.values()) {
            if ((" " + s.getMajor().toLowerCase(Locale.ROOT) + " ").contains(keyword)) {
                result.add(s);
            }
        }
        return result;
    }

    // RS - function 10: 
    public List<Student> sortByGpa() {
        List<Student> sorted = getAll();
        sorted.sort(Comparator.comparingDouble(Student::getGpa));
        return sorted;
    }

    // D - function 9: 
    public boolean remove(String id) {
        Student s = findById(id);
        if (s == null) {
            return false;
        }
        students.remove(key(s.getId()));
        dirty = true;

        return true;
    }

    // L 
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

    public String validateForSave() {
        int position = 0;
        for (Student s : students.values()) {
            position++;
            String error = studentValidator.firstError(
                    s.getId(), s.getName(), s.getMajor(), String.valueOf(s.getGpa()));
            if (error != null) {
                return "Invalid student at position " + position + " (" + s.getId() + "): " + error;
            }
        }
        return null;
    }

    // S 
    public void save() throws IOException {
        String error = validateForSave();
        if (error != null) {
            throw new IllegalStateException("Cannot save: " + error);
        }
        fileHelper.save(getAll());
        dirty = false;
    }
}
