package de.jotoho.fhswf.se.projectapp.database;

import de.jotoho.fhswf.se.projectapp.Student;

public final class StringGenerator {

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
