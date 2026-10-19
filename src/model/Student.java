package model;

import java.util.Locale;
import java.util.Objects;

public class Student {
    private final String id;    
    private String name;
    private String major;
    private double gpa;

    public Student(String id, String name, String major, double gpa) {
        this.id = Objects.requireNonNull(id, "Student ID must not be null.");
        this.name = Objects.requireNonNull(name, "Name must not be null.");
        this.major = Objects.requireNonNull(major, "Major must not be null.");
        this.gpa = gpa;
    }

    public String getId() {return id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = Objects.requireNonNull(name, "Name must not be null.");}

    public String getMajor() {return major;}
    public void setMajor(String major) {this.major = Objects.requireNonNull(major, "Major must not be null.");}

    public double getGpa() {return gpa;}
    public void setGpa(double gpa) {this.gpa = gpa;}

    //Chỉ dựa vào ID, so sánh không phân biệt hoa/thường
    //Quản lý theo identity.VD: stu0001 và STU0001 cùng sinh viên
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Student other = (Student) o;
        return id.equalsIgnoreCase(other.id);
    }

    @Override
    public int hashCode() {
                return Objects.hash(id.toUpperCase(Locale.ROOT));
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-25s | %-25s | %.2f", id, name, major, gpa);
    } 
}
