/*
    SPDX-License-Identifier: AGPL-3.0-only
    SPDX-FileCopyrightText: 2024 Tim Beckmann <beckmann.tim@fh-swf.de>
    SPDX-FileCopyrightText: 2024 Jonas Tobias Hopusch <git@jotoho.de>
*/
package de.jotoho.fhswf.se.projectapp.backend.database;

import de.jotoho.fhswf.se.projectapp.Ansprechpartner;
import de.jotoho.fhswf.se.projectapp.Projekt;
import de.jotoho.fhswf.se.projectapp.Student;
import de.jotoho.fhswf.se.projectapp.Unternehmen;
import de.jotoho.fhswf.se.projectapp.backend.stringgenerator.AnsprechpartnerStringGenerator;
import de.jotoho.fhswf.se.projectapp.backend.stringgenerator.ProjektStringGenerator;
import de.jotoho.fhswf.se.projectapp.backend.stringgenerator.StudentStringGenerator;
import de.jotoho.fhswf.se.projectapp.backend.stringgenerator.UnternehmenStringGenerator;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Objects;
import java.util.Scanner;


public final class Database {
    private static final String FILE_NAME = "databaseFile.db";
    private static final String PATH_TO_DATABASE_DIR = System.getProperty("user.home") + "/sqlite";
    private static final String PATH_TO_DATABASE_FILE = "jdbc:sqlite:" + PATH_TO_DATABASE_DIR + '/' + FILE_NAME;
    private static final Path createScriptPath = Path.of("src/main/resources/createScript.sql");


    public static void initDatabase() {
        createNewDirectory();
        createNewDatabaseFile();
        createDatabaseTables();

        StudentDatabase.initStudent();
        UnternehmenDatabase.initUnternehmen();
        AnsprechpartnerDatabase.initAnsprechpartner();
        ProjektDatabase.initProjekt();
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
        final String[] parts = createScript.split("#");
        for (String part : parts)
            executeStatement(part);
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(PATH_TO_DATABASE_FILE);
        } catch (Exception exception) {
            System.err.println("Unable to get connection to database!\n" + exception.getMessage());
        }
        return null;
    }

    public static ResultSet executeQuery(final String select, final Connection connection) throws SQLException {
        if (connection != null)
            return connection.createStatement().executeQuery(select);
        else
            throw new SQLException("No connection to database!");
    }

    @SuppressWarnings("unused")
    public static void executeStatement(final String statement) {
        if(statement == null)
            return;
        try (final Connection connection = getConnection()) {
            if (connection != null) {
                Statement stmt = connection.createStatement();
                stmt.execute(statement);
            }
        } catch (SQLException exception) {
            System.err.println(exception.getMessage());
        }
    }

    public static long getIDFromInput(final Scanner scanner) {
        try {
            return scanner.nextLong();
        } catch (final Exception InputMismatchException) {
            scanner.next();
            System.out.print("ID hat das falsche Formart! Bitte erneut eingeben: ");
            return getIDFromInput(scanner);
        }
    }

    @SuppressWarnings("unused")
    public static void update(final Object object) {
        Objects.requireNonNull(object);
        switch (object) {
            case Student student -> executeStatement(StudentStringGenerator.updateStudentString(student));
            case Projekt projekt -> executeStatement(ProjektStringGenerator.updateProjektString(projekt));
            case Ansprechpartner partner -> executeStatement(AnsprechpartnerStringGenerator.updateAnsprechpartnerString(partner));
            case Unternehmen unternehmen -> executeStatement(UnternehmenStringGenerator.updateUnternehmenString(unternehmen));
            default -> System.err.println("No implementation for class : " + object.getClass());
        }
    }

    @SuppressWarnings("unused")
    public static void delete(final Object object) {
        Objects.requireNonNull(object);
        switch (object) {
            case Student student ->
                    executeStatement(StudentStringGenerator.deleteStudentString(student.getStudentID()));
            case Projekt projekt -> executeStatement(ProjektStringGenerator.deleteProjektString(projekt.getID()));
            case Ansprechpartner partner -> executeStatement(AnsprechpartnerStringGenerator.deleteAnsprechpartnerString(partner.getID()));
            case Unternehmen unternehmen -> executeStatement(UnternehmenStringGenerator.deleteUnternehmenString(unternehmen.getID()));
            default -> System.err.println("No implementation for class : " + object.getClass());
        }
    }

    @SuppressWarnings("unused")
    public static void insert(final Object object) {
        Objects.requireNonNull(object);
        switch (object) {
            case Student student -> executeStatement(StudentStringGenerator.insertStudentString(student));
            case Projekt projekt -> executeStatement(ProjektStringGenerator.insertProjektString(projekt));
            case Ansprechpartner partner -> executeStatement(AnsprechpartnerStringGenerator.insertAnsprechpartnerString(partner));
            case Unternehmen unternehmen -> executeStatement(UnternehmenStringGenerator.insertUnternehmenString(unternehmen));
            default -> System.err.println("No implementation for class : " + object.getClass());
        }
    }

    public static void save(){
        StudentDatabase.saveStudents();
        UnternehmenDatabase.saveUnternehmen();
        AnsprechpartnerDatabase.saveAnsprechpartner();
        ProjektDatabase.saveProjekte();
    }

}
