import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

/**
 * StudentManagementSystem
 * -------------------------------------------------------------------
 * A Swing GUI application that lets an administrator manage student
 * records, enroll students in courses, and assign grades. The interface
 * is organized into three tabs (Students, Course Enrollment, Grade
 * Management) that share the same underlying data model, so an action
 * taken on one tab (e.g. enrolling a student) is immediately reflected
 * wherever that data is displayed elsewhere in the application.
 *
 * Event handling is used throughout: button clicks, combo box
 * selections, and table row selections all trigger listener methods
 * that update the interface dynamically. Invalid actions (missing
 * selections, malformed input) throw custom checked exceptions that
 * are caught at the point of the event and reported to the
 * administrator through a JOptionPane error dialog, so the application
 * never crashes on bad input.
 */
public class StudentManagementSystem extends JFrame {

    // ---- Shared data ----------------------------------------------------
    private final List<Student> students = new ArrayList<>();
    private final Course[] courses = {
            new Course("CS101", "Intro to Programming"),
            new Course("MATH201", "Calculus II"),
            new Course("ENG150", "English Composition"),
            new Course("PSY110", "Intro to Psychology")
    };
    private int nextId = 1;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern GRADE_PATTERN =
            Pattern.compile("^[A-Fa-f][+-]?$|^N/A$");

    // ---- Students tab components -----------------------------------------
    private JTextField nameField, emailField, majorField;
    private JLabel idPreviewLabel;
    private JTable studentTable;
    private StudentTableModel studentTableModel;

    // ---- Enrollment tab components ----------------------------------------
    private JComboBox<Course> courseCombo;
    private DefaultListModel<Student> eligibleListModel;
    private JList<Student> eligibleList;
    private JTable enrolledInCourseTable;
    private StudentTableModel enrolledInCourseModel;

    // ---- Grades tab components ---------------------------------------------
    private JComboBox<Student> gradeStudentCombo;
    private JTable courseGradeTable;
    private CourseGradeTableModel courseGradeTableModel;
    private JTextField gradeField;

    private JTabbedPane tabbedPane;

    public StudentManagementSystem() {
        super("Student Management System");
        seedData();
        buildMenuBar();
        buildTabs();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);
    }

    private void seedData() {
        students.add(new Student(nextId++, "Alice Johnson", "alice.johnson@example.edu", "Computer Science"));
        students.add(new Student(nextId++, "Brian Smith", "brian.smith@example.edu", "Mathematics"));
    }

    // =====================================================================
    // Menu bar — mirrors the primary actions available as buttons, per the
    // assignment requirement that student/enrollment/grade actions be
    // reachable from menu items as well as GUI controls.
    // =====================================================================
    private void buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu studentMenu = new JMenu("Student Records");
        JMenuItem addItem = new JMenuItem("Add Student");
        JMenuItem updateItem = new JMenuItem("Update Student");
        JMenuItem viewItem = new JMenuItem("View Student Details");
        addItem.addActionListener(e -> { tabbedPane.setSelectedIndex(0); clearForm(); nameField.requestFocus(); });
        updateItem.addActionListener(e -> { tabbedPane.setSelectedIndex(0); studentTable.requestFocus(); });
        viewItem.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        studentMenu.add(addItem);
        studentMenu.add(updateItem);
        studentMenu.add(viewItem);

        JMenu enrollMenu = new JMenu("Enrollment");
        JMenuItem enrollItem = new JMenuItem("Enroll Student");
        enrollItem.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        enrollMenu.add(enrollItem);

        JMenu gradeMenu = new JMenu("Grades");
        JMenuItem gradeItem = new JMenuItem("Assign Grade");
        gradeItem.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        gradeMenu.add(gradeItem);

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        menuBar.add(studentMenu);
        menuBar.add(enrollMenu);
        menuBar.add(gradeMenu);
        setJMenuBar(menuBar);
    }

    private void buildTabs() {
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Students", buildStudentsPanel());
        tabbedPane.addTab("Course Enrollment", buildEnrollmentPanel());
        tabbedPane.addTab("Grade Management", buildGradesPanel());
        add(tabbedPane);
    }

    // =====================================================================
    // TAB 1 — Students: add, update, and view student records
    // =====================================================================
    private JPanel buildStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ---- Form panel ----
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Student Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        idPreviewLabel = new JLabel("New student ID: " + nextId);
        nameField = new JTextField(18);
        emailField = new JTextField(18);
        majorField = new JTextField(18);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; form.add(idPreviewLabel, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; form.add(nameField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; form.add(emailField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Major:"), gbc);
        gbc.gridx = 1; form.add(majorField, gbc);
        row++;

        JButton addButton = new JButton("Add Student");
        JButton updateButton = new JButton("Update Selected");
        JButton clearButton = new JButton("Clear Form");
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonRow.add(addButton);
        buttonRow.add(updateButton);
        buttonRow.add(clearButton);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        form.add(buttonRow, gbc);

        // ---- Table ----
        studentTableModel = new StudentTableModel(students);
        studentTable = new JTable(studentTableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setRowSorter(new TableRowSorter<>(studentTableModel));
        JScrollPane tableScroll = new JScrollPane(studentTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("All Students"));

        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && studentTable.getSelectedRow() != -1) {
                int modelRow = studentTable.convertRowIndexToModel(studentTable.getSelectedRow());
                Student s = studentTableModel.getStudentAt(modelRow);
                nameField.setText(s.getName());
                emailField.setText(s.getEmail());
                majorField.setText(s.getMajor());
            }
        });

        addButton.addActionListener(e -> onAddStudent());
        updateButton.addActionListener(e -> onUpdateStudent());
        clearButton.addActionListener(e -> clearForm());

        panel.add(form, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);
        return panel;
    }

    private void onAddStudent() {
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String major = majorField.getText().trim();
            validateStudentInput(name, email, major);

            Student s = new Student(nextId, name, email, major);
            students.add(s);
            nextId++;
            studentTableModel.refresh();
            refreshAllStudentDependentViews();
            idPreviewLabel.setText("New student ID: " + nextId);
            clearForm();
            JOptionPane.showMessageDialog(this, "Student added successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (InvalidInputException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Cannot Add Student", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdateStudent() {
        try {
            int viewRow = studentTable.getSelectedRow();
            if (viewRow == -1) {
                throw new NoSelectionException(
                        "Please select a student in the table before clicking Update Selected.");
            }
            int modelRow = studentTable.convertRowIndexToModel(viewRow);
            Student s = studentTableModel.getStudentAt(modelRow);

            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String major = majorField.getText().trim();
            validateStudentInput(name, email, major);

            s.setName(name);
            s.setEmail(email);
            s.setMajor(major);
            studentTableModel.refresh();
            refreshAllStudentDependentViews();
            JOptionPane.showMessageDialog(this, "Student updated successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NoSelectionException | InvalidInputException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Cannot Update Student", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void validateStudentInput(String name, String email, String major)
            throws InvalidInputException {
        if (name.isEmpty() || major.isEmpty()) {
            throw new InvalidInputException("Name and Major are required fields and cannot be empty.");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidInputException(
                    "\"" + email + "\" is not a valid email address. Expected format: name@example.com");
        }
    }

    private void clearForm() {
        nameField.setText("");
        emailField.setText("");
        majorField.setText("");
        studentTable.clearSelection();
    }

    // =====================================================================
    // TAB 2 — Course Enrollment
    // =====================================================================
    private JPanel buildEnrollmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Select Course:"));
        courseCombo = new JComboBox<>(courses);
        top.add(courseCombo);
        JButton enrollButton = new JButton("Enroll Selected Student");
        top.add(enrollButton);

        eligibleListModel = new DefaultListModel<>();
        eligibleList = new JList<>(eligibleListModel);
        eligibleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane eligibleScroll = new JScrollPane(eligibleList);
        eligibleScroll.setBorder(BorderFactory.createTitledBorder("Eligible Students (not yet enrolled)"));

        enrolledInCourseModel = new StudentTableModel(new ArrayList<>());
        enrolledInCourseTable = new JTable(enrolledInCourseModel);
        JScrollPane enrolledScroll = new JScrollPane(enrolledInCourseTable);
        enrolledScroll.setBorder(BorderFactory.createTitledBorder("Students Enrolled in Selected Course"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, eligibleScroll, enrolledScroll);
        splitPane.setResizeWeight(0.5);

        courseCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) refreshEnrollmentTab();
        });

        enrollButton.addActionListener(e -> onEnrollStudent());

        panel.add(top, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);

        refreshEnrollmentTab();
        return panel;
    }

    private void onEnrollStudent() {
        try {
            Course course = (Course) courseCombo.getSelectedItem();
            Student student = eligibleList.getSelectedValue();
            if (course == null || student == null) {
                throw new NoSelectionException(
                        "Please select both a course and a student before clicking Enroll Selected Student.");
            }
            student.enroll(course.getCode());
            refreshAllStudentDependentViews();
            JOptionPane.showMessageDialog(this,
                    student.getName() + " was enrolled in " + course.getCode() + ".",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NoSelectionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Cannot Enroll Student", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshEnrollmentTab() {
        if (courseCombo == null) return;
        Course selected = (Course) courseCombo.getSelectedItem();
        eligibleListModel.clear();
        List<Student> enrolled = new ArrayList<>();
        if (selected != null) {
            for (Student s : students) {
                if (s.isEnrolledIn(selected.getCode())) {
                    enrolled.add(s);
                } else {
                    eligibleListModel.addElement(s);
                }
            }
        }
        enrolledInCourseModel = new StudentTableModel(enrolled);
        enrolledInCourseTable.setModel(enrolledInCourseModel);
    }

    // =====================================================================
    // TAB 3 — Grade Management
    // =====================================================================
    private JPanel buildGradesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Select Student:"));
        gradeStudentCombo = new JComboBox<>();
        for (Student s : students) gradeStudentCombo.addItem(s);
        top.add(gradeStudentCombo);

        top.add(new JLabel("Grade (A-F, optional +/-):"));
        gradeField = new JTextField(5);
        top.add(gradeField);
        JButton assignButton = new JButton("Assign Grade");
        top.add(assignButton);

        courseGradeTableModel = new CourseGradeTableModel();
        courseGradeTable = new JTable(courseGradeTableModel);
        JScrollPane tableScroll = new JScrollPane(courseGradeTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Enrolled Courses and Current Grades"));

        gradeStudentCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                courseGradeTableModel.setStudent((Student) gradeStudentCombo.getSelectedItem());
            }
        });

        assignButton.addActionListener(e -> onAssignGrade());

        panel.add(top, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);

        if (gradeStudentCombo.getItemCount() > 0) {
            courseGradeTableModel.setStudent((Student) gradeStudentCombo.getSelectedItem());
        }
        return panel;
    }

    private void onAssignGrade() {
        try {
            Student student = (Student) gradeStudentCombo.getSelectedItem();
            int row = courseGradeTable.getSelectedRow();
            if (student == null || row == -1) {
                throw new NoSelectionException(
                        "Please select a student and one of their enrolled courses before assigning a grade.");
            }
            String grade = gradeField.getText().trim().toUpperCase();
            if (!GRADE_PATTERN.matcher(grade).matches()) {
                throw new InvalidGradeException(
                        "\"" + grade + "\" is not a valid grade. Use a letter A-F, optionally followed by + or -.");
            }
            String courseCode = courseGradeTableModel.getCourseCodeAt(row);
            student.assignGrade(courseCode, grade);
            courseGradeTableModel.refresh();
            refreshAllStudentDependentViews();
            gradeField.setText("");
            JOptionPane.showMessageDialog(this,
                    "Grade " + grade + " assigned for " + courseCode + ".",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NoSelectionException | InvalidGradeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Cannot Assign Grade", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =====================================================================
    // Cross-tab refresh: keeps every view in sync with the shared data
    // =====================================================================
    private void refreshAllStudentDependentViews() {
        studentTableModel.refresh();
        refreshEnrollmentTab();
        Student prevSelected = (Student) (gradeStudentCombo != null ? gradeStudentCombo.getSelectedItem() : null);
        if (gradeStudentCombo != null) {
            gradeStudentCombo.removeAllItems();
            for (Student s : students) gradeStudentCombo.addItem(s);
            if (prevSelected != null) gradeStudentCombo.setSelectedItem(prevSelected);
            if (gradeStudentCombo.getSelectedItem() != null) {
                courseGradeTableModel.setStudent((Student) gradeStudentCombo.getSelectedItem());
            }
        }
    }

    // ---- Accessors used by the automated demo/screenshot driver ----------
    JTextField getNameField() { return nameField; }
    JTextField getEmailField() { return emailField; }
    JTextField getMajorField() { return majorField; }
    JTable getStudentTable() { return studentTable; }
    JTabbedPane getTabbedPane() { return tabbedPane; }
    JComboBox<Course> getCourseCombo() { return courseCombo; }
    JList<Student> getEligibleList() { return eligibleList; }
    JComboBox<Student> getGradeStudentCombo() { return gradeStudentCombo; }
    JTable getCourseGradeTable() { return courseGradeTable; }
    JTextField getGradeField() { return gradeField; }
    void triggerAdd() { onAddStudent(); }
    void triggerUpdate() { onUpdateStudent(); }
    void triggerEnroll() { onEnrollStudent(); }
    void triggerAssignGrade() { onAssignGrade(); }

    // =====================================================================
    // DEMO / SCREENSHOT MODE
    // -----------------------------------------------------------------
    // When run with a numeric command-line argument (1-9), the app skips
    // straight to a specific state — including deliberately triggering
    // validation exceptions — so a single screenshot of that run captures
    // exactly the scenario needed for the assignment documentation.
    // Run with no argument (or argument 0) for the normal, empty-form
    // starting view. This method is NOT part of the interactive
    // application logic itself; it only calls the same event-handler
    // methods a real user click would call.
    // =====================================================================
    private void runScenario(int scenario) {
        switch (scenario) {
            case 2: // Add Student — valid input
                tabbedPane.setSelectedIndex(0);
                nameField.setText("Carlos Diaz");
                emailField.setText("carlos.diaz@example.edu");
                majorField.setText("Biology");
                triggerAdd();
                break;
            case 3: // Add Student — invalid input (empty name) triggers InvalidInputException
                tabbedPane.setSelectedIndex(0);
                nameField.setText("");
                emailField.setText("not-a-valid-email");
                majorField.setText("Biology");
                triggerAdd();
                break;
            case 4: // Update Student — valid
                tabbedPane.setSelectedIndex(0);
                studentTable.setRowSelectionInterval(0, 0);
                nameField.setText(students.get(0).getName());
                emailField.setText(students.get(0).getEmail());
                majorField.setText("Data Science");
                triggerUpdate();
                break;
            case 5: // Update Student — no row selected triggers NoSelectionException
                tabbedPane.setSelectedIndex(0);
                studentTable.clearSelection();
                nameField.setText("Someone");
                emailField.setText("someone@example.edu");
                majorField.setText("Physics");
                triggerUpdate();
                break;
            case 6: // Enroll Student — valid
                tabbedPane.setSelectedIndex(1);
                courseCombo.setSelectedIndex(0);
                refreshEnrollmentTab();
                if (!eligibleListModel.isEmpty()) eligibleList.setSelectedIndex(0);
                triggerEnroll();
                break;
            case 7: // Enroll Student — no student selected triggers NoSelectionException
                tabbedPane.setSelectedIndex(1);
                courseCombo.setSelectedIndex(1);
                refreshEnrollmentTab();
                eligibleList.clearSelection();
                triggerEnroll();
                break;
            case 8: // Assign Grade — valid
                tabbedPane.setSelectedIndex(2);
                students.get(0).enroll(courses[0].getCode());
                refreshAllStudentDependentViews();
                gradeStudentCombo.setSelectedItem(students.get(0));
                courseGradeTableModel.setStudent(students.get(0));
                if (courseGradeTable.getRowCount() > 0) courseGradeTable.setRowSelectionInterval(0, 0);
                gradeField.setText("A");
                triggerAssignGrade();
                break;
            case 9: // Assign Grade — invalid format triggers InvalidGradeException
                tabbedPane.setSelectedIndex(2);
                students.get(0).enroll(courses[0].getCode());
                refreshAllStudentDependentViews();
                gradeStudentCombo.setSelectedItem(students.get(0));
                courseGradeTableModel.setStudent(students.get(0));
                if (courseGradeTable.getRowCount() > 0) courseGradeTable.setRowSelectionInterval(0, 0);
                gradeField.setText("Z9");
                triggerAssignGrade();
                break;
            default: // 1 or anything else: plain starting view, no action taken
                tabbedPane.setSelectedIndex(0);
                break;
        }
    }

    private static volatile StudentManagementSystem appRef;

    /**
     * Self-contained demo runner: launches the GUI, performs the requested
     * scenario, waits for it to render (including any error dialog), saves
     * a screenshot of the whole screen to disk as "scenario_<N>.png" in the
     * current directory, then exits. This means no external screenshot
     * tool is needed — just run under a virtual display (e.g. `xvfb-run`)
     * and the PNG appears automatically.
     */
    public static void main(String[] args) throws Exception {
        int scenario = 1;
        if (args.length > 0) {
            try { scenario = Integer.parseInt(args[0].trim()); } catch (NumberFormatException ignored) { }
        }
        final int scenarioToRun = scenario;

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) { }
            appRef = new StudentManagementSystem();
            appRef.setVisible(true);
        });

        Thread.sleep(800); // let the window finish rendering
        SwingUtilities.invokeLater(() -> {
            if (appRef != null) appRef.runScenario(scenarioToRun);
        });
        Thread.sleep(1800); // let any dialog finish rendering

        try {
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage img = robot.createScreenCapture(screenRect);
            String filename = "scenario_" + scenarioToRun + ".png";
            ImageIO.write(img, "png", new File(filename));
            System.out.println("Saved screenshot: " + filename);
        } catch (Exception e) {
            System.out.println("Screenshot failed: " + e.getMessage());
        }
        System.exit(0);
    }
}
