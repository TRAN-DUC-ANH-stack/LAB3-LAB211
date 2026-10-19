
import java.util.NoSuchElementException;
import repository.CourseTextFile;
import repository.StudentTextFile;
import service.CourseManager;
import service.StudentManager;
import view.Menu;

public class Main {

    public static void main(String[] args) {
        StudentManager studentManager = new StudentManager(new StudentTextFile("Students.txt"));
        CourseManager courseManager = new CourseManager(
                new CourseTextFile("Courses.txt", CourseManager::parseLine), studentManager);
        Menu menu = new Menu(studentManager, courseManager);

        menu.loadData();
        boolean running = true;
        try {
            while (running) {
                switch (menu.showMenu()) {
                    case 1:
                        menu.listAllStudents();
                        break;
                    case 2:
                        menu.addStudent();
                        break;
                    case 3:
                        menu.searchStudent();
                        break;
                    case 4:
                        menu.updateStudent();
                        break;
                    case 5:
                        menu.listStudentsByMajor();
                        break;
                    case 6:
                        menu.addCourse();
                        break;
                    case 7:
                        menu.listCoursesByStudent();
                        break;
                    case 8:
                        menu.calculateTotalDuration();
                        break;
                    case 9:
                        menu.removeStudent();
                        break;
                    case 10:
                        menu.sortStudentsByGpa();
                        break;
                    case 11:
                        menu.saveData();
                        break;
                    case 12:
                        running = !menu.quit();
                        break;
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println();
            System.out.println("Input ended, program stopped."
                    + (studentManager.isDirty() || courseManager.isDirty() ? " Unsaved changes were NOT saved." : ""));
        }
    }
}
