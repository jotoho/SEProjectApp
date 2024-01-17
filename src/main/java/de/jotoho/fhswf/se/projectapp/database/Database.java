package de.jotoho.fhswf.se.projectapp.database;

import de.jotoho.fhswf.se.projectapp.Ansprechpartner;
import de.jotoho.fhswf.se.projectapp.Projekt;
import de.jotoho.fhswf.se.projectapp.Student;
import de.jotoho.fhswf.se.projectapp.Unternehmen;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;


public final class Database {
    private static final String FILE_NAME = "databaseFile.db";
    private static final String PATH_TO_DATABASE_DIR = System.getProperty("user.home") + "/sqlite";
    private static final String PATH_TO_DATABASE_FILE = "jdbc:sqlite:" + PATH_TO_DATABASE_DIR + '/' + FILE_NAME;
    private static final Path createScriptPath = Path.of("src/main/resources/createScript.sql");
    private static final  Set<Student> loadedStudents = new HashSet<>();
    private static final  Set<Unternehmen> loadedUnternehmen = new HashSet<>();
    private static final Set<Ansprechpartner> loadedAnsprechpartner = new HashSet<>();

    private static final Set<Projekt> loadedProjekte = new HashSet<>();

    public static void initDatabase() {
        createNewDirectory();
        createNewDatabaseFile();
        createDatabaseTables();
        loadedStudents.addAll(loadStudentsFromDatabase());
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

    public static Set<Student> getStudents() {
        return Collections.unmodifiableSet(loadedStudents);
    }
    public static Set<Unternehmen> getUnternehmen() {
        return Collections.unmodifiableSet(loadedUnternehmen);
    }

    public static Set<Ansprechpartner> getAnsprechpartner() {
        return Collections.unmodifiableSet(loadedAnsprechpartner);
    }

    public static Set<Projekt> getProjekte() {
        return Collections.unmodifiableSet(loadedProjekte);
    }

    private static Set<Long> getStudentIDsFromDatabase() {
        final Set<Long> ids = new HashSet<>();
        try {
            final ResultSet results = executeQuery(StringGenerator.GET_ALL_STUDENT_IDS, getConnection());
            while (results.next())
                ids.add(results.getLong("Matrikelnummer"));
        } catch (final SQLException sqlException) {
            System.err.println(sqlException.getMessage());
        }
        return ids;
    }

    public static boolean checkIfStudentExists(final long matrikelnummer) {
        return getStudentIDsFromDatabase().contains(matrikelnummer);
    }

    public static void saveStudents() {
        insertNewStudents();
        updateStudents();
        removeDeletedStudents();
    }

    @SuppressWarnings("unused")
    private static void removeDeletedStudents() {
        for (final long studentID : getStudentIDsFromDatabase())
            if (getStudent(studentID).isEmpty())
                executeStatement(StringGenerator.deleteStudentString(studentID));
    }

    @SuppressWarnings("unused")
    private static void updateStudents() {
        for (final Student student : getStudents())
            update(student);
    }

    @SuppressWarnings("unused")
    private static void insertNewStudents() {
        for (final Student student : loadedStudents)
            if (!checkIfStudentExists(student.getStudentID()))
                insert(student);
    }

    public static Optional<Student> getStudent(final long matrikelnummer) {
        Optional<Student> matchingStudent = Optional.empty();
        for (final Student student : loadedStudents) {
            if (student.getStudentID() == matrikelnummer)
                matchingStudent = Optional.of(student);
        }
        return matchingStudent;
    }

    public static Optional<Unternehmen> getUnternehmen(final long id){
        Optional<Unternehmen> matchingUnternehmen = Optional.empty();
        for (final Unternehmen unternehmen : loadedUnternehmen) {
            if (unternehmen.getID() == id)
                matchingUnternehmen = Optional.of(unternehmen);
        }
        return matchingUnternehmen;
    }

    public static Optional<Ansprechpartner> getAnsprechpartner(final long id){
        Optional<Ansprechpartner> matchingAnsprechpartner = Optional.empty();
        for (final Ansprechpartner ansprechpartner : loadedAnsprechpartner) {
            if (ansprechpartner.getID() == id)
                matchingAnsprechpartner = Optional.of(ansprechpartner);
        }
        return matchingAnsprechpartner;
    }

    @SuppressWarnings("unused")
    public static void addStudent(final Student student) {
        loadedStudents.add(student);
    }

    @SuppressWarnings("unused")
    public static void removeStudent(final Student student) {
        loadedStudents.remove(student);
    }

    public static void addAnsprechpartner(final Ansprechpartner ansprechpartner) {
        loadedAnsprechpartner.add(ansprechpartner);
    }

    @SuppressWarnings("unused")
    public static void removeAnsprechpartner(final Ansprechpartner ansprechpartner) {
        if(loadedProjekte   .stream()
                            .map(Projekt::getContact)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .anyMatch(ansprechpartner::equals))
            System.err.println("Contact still in use!");
        else
            loadedAnsprechpartner.remove(ansprechpartner);
    }

    public static void addUnternehmen(final Unternehmen unternehmen) {
        loadedUnternehmen.add(unternehmen);
    }

    @SuppressWarnings("unused")
    public static void removeUnternehmen(final Unternehmen unternehmen) {
        if(loadedAnsprechpartner.stream().map(Ansprechpartner::getOrganization).anyMatch(unternehmen::equals))
            System.err.println("Organization still in use!");
        else
            loadedUnternehmen.remove(unternehmen);
    }

    @SuppressWarnings("unused")
    public static ResultSet executeQuery(final String select, final Connection connection) throws SQLException {
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

    private static Student fillStudentWithResult(final ResultSet result) throws SQLException {
        Objects.requireNonNull(result);
        if (Student.getLoadedStudent(result.getLong("Matrikelnummer")).isPresent())
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
    public static Set<Student> loadStudentsFromDatabase() {
        final Set<Student> loadedStudents = new HashSet<>();
        try (ResultSet results = executeQuery(StringGenerator.SELECT_ALL_STUDENTS, getConnection())) {
            while (results.next()) {
                Student newStudent = fillStudentWithResult(results);
                if (newStudent != null)
                    loadedStudents.add(newStudent);
            }
        } catch (SQLException exception) {
            System.err.println("Unable to load Studenten!\n" + exception.getMessage());
        }
        return loadedStudents;
    }

    @SuppressWarnings("unused")
    public static Optional<Student> selectStudentFromDatabase(final long matrikelnummer) {
        Optional<Student> selectedStudent = Optional.empty();
        try {
            final ResultSet results = Database.executeQuery(StringGenerator.selectStudentString(matrikelnummer), getConnection());
            selectedStudent = Optional.ofNullable(fillStudentWithResult(results));
        } catch (final Exception exception) {
            System.err.println("Unable to select Student!\n" + exception.getMessage());
        }
        if (selectedStudent.isEmpty())
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
            case Student student -> executeStatement(StringGenerator.deleteStudentString(student.getStudentID()));
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

    private static final class StringGenerator {
        public static String SELECT_ALL_STUDENTS = "select * from Student";
        public static String GET_ALL_STUDENT_IDS = "select Matrikelnummer from Student ";

        private StringGenerator() {
        }

        public static String selectStudentString(final long Matrikelnummer) {
            return "select * from Student where Matrikelnummer = " + Matrikelnummer;
        }

        public static String insertStudentString(final Student student) {
            return "insert into Student(Matrikelnummer,Vorname,Nachname,EMail) " +
                    "values(" + student.getStudentID() +
                    ",\"" + student.getFirstName() + '\"' +
                    ",\"" + student.getFamilyName() + '\"' +
                    ",\"" + student.getEmailAddr().orElse("") + "\");";
        }

        public static String updateStudentString(final Student student) {
            return "update Student set " +
                    "Vorname = \"" + student.getFirstName() + "\"," +
                    "Nachname = \"" + student.getFamilyName() + "\"," +
                    "EMail = \"" + student.getEmailAddr().orElse("") + '\"' +
                    "where Matrikelnummer =" + student.getStudentID();
        }

        public static String deleteStudentString(final long studentID) {
            return "delete from Student where Matrikelnummer =" + studentID;
        }
    }
}
