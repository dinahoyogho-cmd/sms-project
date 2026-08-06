import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Backs the JTable on the "Students" tab. Because it extends
 * AbstractTableModel and we call fireTableDataChanged()/fireTableRowsUpdated()
 * whenever the underlying list changes, the table (and any other view built
 * on this model) updates dynamically without needing a manual refresh.
 */
public class StudentTableModel extends AbstractTableModel {
    private final String[] columns = {"ID", "Name", "Email", "Major", "Courses Enrolled"};
    private final List<Student> students;

    public StudentTableModel(List<Student> students) {
        this.students = students;
    }

    @Override
    public int getRowCount() { return students.size(); }

    @Override
    public int getColumnCount() { return columns.length; }

    @Override
    public String getColumnName(int col) { return columns[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        Student s = students.get(row);
        switch (col) {
            case 0: return s.getId();
            case 1: return s.getName();
            case 2: return s.getEmail();
            case 3: return s.getMajor();
            case 4: return s.getEnrolledCourses().size();
            default: return "";
        }
    }

    public Student getStudentAt(int row) { return students.get(row); }

    public void refresh() { fireTableDataChanged(); }
}
