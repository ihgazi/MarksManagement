import java.sql.*;
import java.util.Scanner;

class MarksMgmt {
    static final String JDBC_DRIVER = "org.sqlite.JDBC";
    static final String DATABASE_URL = "jdbc:sqlite:marksmgmt.db";
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            //Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DATABASE_URL);
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter teacher ID: ");
            int teacherId = scanner.nextInt();

            if (validateTeacher(connection, teacherId)) {
                System.out.println("Login successful! Welcome, Teacher ID: " + teacherId);
                while (true) {
                    System.out.println("1. View list of all students");
                    System.out.println("2. Make a new result entry for a student");
                    System.out.println("3. Display total marks of all students");
                    System.out.println("4. Exit");
                    System.out.print("Enter your choice: ");
                    int choice = scanner.nextInt();
                    scanner.nextLine();
                    if (choice >= 4) break;
                    switch (choice) {
                        case 1:
                            listAllStudents(connection);
                            break;
                        case 2:
                            addResultEntry(connection, teacherId);
                            break;
                        case 3:
                            displaySumOfMarks(connection);
                            break;
                        default:
                            System.out.println("Invalid choice");
                    }
                }
            } else {
                System.out.println("Invalid teacher ID. Login failed.");
            }

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean validateTeacher(Connection connection, int teacherId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM teacher WHERE teacherid = ?");
        preparedStatement.setInt(1, teacherId);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    public static void listAllStudents(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM student");

        System.out.println("List of all students:");
        while (resultSet.next()) {
            System.out.println("Student ID: " + resultSet.getInt("studentid") + ", Student Name: " + resultSet.getString("studentname"));
        }
    }

    public static void addResultEntry(Connection connection, int teacherId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT subjectid from teacher where teacherid = ?");
        preparedStatement.setInt(1,teacherId);
        ResultSet resultSet = preparedStatement.executeQuery();
        int subjectid;
        if (resultSet.next()) {
            subjectid = resultSet.getInt("subjectid");
        }
        else {
            System.out.println("Error processing request");
            return;
        }

        System.out.print("Enter student ID: ");
        int studentId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter marks: ");
        int marks = scanner.nextInt();
        scanner.nextLine();

        preparedStatement = connection.prepareStatement("INSERT INTO result (studentid, subjectid, marks) VALUES (?,?,?)");
        preparedStatement.setInt(1, studentId);
        preparedStatement.setInt(2, subjectid);
        preparedStatement.setInt(3, marks);
        int rowsAffected = preparedStatement.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("New result entry added successfully");
        } else {
            System.out.println("Failed to add new result entry");
        }
    }

    public static void displaySumOfMarks(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT studentid, SUM(marks) AS total_marks FROM result GROUP BY studentid ORDER BY total_marks DESC");

        System.out.println("Sum of marks of all students (sorted by sum in descending order):");
        while (resultSet.next()) {
            System.out.println("Student ID: " + resultSet.getInt("studentid") + ", Total Marks: " + resultSet.getInt("total_marks"));
        }
    }
}
      
