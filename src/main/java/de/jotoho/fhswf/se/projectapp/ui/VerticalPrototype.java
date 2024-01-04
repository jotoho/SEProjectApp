package de.jotoho.fhswf.se.projectapp.ui;

import de.jotoho.fhswf.se.projectapp.ui.OptionSelectionMenu.Option;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class VerticalPrototype {
    private VerticalPrototype() {
        super();
    }

    public static void main(final String[] args) {
        final List<Option<String>> optionList = new ArrayList<>();
        optionList.add(new Option<>("White", Set.of("White"), true, "White", null));
        optionList.add(new Option<>("Black", Collections.emptySet(), true, "Black", null));
        optionList.add(new Option<>("Red", Collections.emptySet(), true, "Red", null));
        optionList.add(new Option<>("Green", Collections.emptySet(), true, "Green", null));
        optionList.add(new Option<>("Blue", Collections.emptySet(), true, "Blue", null));
        optionList.add(new Option<>("Rainbow", Set.of("Rainbow"), false, "Rainbow", null));
        final var selectMenu = new OptionSelectionMenu<>("Select a color.", optionList);
        selectMenu.activate();
        System.out.println("User selected: " + selectMenu.getSelectedOption()
                                                         .map(Option::get)
                                                         .orElse("Failure"));
    }
}
