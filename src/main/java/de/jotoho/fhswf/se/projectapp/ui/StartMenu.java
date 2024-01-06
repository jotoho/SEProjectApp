package de.jotoho.fhswf.se.projectapp.ui;

import de.jotoho.fhswf.se.projectapp.Student;
import de.jotoho.fhswf.se.projectapp.database.Database;

import java.util.*;

public final class StartMenu {
    private static final String DUMMY_FIRSTNAME = "Max";
    private static final String DUMMY_LASTNAME = "Mustermann";
    private static final String ROLE_STUDENT = "Student";
    private static final String ROLE_EMPLOYEE = "Angestellter";
    private static final String OPTION_SHOW_STUDENTS = "Alle Studenten anzeigen";
    private static final String OPTION_SHOW_STUDENT = "Einen Studenten anzeigen";
    private static final String OPTION_EDIT_STUDENT = "Student bearbeiten";

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
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_SHOW_STUDENTS, Set.of(), true, OPTION_SHOW_STUDENTS, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_SHOW_STUDENT, Set.of(), true, OPTION_SHOW_STUDENT, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_EDIT_STUDENT, Set.of(), true, OPTION_EDIT_STUDENT, null));
        final var selectMenu = new OptionSelectionMenu<>("Wählen sie ihre Option.", optionList);
        selectMenu.activate();

        final String option = selectMenu.getSelectedOption()
                .map(OptionSelectionMenu.Option::get).orElse("Fehler");

        switch (option) {
            case OPTION_SHOW_STUDENTS ->
                    System.out.println(StudentList.getFormattedStudentList(Database.getStudents()));
            case OPTION_EDIT_STUDENT -> {
                System.out.print("Bitte Matrikelnummer des Studenten eingeben: ");
                Database.getStudent(getMatrikelnummer(new Scanner(System.in))).ifPresent(StudentEditMenu::editStudent);
            }
            case OPTION_SHOW_STUDENT -> {
                System.out.print("Bitte Matrikelnummer des Studenten eingeben: ");
                Database.getStudent(getMatrikelnummer(new Scanner(System.in))).ifPresent(StudentEditMenu::listStudent);
            }
            default -> System.out.print(option + ": Haben wir nich");
        }
    }


}
