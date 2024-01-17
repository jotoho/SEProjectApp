package de.jotoho.fhswf.se.projectapp.ui;

import de.jotoho.fhswf.se.projectapp.Ansprechpartner;
import de.jotoho.fhswf.se.projectapp.Unternehmen;
import de.jotoho.fhswf.se.projectapp.database.Database;

import java.util.*;

import static de.jotoho.fhswf.se.projectapp.ui.StartMenu.startMenu;
import static de.jotoho.fhswf.se.projectapp.ui.UnternehmenMenu.createUnternehmen;

public final class AnsprechpartnerMenu {
    private static final String PLACEHOLDER = "Platzhalter";
    private static final String OPTION_SHOW_ANSPRECHPARTNER = "Alle Ansprechpartner anzeigen";
    private static final String OPTION_SHOW_ONE_ANSPRECHPARTNER = "Ein Ansprechpartner anzeigen";
    private static final String OPTION_EDIT_ANSPRECHPARTNER = "Ansprechpartner bearbeiten";
    private static final String OPTION_CREATE_ANSPRECHPARTNER = "Ansprechpartner erstellen";
    private static final String OPTION_DELETE_ANSPRECHPARTNER = "Ansprechpartner löschen";
    private static final String BACK = "Zurück";

    private AnsprechpartnerMenu(){}
    
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


    public static void ansprechpartnerMenu(){
        final List<OptionSelectionMenu.Option<String>> optionList = new ArrayList<>();

        optionList.add(new OptionSelectionMenu.Option<>(OPTION_SHOW_ANSPRECHPARTNER, Set.of("Alle"), true, OPTION_SHOW_ANSPRECHPARTNER, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_SHOW_ONE_ANSPRECHPARTNER, Set.of("Einen"), true, OPTION_SHOW_ONE_ANSPRECHPARTNER, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_EDIT_ANSPRECHPARTNER, Set.of("Edit"), true, OPTION_EDIT_ANSPRECHPARTNER, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_CREATE_ANSPRECHPARTNER, Set.of("Create"), true, OPTION_CREATE_ANSPRECHPARTNER, null));
        optionList.add(new OptionSelectionMenu.Option<>(OPTION_DELETE_ANSPRECHPARTNER, Set.of("Delete"), true, OPTION_DELETE_ANSPRECHPARTNER, null));
        optionList.add(new OptionSelectionMenu.Option<>(BACK, Set.of("Back"), true, BACK, null));


        final var selectMenu = new OptionSelectionMenu<>("Wählen sie ihre Option.", optionList);
        selectMenu.activate();

        final String option = selectMenu.getSelectedOption()
                .map(OptionSelectionMenu.Option::get).orElse("Fehler");

        switch (option) {
            case OPTION_SHOW_ANSPRECHPARTNER -> {
                System.out.println(AnsprechpartnerList.getFormatted(Database.getAnsprechpartner()));
                ansprechpartnerMenu();
            }
            case OPTION_SHOW_ONE_ANSPRECHPARTNER -> {
                System.out.print("Bitte ID des Ansprechpartner eingeben: ");
                Database.getAnsprechpartner(getID(new Scanner(System.in))).ifPresent(AnsprechpartnerEditMenu::listAnsprechpartner);
                ansprechpartnerMenu();
            }
            case OPTION_EDIT_ANSPRECHPARTNER -> {
                System.out.print("Bitte ID des Ansprechpartner eingeben: ");
                Database.getAnsprechpartner(getID(new Scanner(System.in))).ifPresent(AnsprechpartnerEditMenu::editAnsprechpartner);
                ansprechpartnerMenu();
            }
            case OPTION_CREATE_ANSPRECHPARTNER ->{ createAnsprechpartner();ansprechpartnerMenu();}
            case OPTION_DELETE_ANSPRECHPARTNER -> {deleteAnsprechpartner();ansprechpartnerMenu();}
            case BACK -> startMenu();
        }
    }

    public static Ansprechpartner createAnsprechpartner(){
        final Ansprechpartner newAnsprechpartner = new Ansprechpartner(getFreeID(),PLACEHOLDER,PLACEHOLDER,createUnternehmen());
        AnsprechpartnerEditMenu.editAnsprechpartner(newAnsprechpartner);
        while(newAnsprechpartner.getFirstName().equals(PLACEHOLDER) || newAnsprechpartner.getFamilyName().equals(PLACEHOLDER)){
            System.out.print("Bitte die Einträge mit " + PLACEHOLDER + " oder ändern!");
            AnsprechpartnerEditMenu.listAnsprechpartner(newAnsprechpartner);
        }
        Database.addAnsprechpartner(newAnsprechpartner);
        return newAnsprechpartner;
    }

    public static void deleteAnsprechpartner(){
        System.out.print("Bitte ID des Ansprechpartner eingeben: ");
        long id = getID(new Scanner(System.in));
        final Optional<Unternehmen> unternehmen = Database.getUnternehmen(id);
        if (unternehmen.isEmpty()) {
            System.out.print("Kein Ansprechpartner mit dieser ID vorhanden!");
            ansprechpartnerMenu();
        }
        unternehmen.ifPresent(UnternehmenEditMenu::listUnternehmen);
        System.out.print("Wirklich löschen?[Y/N]");
        if (!new Scanner(System.in).next().equals("Y"))
            ansprechpartnerMenu();
        unternehmen.ifPresent(Database::removeUnternehmen);
        ansprechpartnerMenu();
    }
}
