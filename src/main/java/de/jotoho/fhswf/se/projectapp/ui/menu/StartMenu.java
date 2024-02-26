/*
    SPDX-License-Identifier: AGPL-3.0-only
    SPDX-FileCopyrightText: 2024 Tim Beckmann <beckmann.tim@fh-swf.de>
    SPDX-FileCopyrightText: 2024 Jonas Tobias Hopusch <git@jotoho.de>
*/
package de.jotoho.fhswf.se.projectapp.ui.menu;

import de.jotoho.fhswf.se.projectapp.backend.database.Database;
import de.jotoho.fhswf.se.projectapp.backend.database.StudentDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static de.jotoho.fhswf.se.projectapp.ui.menu.AnsprechpartnerMenu.ansprechpartnerMenu;
import static de.jotoho.fhswf.se.projectapp.ui.menu.ProjektMenu.projektMenu;
import static de.jotoho.fhswf.se.projectapp.ui.menu.ReviewMenu.reviewMenu;
import static de.jotoho.fhswf.se.projectapp.ui.menu.StudentMenu.studentMenu;
import static de.jotoho.fhswf.se.projectapp.ui.menu.UnternehmenMenu.unternehmenMenu;

@SuppressWarnings("unused")
public final class StartMenu {
    private static final String ROLE_STUDENT = "Student";
    private static final String ROLE_EMPLOYEE = "Angestellter";
    private static final String STUDENT_MENU = "Studentenmenü";
    private static final String UNTERNEHMEN_MENU = "Unternehmenmenü";
    private static final String ANSPRECHPARTNER_MENU = "Ansprechpartnermenü";
    private static final String PROJEKT_MENU = "Projektmenü";
    private static final String REVIEW_MENU = "Reviewmenü";
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
        optionList.add(new OptionSelectionMenu.Option<>(UNTERNEHMEN_MENU, Set.of("Unternehmen"), true, UNTERNEHMEN_MENU, null));
        optionList.add(new OptionSelectionMenu.Option<>(ANSPRECHPARTNER_MENU, Set.of("Ansprechpartner"), true, ANSPRECHPARTNER_MENU, null));
        optionList.add(new OptionSelectionMenu.Option<>(PROJEKT_MENU, Set.of("Projekt"), true, PROJEKT_MENU, null));
        optionList.add(new OptionSelectionMenu.Option<>(REVIEW_MENU, Set.of("Review"), true, REVIEW_MENU, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_SAVE, Set.of("Save"), true, OPTION_SAVE, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_EXIT, Set.of("Exit"), true, OPTION_EXIT, null));
        final var selectMenu = new OptionSelectionMenu<>("Startmenü", optionList);

        selectMenu.activate();

        final String option = selectMenu.getSelectedOption()
                .map(OptionSelectionMenu.Option::get).orElse("Fehler");


        switch (option){
            case STUDENT_MENU -> studentMenu();
            case UNTERNEHMEN_MENU -> unternehmenMenu();
            case ANSPRECHPARTNER_MENU -> ansprechpartnerMenu();
            case PROJEKT_MENU -> projektMenu();
            case REVIEW_MENU -> reviewMenu();
            case OPTION_SAVE -> {
                Database.save();
                startMenu();
            }
            case OPTION_EXIT -> System.exit(0);
        }
    }


}
