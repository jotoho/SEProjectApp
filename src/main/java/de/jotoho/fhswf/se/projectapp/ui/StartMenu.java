package de.jotoho.fhswf.se.projectapp.ui;

import de.jotoho.fhswf.se.projectapp.Student;
import de.jotoho.fhswf.se.projectapp.database.Database;

import java.util.*;

public final class StartMenu {
    private static final String ROLE_STUDENT = "Student";
    private static final String ROLE_EMPLOYEE = "Angestellter";
    private StartMenu() {
    }
    /*
    @SuppressWarnings("unused")
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
    */




}
