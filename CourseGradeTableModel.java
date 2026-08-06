import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Backs the JTable on the "Grades" tab that shows the courses a selected
 * student is enrolled in, along with their current grade in each.
 */
public class CourseGradeTableModel extends AbstractTableModel {
    private final String[] columns = {"Course Code", "Grade"};
    private List<String> courseCodes = new ArrayList<>();
    private Student currentStudent;

    public void setStudent(Student student) {
        this.currentStudent = student;
        courseCodes = new ArrayList<>();
        if (student != null) {
            for (Map.Entry<String, String> e : student.getEnrolledCourses().entrySet()) {
                courseCodes.add(e.getKey());
            }
        }
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() { return courseCodes.size(); }

    @Override
    public int getColumnCount() { return columns.length; }

    @Override
    public String getColumnName(int col) { return columns[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        String code = courseCodes.get(row);
        if (col == 0) return code;
        return currentStudent.getEnrolledCourses().get(code);
    }

    public String getCourseCodeAt(int row) { return courseCodes.get(row); }

    public void refresh() { fireTableDataChanged(); }
}
