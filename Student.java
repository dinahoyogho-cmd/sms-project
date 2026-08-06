/**
 * The Student class represents a single student record in the
 * Student Record Management System. It follows the principle of
 * encapsulation by declaring all instance variables as private and
 * exposing controlled access through public getter and setter
 * methods (Eck, 2022).
 */
public class Student {

    // Private instance variables store the state of each Student object.
    private String name;
    private int id;
    private int age;
    private String grade;

    /**
     * Constructs a new Student with the given details.
     *
     * @param name  the student's full name
     * @param id    the student's unique identification number
     * @param age   the student's age
     * @param grade the student's grade or class level
     */
    public Student(String name, int id, int age, String grade) {
        this.name = name;
        this.id = id;
        this.age = age;
        this.grade = grade;
    }

    // ---------- Getter methods ----------

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public String getGrade() {
        return grade;
    }

    // ---------- Setter methods ----------
    // Setters allow StudentManagement to update a record without
    // exposing the instance variables directly.

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    /**
     * Returns a formatted, human-readable summary of the student's
     * details. Overriding toString() follows the practice described
     * by Eck (2022) of customizing an object's printed representation
     * instead of relying on the default Object.toString() output.
     *
     * @return a formatted string describing this student
     */
    @Override
    public String toString() {
        return String.format(
                "ID: %-6d Name: %-20s Age: %-4d Grade: %s",
                id, name, age, grade
        );
    }
}
