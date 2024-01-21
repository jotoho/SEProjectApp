package de.jotoho.fhswf.se.projectapp.ui.menu;

import de.jotoho.fhswf.se.projectapp.Unternehmen;
import de.jotoho.fhswf.se.projectapp.backend.database.UnternehmenDatabase;
import de.jotoho.fhswf.se.projectapp.ui.editmenu.UnternehmenEditMenu;
import de.jotoho.fhswf.se.projectapp.ui.list.UnternehmenList;

import java.util.*;

import static de.jotoho.fhswf.se.projectapp.backend.database.Database.getIDFromInput;
import static de.jotoho.fhswf.se.projectapp.ui.menu.StartMenu.startMenu;

@SuppressWarnings("unused")
public final class UnternehmenMenu {
    private static final String PLACEHOLDER = "Platzhalter";
    private static final String OPTION_SHOW = "Alle Unternehmen anzeigen";
    private static final String OPTION_SHOW_ONE = "Ein Unternehmen anzeigen";
    private static final String OPTION_EDIT = "Unternehmen bearbeiten";
    private static final String OPTION_CREATE = "Unternehmen erstellen";
    private static final String OPTION_DELETE = "Unternehmen löschen";
    private static final String BACK = "Zurück";
    private UnternehmenMenu(){}

    public static void unternehmenMenu(){
        final List<OptionSelectionMenu.Option<String>> optionList = new ArrayList<>();

        optionList.add(new OptionSelectionMenu.Option<>(OPTION_SHOW, Set.of("Alle"), true, OPTION_SHOW, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_SHOW_ONE, Set.of("Eins"), true, OPTION_SHOW_ONE, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_EDIT, Set.of("Edit"), true, OPTION_EDIT, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_CREATE, Set.of("Create"), true, OPTION_CREATE, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_DELETE, Set.of("Delete"), true, OPTION_DELETE, null));
        optionList.add(new OptionSelectionMenu.Option<>(BACK, Set.of("Back"), true, BACK, null));


        final var selectMenu = new OptionSelectionMenu<>("Unternehmenmenü", optionList);
        selectMenu.activate();

        final String option = selectMenu.getSelectedOption()
                .map(OptionSelectionMenu.Option::get).orElse("Fehler");

        switch (option) {
            case OPTION_SHOW -> {
                System.out.println(UnternehmenList.getFormatted(UnternehmenDatabase.getUnternehmen()));
                unternehmenMenu();
            }
            case OPTION_SHOW_ONE -> {
                System.out.print("Bitte ID des Unternehmen eingeben: ");
                UnternehmenDatabase.getUnternehmen(getIDFromInput(new Scanner(System.in))).ifPresent(UnternehmenEditMenu::listUnternehmen);
                unternehmenMenu();
            }
            case OPTION_EDIT -> {
                System.out.print("Bitte ID des Unternehmen eingeben: ");
                UnternehmenDatabase.getUnternehmen(getIDFromInput(new Scanner(System.in))).ifPresent(UnternehmenEditMenu::editUnternehmen);
                unternehmenMenu();
            }
            case OPTION_CREATE -> { createUnternehmen();unternehmenMenu();}
            case OPTION_DELETE -> { deleteUnternehmen();unternehmenMenu();}
            case BACK -> startMenu();
        }
    }



    public static Unternehmen createUnternehmen(){
        final Unternehmen newUnternehmen = new Unternehmen(UnternehmenDatabase.getFreeID(),PLACEHOLDER,PLACEHOLDER);
        UnternehmenEditMenu.editUnternehmen(newUnternehmen);
        while(newUnternehmen.getName().equals(PLACEHOLDER) || newUnternehmen.getAddress().equals(PLACEHOLDER)){
            System.out.print("Bitte die Einträge mit " + PLACEHOLDER + " oder ändern!");
            UnternehmenEditMenu.editUnternehmen(newUnternehmen);
        }
        UnternehmenDatabase.addUnternehmen(newUnternehmen);
        return newUnternehmen;
    }

    public static void deleteUnternehmen(){
        System.out.print("Bitte ID des Unternehmen eingeben: ");
        long id = getIDFromInput(new Scanner(System.in));
        final Optional<Unternehmen> unternehmen = UnternehmenDatabase.getUnternehmen(id);
        if (unternehmen.isEmpty()) {
            System.out.print("Kein Unternehmen mit dieser ID vorhanden!");
            unternehmenMenu();
        }
        unternehmen.ifPresent(UnternehmenEditMenu::listUnternehmen);
        System.out.print("Wirklich löschen?[Y/N]");
        if (!new Scanner(System.in).next().equals("Y"))
            unternehmenMenu();
        unternehmen.ifPresent(UnternehmenDatabase::removeUnternehmen);
        unternehmenMenu();
    }


}
