/*
    SPDX-License-Identifier: AGPL-3.0-only
    SPDX-FileCopyrightText: 2024 Tim Beckmann <beckmann.tim@fh-swf.de>
    SPDX-FileCopyrightText: 2024 Jonas Tobias Hopusch <git@jotoho.de>
*/
package de.jotoho.fhswf.se.projectapp.backend.database;

import de.jotoho.fhswf.se.projectapp.Projekt;
import de.jotoho.fhswf.se.projectapp.Student;
import de.jotoho.fhswf.se.projectapp.backend.stringgenerator.StudentStringGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static de.jotoho.fhswf.se.projectapp.backend.database.Database.*;

public final class StudentDatabase {

    public static final Set<Student> loadedStudents = new HashSet<>();

    private StudentDatabase() {
    }

    public static void initStudent(){
        loadedStudents.addAll(loadStudentsFromDatabase());
    }

    public static void addStudent(final Student student) {
        loadedStudents.add(student);
    }

    public static void removeStudent(Student student) {
        loadedStudents.remove(student);
        for(final Projekt projekt : ProjektDatabase.getProjekte())
            if(projekt.getMemberView().contains(student))
                projekt.removeMember(student);
    }

    public static Set<Student> getStudents() {
        return Collections.unmodifiableSet(loadedStudents);
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

    public static boolean checkIfStudentExists(final long matrikelnummer) {
        return getStudentIDsFromDatabase().contains(matrikelnummer);
    }

    public static Set<Student> loadStudentsFromDatabase() {
        final Set<Student> loadedStudents = new HashSet<>();
        try (ResultSet results = executeQuery(StudentStringGenerator.SELECT_ALL, getConnection())) {
            while (results.next()) {
                final Student newStudent = fillStudentWithResult(results);
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
            final ResultSet results = executeQuery(StudentStringGenerator.selectStudentString(matrikelnummer), getConnection());
            selectedStudent = Optional.ofNullable(fillStudentWithResult(results));
        } catch (final Exception exception) {
            System.err.println("Unable to select Student!\n" + exception.getMessage());
        }
        if (selectedStudent.isEmpty())
            return Student.getLoadedStudent(matrikelnummer);
        return selectedStudent;
    }

    public static Optional<Student> getStudent(final long matrikelnummer) {
        Optional<Student> matchingStudent = Optional.empty();
        for (final Student student : loadedStudents) {
            if (student.getStudentID() == matrikelnummer)
                matchingStudent = Optional.of(student);
        }
        return matchingStudent;
    }

    public static Set<Long> getStudentIDsFromDatabase() {
        final Set<Long> ids = new HashSet<>();
        try {
            final ResultSet results = executeQuery(StudentStringGenerator.GET_ALL_IDS, getConnection());
            while (results.next())
                ids.add(results.getLong("Matrikelnummer"));
        } catch (final SQLException sqlException) {
            System.err.println(sqlException.getMessage());
        }
        return ids;
    }

    public static void removeDeletedStudents() {
        for (final long studentID : getStudentIDsFromDatabase())
            if (getStudent(studentID).isEmpty())
                executeStatement(StudentStringGenerator.deleteStudentString(studentID));
    }

    @SuppressWarnings("unused")
    public static void updateStudents() {
        for (final Student student : getStudents())
            update(student);
    }

    @SuppressWarnings("unused")
    public static void insertNewStudents() {
        for (final Student student : loadedStudents)
            if (!checkIfStudentExists(student.getStudentID()))
                insert(student);
    }

    public static void saveStudents() {
        insertNewStudents();
        updateStudents();
        removeDeletedStudents();
    }
}
