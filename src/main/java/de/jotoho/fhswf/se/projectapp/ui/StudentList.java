package de.jotoho.fhswf.se.projectapp.ui;

import de.jotoho.fhswf.se.projectapp.Student;
import static java.util.Objects.requireNonNull;

import java.util.*;
import java.util.function.Consumer;

public final class StudentList {
    private StudentList() {

    }

    public static String getFormattedStudentList(final Collection<Student> students) {
        requireNonNull(students);
        final int maxLengthIDs = students.parallelStream()
                                         .mapToLong(Student::getStudentID)
                                         .mapToObj(Long::toUnsignedString)
                                         .mapToInt(String::length)
                                         .max()
                                         .orElse(0);
        final int maxLengthFamilyName = students.parallelStream()
                                                .map(Student::getFamilyName)
                                                .mapToInt(String::length)
                                                .max()
                                                .orElse(0);
        final int maxLengthFirstName = students.parallelStream()
                                               .map(Student::getFirstName)
                                               .mapToInt(String::length)
                                               .max()
                                               .orElse(0);
        final int maxLengthEmail = students.parallelStream()
                                           .map(Student::getEmailAddr)
                                           .map(o -> o.orElse(null))
                                           .filter(Objects::nonNull)
                                           .distinct()
                                           .map(s -> "<" + s + ">")
                                           .mapToInt(String::length)
                                           .max()
                                           .orElse(0);

        final StringBuilder result = new StringBuilder();
        final Consumer<Student> printFn = st -> {
            final String sID = Long.toUnsignedString(st.getStudentID());
            final String sFamilyName = st.getFamilyName();
            final String sFirstName = st.getFirstName();
            final String sEmail = st.getEmailAddr().map(s -> "<" + s + ">").orElse("");

            result.repeat(" ", maxLengthIDs - sID.length())
                  .append(sID)
                  .repeat(" ", maxLengthFirstName - sFirstName.length() + 1)
                  .append(sFirstName)
                  .repeat(" ", maxLengthFamilyName - sFamilyName.length() + 1)
                  .append(sFamilyName)
                  .repeat(" ", maxLengthEmail - sEmail.length() + 1)
                  .append(sEmail)
                  .append("\n");
        };

        students.stream()
                .sorted(Comparator.comparingLong(Student::getStudentID))
                .forEachOrdered(printFn);

        return result.toString();
    }

    public static void main(final String[] args) {
        final int NUM_STUDENTS = 30;
        final var students = new HashSet<Student>(NUM_STUDENTS);
        for (int i = 0; i < NUM_STUDENTS; i++) {
            long randomNumber = -1;
            while (randomNumber < 0
                   || Student.getLoadedStudent(randomNumber)
                             .isPresent()) {
                randomNumber = new Random().nextLong();
                randomNumber %= 1_000_000;
            }
            final Student s =
                    new Student(randomNumber,
                                "Max",
                                "Mustermann",
                                "max@mustermann.de");
            students.add(s);
        }

        System.out.println(getFormattedStudentList(students));
    }
}
