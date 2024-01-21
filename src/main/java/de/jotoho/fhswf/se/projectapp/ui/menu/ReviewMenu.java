package de.jotoho.fhswf.se.projectapp.ui.menu;

import de.jotoho.fhswf.se.projectapp.Projekt;
import de.jotoho.fhswf.se.projectapp.backend.database.Database;
import de.jotoho.fhswf.se.projectapp.backend.database.ProjektDatabase;
import de.jotoho.fhswf.se.projectapp.ui.list.ProjektList;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import static de.jotoho.fhswf.se.projectapp.ui.menu.StartMenu.startMenu;

@SuppressWarnings("unused")
public final class ReviewMenu {
    private static final String OPTION_SHOW = "Alle Projekte für Review anzeigen";
    private static final String REVIEW = "Projekt reviewen";
    private static final String BACK = "Zurück";

    private ReviewMenu(){}

    public static void reviewMenu(){
        final List<OptionSelectionMenu.Option<String>> optionList = new ArrayList<>();

        optionList.add(new OptionSelectionMenu.Option<>(OPTION_SHOW, Set.of("Projekte"), true, OPTION_SHOW, null));
        optionList.add(new OptionSelectionMenu.Option<>(REVIEW, Set.of("Review"), true, REVIEW, null));
        optionList.add(new OptionSelectionMenu.Option<>(BACK, Set.of("Back"), true, BACK, null));

        final var selectMenu = new OptionSelectionMenu<>("Reviewmenü", optionList);
        selectMenu.activate();

        final String option = selectMenu.getSelectedOption()
                .map(OptionSelectionMenu.Option::get).orElse("Fehler");

        switch (option) {
            case OPTION_SHOW ->{
                final var reviewProjects = ProjektDatabase.getProjekte()
                                                          .stream()
                                                          .filter(projekt -> projekt.getStatus() == Projekt.Status.IN_REVIEW)
                                                          .collect(Collectors.toSet());
                final String reviewList = ProjektList.getFormatted(reviewProjects);
                System.out.println(reviewList);
                reviewMenu();
            }
            case REVIEW -> {
                System.out.print("Bitte Projekt_ID eingeben: ");
                final long id = Database.getIDFromInput(new Scanner(System.in));
                if(ProjektDatabase.getProjekt(id).isEmpty())
                    reviewMenu();
                if(ProjektDatabase.getProjekt(id).get().getStatus() != Projekt.Status.IN_REVIEW)
                    reviewMenu();
                ProjektReviewMenu.review(ProjektDatabase.getProjekt(id).get());
                reviewMenu();
            }
            case BACK -> startMenu();
        }
    }
}
