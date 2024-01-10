package de.jotoho.fhswf.se.projectapp.ui;

import de.jotoho.fhswf.se.projectapp.Student;
import de.jotoho.fhswf.se.projectapp.database.Database;

import java.util.*;

public final class StartMenu {
    private static final String DUMMY_FIRSTNAME = "Platzhalter";
    private static final String DUMMY_LASTNAME = "Platzhalter";
    private static final String ROLE_STUDENT = "Student";
    private static final String ROLE_EMPLOYEE = "Angestellter";
    private static final String OPTION_SHOW_STUDENTS = "Alle Studenten anzeigen";
    private static final String OPTION_SHOW_STUDENT = "Einen Studenten anzeigen";
    private static final String OPTION_EDIT_STUDENT = "Student bearbeiten";
    private static final String OPTION_CREATE_STUDENT = "Student erstellen";
    private static final String OPTION_DELETE_STUDENT = "Studenten löschen";
    private static final String OPTION_SAVE = "Speichern";
    private static final String OPTION_EXIT = "Beenden";

    private StartMenu() {
    }

    @SuppressWarnings("usused")
    public static void userRole() {
        final List<OptionSelectionMenu.Option<String>> optionList = new ArrayList<>();
        optionList.add(new OptionSelectionMenu.Option<>(ROLE_STUDENT, Set.of(ROLE_STUDENT), true, ROLE_STUDENT, null));
        optionList.add(new OptionSelectionMenu.Option<>(ROLE_EMPLOYEE, Set.of(ROLE_EMPLOYEE), true, ROLE_EMPLOYEE, null));
        final var selectMenu = new OptionSelectionMenu<>("Wählen sie ihre Rolle.", optionList);
        selectMenu.activate();

        if (selectMenu.getSelectedOption()
                .map(OptionSelectionMenu.Option::get)
                .filter(ROLE_STUDENT::equals).isPresent())
            checkStudentRegistered();
        else
            userOptions();
    }

    @SuppressWarnings("unused")
    public static long getMatrikelnummer(final Scanner scanner) {
        long input = 0;
        try {
            input = scanner.nextLong();
        } catch (final Exception InputMismatchException) {
            scanner.next();
            System.out.print("Matrikelnummer hat das falsche Formart! Bitte erneut eingeben: ");
            getMatrikelnummer(scanner);
        }
        return input;
    }

    @SuppressWarnings("unused")
    public static void checkStudentRegistered() {
        System.out.print("Bitte geben sie ihre Matrikelnummer ein: ");
        final Scanner scanner = new Scanner(System.in);
        final long studentID = getMatrikelnummer(scanner);
        if (Database.getStudent(studentID).isEmpty()) {
            StudentEditMenu.editStudent(new Student(
                    studentID,
                    DUMMY_FIRSTNAME,
                    DUMMY_LASTNAME
            ));
        }
        scanner.close();
        userOptions();
    }

    public static void userOptions() {
        final List<OptionSelectionMenu.Option<String>> optionList = new ArrayList<>();
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_SHOW_STUDENTS, Set.of("Student"), true, OPTION_SHOW_STUDENTS, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_SHOW_STUDENT, Set.of("Students"), true, OPTION_SHOW_STUDENT, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_CREATE_STUDENT, Set.of("Create"), true, OPTION_CREATE_STUDENT, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_EDIT_STUDENT, Set.of("Edit"), true, OPTION_EDIT_STUDENT, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_DELETE_STUDENT, Set.of("Delete"), true, OPTION_DELETE_STUDENT, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_SAVE, Set.of("Save"), true, OPTION_SAVE, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_EXIT, Set.of("Exit"), true, OPTION_EXIT, null));
        final var selectMenu = new OptionSelectionMenu<>("Wählen sie ihre Option.", optionList);

        selectMenu.activate();

        final String option = selectMenu.getSelectedOption()
                .map(OptionSelectionMenu.Option::get).orElse("Fehler");

        switch (option) {
            case OPTION_SHOW_STUDENTS -> {
                System.out.println(StudentList.getFormattedStudentList(Database.getStudents()));
                userOptions();
            }
            case OPTION_EDIT_STUDENT -> {
                System.out.print("Bitte Matrikelnummer des Studenten eingeben: ");
                Database.getStudent(getMatrikelnummer(new Scanner(System.in))).ifPresent(StudentEditMenu::editStudent);
                userOptions();
            }
            case OPTION_DELETE_STUDENT -> deleteStudentMenu();

            case OPTION_SHOW_STUDENT -> {
                System.out.print("Bitte Matrikelnummer des Studenten eingeben: ");
                Database.getStudent(getMatrikelnummer(new Scanner(System.in))).ifPresent(StudentEditMenu::listStudent);
                userOptions();
            }
            case OPTION_SAVE -> {
                Database.saveStudents();
                userOptions();
            }
            case OPTION_CREATE_STUDENT -> createStudentMenu();
            case OPTION_EXIT -> System.exit(0);
            default -> {
                System.out.print(option + ": Haben wir nich");
                userOptions();
            }
        }
    }

    private static void deleteStudentMenu() {
        System.out.print("Bitte Matrikelnummer des Studenten eingeben: ");
        final long matrikelnummer = getMatrikelnummer(new Scanner(System.in));
        final Optional<Student> student = Database.getStudent(matrikelnummer);
        if (student.isEmpty()) {
            System.out.print("Kein Student mit dieser Matrikelnummer vorhanden!");
            userOptions();
        }
        student.ifPresent(StudentEditMenu::listStudent);
        System.out.print("Wirklich löschen?[Y/N]");
        if (!new Scanner(System.in).next().equals("Y"))
            userOptions();
        student.ifPresent(Database::removeStudent);
        userOptions();
    }

    private static void createStudentMenu() {
        System.out.print("Bitte Matrikelnummer des Studenten eingeben: ");
        long matrikelnummer = getMatrikelnummer(new Scanner(System.in));
        if (Database.checkIfStudentExists(matrikelnummer)) {
            System.out.print("Matrikelnummer wird bereits verwendet!");
            userOptions();
        }
        final Student newStudent = new Student(matrikelnummer, DUMMY_FIRSTNAME, DUMMY_LASTNAME);
        StudentEditMenu.editStudent(newStudent);
        if (newStudent.getFirstName().equals(DUMMY_FIRSTNAME) || newStudent.getFamilyName().equals(DUMMY_LASTNAME)) {
            System.out.print("Bitte die Einträge mit " + DUMMY_FIRSTNAME + " oder " + DUMMY_LASTNAME + " ändern!");
            StudentEditMenu.editStudent(newStudent);
        }
        Database.addStudent(newStudent);
        userOptions();
    }


}
