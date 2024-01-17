package de.jotoho.fhswf.se.projectapp;

import static java.util.Objects.*;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("unused")
public class Student {
    private static final ConcurrentMap<Long, WeakReference<Student>> existingStudents =
            new ConcurrentHashMap<>();

    private String firstName;
    private String familyName;
    private final long studentID;
    private Optional<String> emailAddr = Optional.empty();

    @SuppressWarnings("unused")
    public Optional<String> getEmailAddr() {
        return this.emailAddr;
    }

    @SuppressWarnings("unused")
    public String getFirstName() {
        return this.firstName;
    }

    @SuppressWarnings("unused")
    public String getFamilyName() {
        return this.familyName;
    }

    @SuppressWarnings("unused")
    public long getStudentID() {
        return this.studentID;
    }

    @SuppressWarnings("unused")
    private static boolean verifyEmail(final String email) {
        if (isNull(email))
            return false;
        else if (email.isBlank() || !email.contains("@"))
            return false;
        else
            return true;
    }

    @SuppressWarnings("unused")
    public void setFirstName(final String firstName) {
        requireNonNull(firstName);
        if (firstName.isBlank())
            throw new IllegalArgumentException("Blank first names are not permitted");
        this.firstName = firstName.strip();
    }

    @SuppressWarnings("unused")
    public void setFamilyName(final String familyName) {
        requireNonNull(familyName);
        if (familyName.isBlank())
            throw new IllegalArgumentException("Blank family names are not permitted");
        this.familyName = familyName.strip();
    }

    @SuppressWarnings("unused")
    public void setEmail(final String email) {
        if (isNull(email))
            this.emailAddr = Optional.empty();
        else if (verifyEmail(email))
            this.emailAddr = Optional.of(email);
        else
            throw new IllegalArgumentException("Invalid email address : " + email);
    }

    @SuppressWarnings("unused")
    public static Optional<Student> getLoadedStudent(final long studentID) {
        return Optional.ofNullable(
                existingStudents.getOrDefault(studentID,
                                              new WeakReference<>(null))
                                .get());
    }

    @SuppressWarnings("unused")
    public Student(final long studentID,
                   final String firstName,
                   final String familyName,
                   final String email) {
        requireNonNull(firstName);
        requireNonNull(familyName);

        this.studentID = studentID;
        this.setFirstName(firstName);
        this.setFamilyName(familyName);
        this.setEmail(email);

        final Student newStudent = this;

        existingStudents.compute(studentID, (key, weakStudent) -> {
            if (isNull(weakStudent))
                return new WeakReference<>(newStudent);
            final Student prevStudent = weakStudent.get();
            return new WeakReference<>(isNull(prevStudent) ? newStudent : prevStudent);
        });

        if (existingStudents.getOrDefault(studentID, null).get() != this)
            throw new IllegalStateException("Attempted to create duplicate student");
    }

    @SuppressWarnings("unused")
    public Student(final long studentID,
                   final String firstName,
                   final String familyName) {
        this(studentID,
             firstName,
             familyName,
             null);
    }

    @Override
    public String toString() {
        final var emailDisplay = this.emailAddr.map(s -> " <" + s + ">")
                                               .orElse("");
        return this.firstName + " " +
               this.familyName + emailDisplay;
    }
}
