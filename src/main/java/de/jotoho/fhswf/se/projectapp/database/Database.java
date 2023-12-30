package de.jotoho.fhswf.se.projectapp.database;

import de.jotoho.fhswf.se.projectapp.Student;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;


public class Database implements DatabaseInterface{
    private static final String FILE_NAME = "databaseFile.db";
    private static final String PATH_TO_DATABASE_DIR = System.getProperty("user.home")+"/sqlite";
    private static final String PATH_TO_DATABASE_FILE = "jdbc:sqlite:" + PATH_TO_DATABASE_DIR + '/' + FILE_NAME;
    private static final Path createScriptPath = Path.of("src/main/resources/createScript.sql");

    public static void initDatabase(){
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
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    private static void createDatabaseTables(){
        String createScript = "";
        try {
            createScript = Files.readString(createScriptPath);
        }catch (Exception e){
            System.out.println("Database script file could not be read!\n" + e.getMessage());
        }
        String [] parts = createScript.split("#");
        for(String part : parts)
            executeStatement(part);
    }
    private static Connection getConnection(){
        try {
            return DriverManager.getConnection(PATH_TO_DATABASE_FILE);
        }catch(Exception e){
            System.out.println("Unable to get connection to database!\n" + e.getMessage());
        }
        return null;
    }

    @SuppressWarnings("unused")
    public static ResultSet executeSelect(final String select,Connection connection){
        ResultSet results = null;
        try{
            if (connection != null) {
                Statement statement = connection.createStatement();
                results = statement.executeQuery(select);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return results;
    }

    @SuppressWarnings("unused")
    private static void executeStatement(final String statement) {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                Statement stmt = connection.createStatement();
                stmt.execute(statement);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    public static Set<Student> loadStudentenFromDatabase(){
        Set<Student> loadedStudenten = new HashSet<>(Collections.emptySet());
        try(ResultSet results = executeSelect(StringGenerator.selectAllStundeten,getConnection())) {
            while (results.next()) {
                if(Student.getLoadedStudent(results.getInt("Matrikelnummer")).isPresent())
                    continue;
                Student newStudent = null;
                if(results.getString("EMail").isEmpty())
                     newStudent = new Student(
                            results.getInt("Matrikelnummer"),
                            results.getString("Vorname"),
                            results.getString("Nachname")
                    );
                else
                    newStudent = new Student(
                            results.getInt("Matrikelnummer"),
                            results.getString("Vorname"),
                            results.getString("Nachname"),
                            results.getString("EMail")
                    );
                loadedStudenten.add(newStudent);
            }
        } catch (SQLException e) {
            System.out.println("Unable to load Studenten!\n" + e.getMessage());
        }
        return loadedStudenten;
    }

    @SuppressWarnings("unused")
    public static void update(Object object) {
        switch(object.getClass().getSimpleName()){
            case "Student" -> executeStatement(StringGenerator.updateStudentString((Student) object));
            case "Projekt" -> System.out.println("Projekt not ready");
            case "Ansprechpartner" -> System.out.println("Ansprechpartner not ready");
            case "Unternehmen" -> System.out.println("Unternehemen not ready");
            default -> System.out.println("No implementation for class : " + object.getClass());
        }
    }

    @SuppressWarnings("unused")
    public static void delete(Object object) {
        switch(object.getClass().getSimpleName()){
            case "Student" -> executeStatement(StringGenerator.deleteStudentString((Student) object));
            case "Projekt" -> System.out.println("Projekt not ready");
            case "Ansprechpartner" -> System.out.println("Ansprechpartner not ready");
            case "Unternehmen" -> System.out.println("Unternehemen not ready");
            default -> System.out.println("No implementation for class : " + object.getClass());
        }
    }

    @SuppressWarnings("unused")
    public static void insert(Object object) {
        switch(object.getClass().getSimpleName()){
            case "Student" -> executeStatement(StringGenerator.insertStudentString((Student) object));
            case "Projekt" -> System.out.println("Projekt not ready");
            case "Ansprechpartner" -> System.out.println("Ansprechpartner not ready");
            case "Unternehmen" -> System.out.println("Unternehemen not ready");
            default -> System.out.println("No implementation for class : " + object.getClass());
        }
    }
}
