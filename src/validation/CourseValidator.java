package validation;

import java.time.LocalDate;

// Course constraints. Every check method returns an error message, or null if valid.
// Every method accepts null: a null value is simply treated as invalid (never throws).
// "Unique Course ID" and "Student ID exists" are checked by CourseManager.
public class CourseValidator {

    public static String checkCourseId(String courseId) {
        return (courseId == null || courseId.trim().isEmpty()) ? "Course ID cannot be empty." : null;
    }

    public static String checkName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Course name cannot be empty.";
        }
        return name.indexOf(',') >= 0 ? "Course name cannot contain a comma (,)." : null;
    }

    public static String checkDuration(String weeks) {
        if (weeks != null) {
            try {
                if (Integer.parseInt(weeks.trim()) >= 1) {
                    return null;
                }
            } catch (NumberFormatException e) {
                // fall through to the error message
            }
        }
        return "Duration must be a positive integer (minimum 1 week).";
    }

    // Only used when ADDING a course: old courses in the file may already have started
    public static String checkStartDate(String text) {
        LocalDate date = DateValidator.parse(text);
        if (date == null) {
            return "Invalid date. Accepted formats: " + DateValidator.ACCEPTED_FORMATS + ".";
        }
        return date.isAfter(LocalDate.now()) ? null : "Start date must be a future date.";
    }

    // First error found in the four fields, or null if all are valid.
    // Used when loading a line and when checking the data before saving.
    public static String firstError(String courseId, String studentId, String name, String weeks) {
        String[] errors = {checkCourseId(courseId), StudentValidator.checkId(studentId),
            checkName(name), checkDuration(weeks)};
        for (String error : errors) {
            if (error != null) {
                return error;
            }
        }
        return null;
    }
}
