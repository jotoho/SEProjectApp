package de.jotoho.fhswf.se.projectapp.ui;

import de.jotoho.fhswf.se.projectapp.Student;
import de.jotoho.fhswf.se.projectapp.database.Database;

import java.util.*;

import static de.jotoho.fhswf.se.projectapp.ui.StudentMenu.*;

@SuppressWarnings("unused")
public final class StartMenu {
    private static final String ROLE_STUDENT = "Student";
    private static final String ROLE_EMPLOYEE = "Angestellter";
    private static final String STUDENT_MENU = "Studentenmenü";
    private static final String OPTION_SAVE = "Speichern";
    private static final String OPTION_EXIT = "Beenden";
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

    public static void startMenu(){
        final List<OptionSelectionMenu.Option<String>> optionList = new ArrayList<>();
        optionList.add(new OptionSelectionMenu.Option<>(STUDENT_MENU, Set.of("Student"), true, STUDENT_MENU, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_SAVE, Set.of("Save"), true, OPTION_SAVE, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_EXIT, Set.of("Exit"), true, OPTION_EXIT, null));
        final var selectMenu = new OptionSelectionMenu<>("Wählen sie ihre Option.", optionList);

        selectMenu.activate();

        final String option = selectMenu.getSelectedOption()
                .map(OptionSelectionMenu.Option::get).orElse("Fehler");


        switch (option){
            case STUDENT_MENU -> studentMenu();
            case OPTION_SAVE -> {
                Database.saveStudents();
                studentMenu();
            }
            case OPTION_EXIT -> System.exit(0);
        }
    }


}
