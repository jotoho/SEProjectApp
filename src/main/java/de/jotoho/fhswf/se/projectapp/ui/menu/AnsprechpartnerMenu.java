package de.jotoho.fhswf.se.projectapp.ui.menu;

import de.jotoho.fhswf.se.projectapp.Ansprechpartner;
import de.jotoho.fhswf.se.projectapp.Unternehmen;
import de.jotoho.fhswf.se.projectapp.backend.database.AnsprechpartnerDatabase;
import de.jotoho.fhswf.se.projectapp.backend.database.UnternehmenDatabase;
import de.jotoho.fhswf.se.projectapp.ui.editmenu.AnsprechpartnerEditMenu;
import de.jotoho.fhswf.se.projectapp.ui.list.AnsprechpartnerList;
import de.jotoho.fhswf.se.projectapp.ui.list.UnternehmenList;

import java.util.*;

import static de.jotoho.fhswf.se.projectapp.backend.database.Database.getIDFromInput;
import static de.jotoho.fhswf.se.projectapp.ui.menu.StartMenu.startMenu;
import static de.jotoho.fhswf.se.projectapp.ui.menu.UnternehmenMenu.createUnternehmen;

public final class AnsprechpartnerMenu {
    private static final String PLACEHOLDER = "Platzhalter";
    private static final String OPTION_SHOW_ANSPRECHPARTNER = "Alle Ansprechpartner anzeigen";
    private static final String OPTION_SHOW_ONE_ANSPRECHPARTNER = "Ein Ansprechpartner anzeigen";
    private static final String OPTION_EDIT_ANSPRECHPARTNER = "Ansprechpartner bearbeiten";
    private static final String OPTION_CREATE_ANSPRECHPARTNER = "Ansprechpartner erstellen";
    private static final String OPTION_DELETE_ANSPRECHPARTNER = "Ansprechpartner löschen";
    private static final String BACK = "Zurück";

    private AnsprechpartnerMenu(){}



    public static void ansprechpartnerMenu(){
        final List<OptionSelectionMenu.Option<String>> optionList = new ArrayList<>();

        optionList.add(new OptionSelectionMenu.Option<>(OPTION_SHOW_ANSPRECHPARTNER, Set.of("Alle"), true, OPTION_SHOW_ANSPRECHPARTNER, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_SHOW_ONE_ANSPRECHPARTNER, Set.of("Einen"), true, OPTION_SHOW_ONE_ANSPRECHPARTNER, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_EDIT_ANSPRECHPARTNER, Set.of("Edit"), true, OPTION_EDIT_ANSPRECHPARTNER, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_CREATE_ANSPRECHPARTNER, Set.of("Create"), true, OPTION_CREATE_ANSPRECHPARTNER, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_DELETE_ANSPRECHPARTNER, Set.of("Delete"), true, OPTION_DELETE_ANSPRECHPARTNER, null));
        optionList.add(new OptionSelectionMenu.Option<>(BACK, Set.of("Back"), true, BACK, null));


        final var selectMenu = new OptionSelectionMenu<>("Ansprechpartnermenü", optionList);
        selectMenu.activate();

        final String option = selectMenu.getSelectedOption()
                .map(OptionSelectionMenu.Option::get).orElse("Fehler");

        switch (option) {
            case OPTION_SHOW_ANSPRECHPARTNER -> {
                System.out.println(AnsprechpartnerList.getFormatted(AnsprechpartnerDatabase.getAnsprechpartner()));
                ansprechpartnerMenu();
            }
            case OPTION_SHOW_ONE_ANSPRECHPARTNER -> {
                System.out.print("Bitte ID des Ansprechpartner eingeben: ");
                AnsprechpartnerDatabase.getAnsprechpartner(getIDFromInput(new Scanner(System.in))).ifPresent(AnsprechpartnerEditMenu::listAnsprechpartner);
                ansprechpartnerMenu();
            }
            case OPTION_EDIT_ANSPRECHPARTNER -> {
                System.out.print("Bitte ID des Ansprechpartner eingeben: ");
                AnsprechpartnerDatabase.getAnsprechpartner(getIDFromInput(new Scanner(System.in))).ifPresent(AnsprechpartnerEditMenu::editAnsprechpartner);
                ansprechpartnerMenu();
            }
            case OPTION_CREATE_ANSPRECHPARTNER -> { createAnsprechpartner();ansprechpartnerMenu();}
            case OPTION_DELETE_ANSPRECHPARTNER -> { deleteAnsprechpartner();ansprechpartnerMenu();}
            case BACK -> startMenu();
        }
    }

    public static Ansprechpartner createAnsprechpartner(){
        final Unternehmen arbeitgeber = pickUnternehmen();
        if(arbeitgeber == null)
            return null;
        final Ansprechpartner newAnsprechpartner = new Ansprechpartner(AnsprechpartnerDatabase.getFreeID(),PLACEHOLDER,PLACEHOLDER,arbeitgeber);
        AnsprechpartnerEditMenu.editAnsprechpartner(newAnsprechpartner);
        while(newAnsprechpartner.getFirstName().equals(PLACEHOLDER) || newAnsprechpartner.getFamilyName().equals(PLACEHOLDER)){
            System.out.print("Bitte die Einträge mit " + PLACEHOLDER + " oder ändern!");
            AnsprechpartnerEditMenu.editAnsprechpartner(newAnsprechpartner);
        }
        AnsprechpartnerDatabase.addAnsprechpartner(newAnsprechpartner);
        return newAnsprechpartner;
    }

    public static void deleteAnsprechpartner(){
        System.out.print("Bitte ID des Ansprechpartner eingeben: ");
        final long id = getIDFromInput(new Scanner(System.in));
        final Optional<Ansprechpartner> ansprechpartner = AnsprechpartnerDatabase.getAnsprechpartner(id);
        if (ansprechpartner.isEmpty()) {
            System.out.print("Kein Ansprechpartner mit dieser ID vorhanden!");
            ansprechpartnerMenu();
        }
        ansprechpartner.ifPresent(AnsprechpartnerEditMenu::listAnsprechpartner);
        System.out.print("Wirklich löschen?[Y/N]");
        if (!new Scanner(System.in).next().equals("Y"))
            ansprechpartnerMenu();
        ansprechpartner.ifPresent(AnsprechpartnerDatabase::removeAnsprechpartner);
        ansprechpartnerMenu();
    }

    public static Unternehmen pickUnternehmen(){
        System.out.print("Unternemhen auswählen[P] oder erstellen[C]?[P/C]");
        final String option = new Scanner(System.in).next();
        switch (option) {
            case "C" -> {
                return createUnternehmen();
            }
            case "P" ->{
                System.out.println(UnternehmenList.getFormatted(UnternehmenDatabase.getUnternehmen()));
                System.out.print("Bitte ID des Unternehmens eingeben: ");
                final long id = getIDFromInput(new Scanner(System.in));
                if (UnternehmenDatabase.getUnternehmen(id).isEmpty()) {
                    System.err.println("ID könnte nicht gefunden werden!");
                    return null;
                }
                return UnternehmenDatabase.getUnternehmen(id).get();
            }
            default -> {
                return null;
            }
        }
    }
}
