package validation;

import java.time.LocalDate;

public class CourseValidator {

    private final StudentValidator studentValidator = new StudentValidator();
    private final DateValidator dateValidator = new DateValidator();

    public String checkCourseId(String courseId) {
        return (courseId == null || courseId.trim().isEmpty()) ? "Course ID cannot be empty." : null;
    }

    public String checkName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Course name cannot be empty.";
        }
        return name.indexOf(',') >= 0 ? "Course name cannot contain a comma (,)." : null;
    }

    public String checkDuration(String weeks) {
        if (weeks != null) {
            try {
                if (Integer.parseInt(weeks.trim()) >= 1) {
                    return null;
                }
            } catch (NumberFormatException e) {
            }
        }
        return "Duration must be a positive integer (minimum 1 week).";
    }

    public String checkStartDate(String text) {
        LocalDate date = dateValidator.parse(text);
        if (date == null) {
            return "Invalid date. Accepted formats: " + dateValidator.ACCEPTED_FORMATS + ".";
        }
        return date.isAfter(LocalDate.now()) ? null : "Start date must be a future date.";
    }

    public String firstError(String courseId, String studentId, String name, String weeks) {
        String[] errors = {checkCourseId(courseId), studentValidator.checkId(studentId),
            checkName(name), checkDuration(weeks)};
        for (String error : errors) {
            if (error != null) {
                return error;
            }
        }
        return null;
    }
}