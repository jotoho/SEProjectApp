package de.jotoho.fhswf.se.projectapp.ui;

import de.jotoho.fhswf.se.projectapp.Unternehmen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@SuppressWarnings("unused")
public final class UnternehmenMenu {
    private static final String PLACEHOLDER = "Platzhalter";
    private static final String OPTION_SHOW_UNTERNEHMEN = "Alle Unternehmen anzeigen";
    private static final String OPTION_SHOW_ONE_UNTERNEHMEN = "Ein Unternehmen anzeigen";
    private static final String OPTION_EDIT_UNTERNEHMEN = "Unternehmen bearbeiten";
    private static final String OPTION_CREATE_UNTERNEHMEN = "Unternehmen erstellen";
    private static final String OPTION_DELETE_UNTERNEHMEN = "Unternehmen löschen";
    private UnternehmenMenu(){}

    public static void unternehmenMenu(){
        final List<OptionSelectionMenu.Option<String>> optionList = new ArrayList<>();

        final var selectMenu = new OptionSelectionMenu<>("Wählen sie ihre Option.", optionList);
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_SHOW_UNTERNEHMEN, Set.of("Alle"), true, OPTION_SHOW_UNTERNEHMEN, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_SHOW_ONE_UNTERNEHMEN, Set.of("Eins"), true, OPTION_SHOW_ONE_UNTERNEHMEN, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_EDIT_UNTERNEHMEN, Set.of("Edit"), true, OPTION_EDIT_UNTERNEHMEN, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_CREATE_UNTERNEHMEN, Set.of("Create"), true, OPTION_CREATE_UNTERNEHMEN, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_DELETE_UNTERNEHMEN, Set.of("Delete"), true, OPTION_DELETE_UNTERNEHMEN, null));

        selectMenu.activate();

        final String option = selectMenu.getSelectedOption()
                .map(OptionSelectionMenu.Option::get).orElse("Fehler");

        switch (option) {
            case OPTION_SHOW_UNTERNEHMEN -> {
                UnternehmenList.getFormatted();
                unternehmenMenu();
            }
            case OPTION_SHOW_ONE_UNTERNEHMEN -> {
                UnternehmenEditMenu.listUnternehmen();
                unternehmenMenu();
            }
            case OPTION_EDIT_UNTERNEHMEN -> {
                UnternehmenEditMenu.editUnternehmen();
                unternehmenMenu();
            }
            case OPTION_CREATE_UNTERNEHMEN -> createUnternehmen();
            case OPTION_DELETE_UNTERNEHMEN -> deleteUnternehmen();
        }
    }
    public static void createUnternehmen(){
        long id;
        boolean freeID = false;
        do{
            id = new Random().nextLong();
            if(Unternehmen.getInstanceRef(id).isEmpty())
                freeID = true;
        }while(!freeID);
        final Unternehmen newUnternehmen = new Unternehmen(id,PLACEHOLDER,PLACEHOLDER);
        UnternehmenEditMenu.editUnternehmen(newUnternehmen);
        while(newUnternehmen.getName().equals(PLACEHOLDER) || newUnternehmen.getAddress().equals(PLACEHOLDER)){
            System.out.print("Bitte die Einträge mit " + PLACEHOLDER + " oder ändern!");
            UnternehmenEditMenu.editUnternehmen(newUnternehmen);
        }
        unternehmenMenu();
    }

    public static void deleteUnternehmen(){
        System.out.print();
    }
}
