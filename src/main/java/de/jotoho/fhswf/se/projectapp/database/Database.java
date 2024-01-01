package de.jotoho.fhswf.se.projectapp.database;

import de.jotoho.fhswf.se.projectapp.Ansprechpartner;
import de.jotoho.fhswf.se.projectapp.Projekt;
import de.jotoho.fhswf.se.projectapp.Student;
import de.jotoho.fhswf.se.projectapp.Unternehmen;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


public class Database implements DatabaseInterface {
    private static final class StringGenerator {

        public static String selectAllStundeten = "select * from Student";
        private StringGenerator(){}

        public static String selectStudentString(final long Matrikelnummer){
            return "select * from Student where Matrikelnummer = " + Matrikelnummer;
        }
        public static String insertStudentString(Student student){
            return  "insert into Student(Matrikelnummer,Vorname,Nachname,EMail) " +
                    "values(" + student.getStudentID() +
                    ",\"" + student.getFirstName() + '\"' +
                    ",\"" + student.getFamilyName() + '\"' +
                    ",\"" + student.getEmailAddr().orElse("") + "\");";
        }

        public static String updateStudentString(Student student){
            return  "update Student set " +
                    "Vorname = \"" + student.getFirstName() + "\"," +
                    "Nachname = \"" + student.getFamilyName() + "\"," +
                    "EMail = \"" + student.getEmailAddr().orElse("") + '\"' +
                    "where Matrikelnummer =" + student.getStudentID();
        }

        public static  String deleteStudentString(Student student){
            return  "delete from Student where Matrikelnummer =" + student.getStudentID();
        }
    }

    private static final String FILE_NAME = "databaseFile.db";
    private static final String PATH_TO_DATABASE_DIR = System.getProperty("user.home") + "/sqlite";
    private static final String PATH_TO_DATABASE_FILE = "jdbc:sqlite:" + PATH_TO_DATABASE_DIR + '/' + FILE_NAME;
    private static final Path createScriptPath = Path.of("src/main/resources/createScript.sql");

    public static void initDatabase() {
        createNewDirectory();
        createNewDatabaseFile();
        createDatabaseTables();
    }

    @SuppressWarnings("unused")
    private static void createNewDirectory() {
        File newDir = new File(PATH_TO_DATABASE_DIR);
        if (!newDir.exists()) {
            newDir.mkdirs();
        }
    }

    @SuppressWarnings("unused")
    private static void createNewDatabaseFile() {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                DatabaseMetaData meta = connection.getMetaData();
                System.err.println("The driver name is " + meta.getDriverName());
                System.err.println("A new database has been created.");
            }
        } catch (SQLException exception) {
            System.err.println(exception.getMessage());
        }
    }

    @SuppressWarnings("unused")
    private static void createDatabaseTables() {
        String createScript = "";
        try {
            createScript = Files.readString(createScriptPath);
        } catch (Exception exception) {
            System.err.println("Database script file could not be read!\n" + exception.getMessage());
        }
        String[] parts = createScript.split("#");
        for (String part : parts)
            executeStatement(part);
    }

    private static Connection getConnection() {
        try {
            return DriverManager.getConnection(PATH_TO_DATABASE_FILE);
        } catch (Exception exception) {
            System.err.println("Unable to get connection to database!\n" + exception.getMessage());
        }
        return null;
    }

    @SuppressWarnings("unused")
    public static ResultSet executeSelect(final String select, Connection connection) throws SQLException{
            if (connection != null)
                return connection.createStatement().executeQuery(select);
            else
                throw new SQLException("No connection to database!");
    }

    @SuppressWarnings("unused")
    private static void executeStatement(final String statement) {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                Statement stmt = connection.createStatement();
                stmt.execute(statement);
            }
        } catch (SQLException exception) {
            System.err.println(exception.getMessage());
        }
    }

    private static Student fillStudentWithResult(ResultSet result) throws SQLException {
        if(Student.getLoadedStudent(result.getInt("Matrikelnummer")).isPresent())
            return null;
        if (result.getString("EMail").isEmpty())
            return new Student(
                    result.getInt("Matrikelnummer"),
                    result.getString("Vorname"),
                    result.getString("Nachname")
            );
        else
            return new Student(
                    result.getInt("Matrikelnummer"),
                    result.getString("Vorname"),
                    result.getString("Nachname"),
                    result.getString("EMail")
            );
    }

    @SuppressWarnings("unused")
    public static Set<Student> loadStudentenFromDatabase() {
        final Set<Student> loadedStudenten = new HashSet<>();
        try (ResultSet results = executeSelect(StringGenerator.selectAllStundeten, getConnection())) {
            while (results.next()) {
                Student newStudent = fillStudentWithResult(results);
                if(newStudent != null)
                    loadedStudenten.add(newStudent);
            }
        } catch (SQLException exception) {
            System.err.println("Unable to load Studenten!\n" + exception.getMessage());
        }
        return loadedStudenten;
    }

    @SuppressWarnings("unused")
    public static Optional<Student> selectStudent(final long matrikelnummer) {
        Optional<Student> selectedStudent = Optional.empty();
        try {
            ResultSet results = Database.executeSelect(StringGenerator.selectStudentString(matrikelnummer), getConnection());
            selectedStudent = Optional.ofNullable(fillStudentWithResult(results));
        } catch (Exception exception) {
            System.err.println("Unable to load Student!\n" + exception.getMessage());
        }
        if(selectedStudent.isEmpty())
            return Student.getLoadedStudent(matrikelnummer);
        return selectedStudent;
    }

    @SuppressWarnings("unused")
    public static void update(final Object object) {
        switch (object) {
            case Student student -> executeStatement(StringGenerator.updateStudentString(student));
            case Projekt projekt -> System.err.println("Projekt not ready");
            case Ansprechpartner partner -> System.err.println("Ansprechpartner not ready");
            case Unternehmen unternehmen -> System.err.println("Unternehemen not ready");
            default -> System.err.println("No implementation for class : " + object.getClass());
        }
    }

    @SuppressWarnings("unused")
    public static void delete(final Object object) {
        switch (object) {
            case Student student -> executeStatement(StringGenerator.deleteStudentString(student));
            case Projekt projekt -> System.err.println("Projekt not ready");
            case Ansprechpartner partner -> System.err.println("Ansprechpartner not ready");
            case Unternehmen unternehmen -> System.err.println("Unternehemen not ready");
            default -> System.err.println("No implementation for class : " + object.getClass());
        }
    }

    @SuppressWarnings("unused")
    public static void insert(final Object object) {
        switch (object) {
            case Student student -> executeStatement(StringGenerator.insertStudentString(student));
            case Projekt projekt -> System.err.println("Projekt not ready");
            case Ansprechpartner partner -> System.err.println("Ansprechpartner not ready");
            case Unternehmen unternehmen -> System.err.println("Unternehemen not ready");
            default -> System.err.println("No implementation for class : " + object.getClass());
        }
    }
}
