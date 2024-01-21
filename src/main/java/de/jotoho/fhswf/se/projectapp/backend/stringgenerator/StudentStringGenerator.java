package de.jotoho.fhswf.se.projectapp.backend.stringgenerator;

import de.jotoho.fhswf.se.projectapp.Student;

import java.util.Objects;

@SuppressWarnings("unused")
public final class StudentStringGenerator {
    public static String SELECT_ALL = "select * from Student";
    public static String GET_ALL_IDS = "select Matrikelnummer from Student ";

    private StudentStringGenerator() {
    }

    public static String selectStudentString(final long Matrikelnummer) {
        return "select * from Student where Matrikelnummer = " + Matrikelnummer;
    }

    public static String insertStudentString(final Student student) {
        Objects.requireNonNull(student);
        return "insert into Student(Matrikelnummer,Vorname,Nachname,EMail) " +
                "values(" + student.getStudentID() +
                ",\"" + student.getFirstName() + '\"' +
                ",\"" + student.getFamilyName() + '\"' +
                ",\"" + student.getEmailAddr().orElse("") + "\");";
    }

    public static String updateStudentString(final Student student) {
        Objects.requireNonNull(student);
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
