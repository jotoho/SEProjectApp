package de.jotoho.fhswf.se.projectapp.ui.menu;

import de.jotoho.fhswf.se.projectapp.Projekt;
import de.jotoho.fhswf.se.projectapp.backend.database.ProjektDatabase;
import de.jotoho.fhswf.se.projectapp.ui.editmenu.ProjektEditMenu;
import de.jotoho.fhswf.se.projectapp.ui.list.ProjektList;

import java.util.*;

import static de.jotoho.fhswf.se.projectapp.backend.database.Database.getIDFromInput;
import static de.jotoho.fhswf.se.projectapp.ui.menu.StartMenu.startMenu;

public final class ProjektMenu {
    private static final String OPTION_SHOW = "Alle Projekte anzeigen";
    private static final String OPTION_SHOW_ONE = "Ein Projekt anzeigen";
    private static final String OPTION_EDIT = "Projekt bearbeiten";
    private static final String OPTION_CREATE = "Projekt erstellen";
    private static final String OPTION_DELETE = "Projekt löschen";
    private static final String BACK = "Zurück";

    private ProjektMenu(){}

    public static void projektMenu(){
        final List<OptionSelectionMenu.Option<String>> optionList = new ArrayList<>();

        optionList.add(new OptionSelectionMenu.Option<>(OPTION_SHOW, Set.of("Alle"), true, OPTION_SHOW, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_SHOW_ONE, Set.of("Eins"), true, OPTION_SHOW_ONE, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_EDIT, Set.of("Edit"), true, OPTION_EDIT, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_CREATE, Set.of("Create"), true, OPTION_CREATE, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_DELETE, Set.of("Delete"), true, OPTION_DELETE, null));
        optionList.add(new OptionSelectionMenu.Option<>(BACK, Set.of("Back"), true, BACK, null));


        final var selectMenu = new OptionSelectionMenu<>("Projektmenü", optionList);
        selectMenu.activate();

        final String option = selectMenu.getSelectedOption()
                .map(OptionSelectionMenu.Option::get).orElse("Fehler");

        switch (option) {
            case OPTION_SHOW -> {
                System.out.println(ProjektList.getFormatted(ProjektDatabase.getProjekte()));
                projektMenu();
            }
            case OPTION_SHOW_ONE -> {
                System.out.print("Bitte ID des Projekt eingeben: ");
                ProjektDatabase.getProjekt(getIDFromInput(new Scanner(System.in))).ifPresent(ProjektEditMenu::listProjekt);
                projektMenu();
            }
            case OPTION_EDIT -> {
                System.out.print("Bitte ID des Projekt eingeben: ");
                ProjektDatabase.getProjekt(getIDFromInput(new Scanner(System.in))).ifPresent(ProjektEditMenu::editProjekt);
                projektMenu();
            }
            case OPTION_CREATE -> { createProjekt();
                projektMenu();}
            case OPTION_DELETE -> { deleteProjekt();
                projektMenu();}
            case BACK -> startMenu();
        }
    }

    private static Projekt createProjekt(){
        final Projekt newProjekt = new Projekt(ProjektDatabase.getFreeID());
        ProjektEditMenu.editProjekt(newProjekt);
        ProjektDatabase.addProjekt(newProjekt);
        return newProjekt;
    }

    private static void deleteProjekt(){
        System.out.print("Bitte ID des Projekts eingeben: ");
        long id = getIDFromInput(new Scanner(System.in));
        final Optional<Projekt> projekt = ProjektDatabase.getProjekt(id);
        if (projekt.isEmpty()) {
            System.out.print("Kein Projekt mit dieser ID vorhanden!");
            projektMenu();
        }
        projekt.ifPresent(ProjektEditMenu::listProjekt);
        System.out.print("Wirklich löschen?[Y/N]");
        if (!new Scanner(System.in).next().equals("Y"))
            projektMenu();
        projekt.ifPresent(ProjektDatabase::removeProjekt);
        projektMenu();
    }
}
