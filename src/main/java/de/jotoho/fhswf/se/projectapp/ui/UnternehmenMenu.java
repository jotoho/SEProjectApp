package de.jotoho.fhswf.se.projectapp.ui;

import de.jotoho.fhswf.se.projectapp.Unternehmen;
import de.jotoho.fhswf.se.projectapp.database.Database;

import java.util.*;

import static de.jotoho.fhswf.se.projectapp.ui.StartMenu.startMenu;

@SuppressWarnings("unused")
public final class UnternehmenMenu {
    private static final String PLACEHOLDER = "Platzhalter";
    private static final String OPTION_SHOW_UNTERNEHMEN = "Alle Unternehmen anzeigen";
    private static final String OPTION_SHOW_ONE_UNTERNEHMEN = "Ein Unternehmen anzeigen";
    private static final String OPTION_EDIT_UNTERNEHMEN = "Unternehmen bearbeiten";
    private static final String OPTION_CREATE_UNTERNEHMEN = "Unternehmen erstellen";
    private static final String OPTION_DELETE_UNTERNEHMEN = "Unternehmen löschen";
    private static final String BACK = "Zurück";
    private UnternehmenMenu(){}

    public static void unternehmenMenu(){
        final List<OptionSelectionMenu.Option<String>> optionList = new ArrayList<>();

        optionList.add(new OptionSelectionMenu.Option<>(OPTION_SHOW_UNTERNEHMEN, Set.of("Alle"), true, OPTION_SHOW_UNTERNEHMEN, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_SHOW_ONE_UNTERNEHMEN, Set.of("Eins"), true, OPTION_SHOW_ONE_UNTERNEHMEN, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_EDIT_UNTERNEHMEN, Set.of("Edit"), true, OPTION_EDIT_UNTERNEHMEN, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_CREATE_UNTERNEHMEN, Set.of("Create"), true, OPTION_CREATE_UNTERNEHMEN, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_DELETE_UNTERNEHMEN, Set.of("Delete"), true, OPTION_DELETE_UNTERNEHMEN, null));
        optionList.add(new OptionSelectionMenu.Option<>(BACK, Set.of("Back"), true, BACK, null));


        final var selectMenu = new OptionSelectionMenu<>("Wählen sie ihre Option.", optionList);
        selectMenu.activate();

        final String option = selectMenu.getSelectedOption()
                .map(OptionSelectionMenu.Option::get).orElse("Fehler");

        switch (option) {
            case OPTION_SHOW_UNTERNEHMEN -> {
                System.out.println(UnternehmenList.getFormatted(Database.getUnternehmen()));
                unternehmenMenu();
            }
            case OPTION_SHOW_ONE_UNTERNEHMEN -> {
                System.out.print("Bitte ID des Unternehmen eingeben: ");
                Database.getUnternehmen(getID(new Scanner(System.in))).ifPresent(UnternehmenEditMenu::listUnternehmen);
                unternehmenMenu();
            }
            case OPTION_EDIT_UNTERNEHMEN -> {
                System.out.print("Bitte ID des Unternehmen eingeben: ");
                Database.getUnternehmen(getID(new Scanner(System.in))).ifPresent(UnternehmenEditMenu::editUnternehmen);
                unternehmenMenu();
            }
            case OPTION_CREATE_UNTERNEHMEN ->{ createUnternehmen();unternehmenMenu();}
            case OPTION_DELETE_UNTERNEHMEN -> {deleteUnternehmen();unternehmenMenu();}
            case BACK -> startMenu();
        }
    }

    public static long getFreeID(){
        long id;
        boolean freeID = false;
        do{
            id = new Random().nextInt(1000);
            if(Unternehmen.getInstanceRef(id).isEmpty())
                freeID = true;
        }while(!freeID);
        return id;
    }

    public static long getID(final Scanner scanner) {
        try {
            return scanner.nextLong();
        } catch (final Exception InputMismatchException) {
            scanner.next();
            System.out.print("ID hat das falsche Formart! Bitte erneut eingeben: ");
            return getID(scanner);
        }
    }

    public static Unternehmen createUnternehmen(){
        final Unternehmen newUnternehmen = new Unternehmen(getFreeID(),PLACEHOLDER,PLACEHOLDER);
        UnternehmenEditMenu.editUnternehmen(newUnternehmen);
        while(newUnternehmen.getName().equals(PLACEHOLDER) || newUnternehmen.getAddress().equals(PLACEHOLDER)){
            System.out.print("Bitte die Einträge mit " + PLACEHOLDER + " oder ändern!");
            UnternehmenEditMenu.editUnternehmen(newUnternehmen);
        }
        Database.addUnternehmen(newUnternehmen);
        return newUnternehmen;
    }

    public static void deleteUnternehmen(){
        System.out.print("Bitte ID des Unternehmen eingeben: ");
        long id = getID(new Scanner(System.in));
        final Optional<Unternehmen> unternehmen = Database.getUnternehmen(id);
        if (unternehmen.isEmpty()) {
            System.out.print("Kein Unternehmen mit dieser ID vorhanden!");
            unternehmenMenu();
        }
        unternehmen.ifPresent(UnternehmenEditMenu::listUnternehmen);
        System.out.print("Wirklich löschen?[Y/N]");
        if (!new Scanner(System.in).next().equals("Y"))
            unternehmenMenu();
        unternehmen.ifPresent(Database::removeUnternehmen);
        unternehmenMenu();
    }


}
