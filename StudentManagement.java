import java.util.ArrayList;

/**
 * The StudentManagement class provides the core record-keeping
 * services for the system. It uses private static variables so that
 * a single, shared list of students and a single running total exist
 * for the whole application, regardless of how many times the class
 * is referenced (Eck, 2022). Because the fields and methods are
 * static, they belong to the class itself rather than to any one
 * instance, which is appropriate here since the system needs exactly
 * one shared roster of students.
 */
public class StudentManagement {

    // Private static variable holding every Student currently on record.
    private static ArrayList<Student> studentList = new ArrayList<>();

    // Private static variable tracking the total number of students added.
    private static int totalStudents = 0;

    /**
     * Adds a new student to the roster and increments the static
     * student counter.
     *
     * @param student the Student object to add
     */
    public static void addStudent(Student student) {
        studentList.add(student);
        totalStudents++;
    }

    /**
     * Searches the roster for a student with the given ID.
     *
     * @param id the student ID to search for
     * @return the matching Student object, or null if no student
     *         with that ID exists
     */
    public static Student findStudentById(int id) {
        for (Student s : studentList) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    /**
     * Updates the name, age, and grade of an existing student.
     *
     * @param id       the ID of the student to update
     * @param newName  the replacement name
     * @param newAge   the replacement age
     * @param newGrade the replacement grade
     * @return true if the student was found and updated, false if no
     *         student with the given ID exists
     */
    public static boolean updateStudent(int id, String newName, int newAge, String newGrade) {
        Student student = findStudentById(id);
        if (student == null) {
            return false;
        }
        student.setName(newName);
        student.setAge(newAge);
        student.setGrade(newGrade);
        return true;
    }

    /**
     * Retrieves and prints the details of a single student.
     *
     * @param id the ID of the student to view
     * @return true if the student was found and displayed, false
     *         otherwise
     */
    public static boolean viewStudent(int id) {
        Student student = findStudentById(id);
        if (student == null) {
            return false;
        }
        System.out.println(student);
        return true;
    }

    /**
     * Prints the details of every student currently on record, along
     * with the total student count.
     */
    public static void viewAllStudents() {
        if (studentList.isEmpty()) {
            System.out.println("No student records are currently stored.");
            return;
        }
        for (Student s : studentList) {
            System.out.println(s);
        }
        System.out.println("Total students on record: " + totalStudents);
    }

    /**
     * Returns the total number of students that have been added to
     * the system.
     *
     * @return the static student counter
     */
    public static int getTotalStudents() {
        return totalStudents;
    }
}
