package validation;

// Student constraints. Every check method returns an error message, or null if valid.
// Every method accepts null: a null value is simply treated as invalid (never throws).
public class StudentValidator {

    public static String checkId(String id) {
        return (id != null && id.matches("STU\\d{4}")) ? null
                : "Student ID must follow the format STU0000 (e.g. STU0001).";
    }

    public static String checkName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Name cannot be empty.";
        }
        if (name.indexOf(',') >= 0) {                        // <-- THÊM 3 DÒNG NÀY
            return "Name cannot contain a comma (,).";
        }
        return name.trim().split("\\s+").length >= 2 ? null : "Name must contain at least two words.";
    }

    public static String checkMajor(String major) {
        if (major == null || major.trim().isEmpty()) {
            return "Major cannot be empty.";
        }
        return major.indexOf(',') >= 0 ? "Major cannot contain a comma (,)." : null;
    }

    public static String checkGpa(String gpa) {
        if (gpa != null) {
            try {
                double value = Double.parseDouble(gpa);
                if (value >= 0.0 && value <= 4.0) {
                    return null;
                }
            } catch (NumberFormatException e) {
                // fall through to the error message
            }
        }
        return "GPA must be a number between 0.0 and 4.0.";
    }

    // First error found in the four fields, or null if all are valid
    public static String firstError(String id, String name, String major, String gpa) {
        String[] errors = {checkId(id), checkName(name), checkMajor(major), checkGpa(gpa)};
        for (String error : errors) {
            if (error != null) {
                return error;
            }
        }
        return null;
    }

    // Used when loading a line from Students.txt
    public static boolean isValid(String id, String name, String major, String gpa) {
        return firstError(id, name, major, gpa) == null;
    }
}
