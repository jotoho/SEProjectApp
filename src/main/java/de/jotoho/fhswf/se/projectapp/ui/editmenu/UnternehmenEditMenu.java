/*
    SPDX-License-Identifier: AGPL-3.0-only
    SPDX-FileCopyrightText: 2024 Tim Beckmann <beckmann.tim@fh-swf.de>
    SPDX-FileCopyrightText: 2024 Jonas Tobias Hopusch <git@jotoho.de>
*/
package de.jotoho.fhswf.se.projectapp.ui.editmenu;

import de.jotoho.fhswf.se.projectapp.Unternehmen;
import de.jotoho.fhswf.se.projectapp.ui.menu.OptionSelectionMenu;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

import static java.util.Objects.*;

@SuppressWarnings("unused")
public final class UnternehmenEditMenu {
    private UnternehmenEditMenu() {
    }

    public static void listUnternehmen(final Unternehmen unternehmen) {
        requireNonNull(unternehmen);
        System.out.println("== Unternehmeninformation ==");
        System.out.print("Unternehmen ID: ");
        System.out.println(unternehmen.getID());
        System.out.print("Name: ");
        System.out.println(unternehmen.getName());
        System.out.print("Adresse: ");
        System.out.println(unternehmen.getAddress());
    }

    public static void editUnternehmen(final Unternehmen unternehmen) {

        final ArrayList<OptionSelectionMenu.Option<AttributeSelector>> attributes = new ArrayList<>();
        Optional<OptionSelectionMenu.Option<AttributeSelector>> selection = Optional.empty();
        boolean firstTime = true;
        while (firstTime || selection.map(OptionSelectionMenu.Option::get).isPresent()) {
            listUnternehmen(unternehmen);
            firstTime = false;
            final OptionSelectionMenu<AttributeSelector> menu = new OptionSelectionMenu<>(
                    "Welches Attribut möchten Sie bearbeiten" + "? ('beenden' zum beenden)");
            menu.addOption(new OptionSelectionMenu.Option<>("Name",
                    Set.of("name"),
                    true,
                    AttributeSelector.NAME,
                    null));
            menu.addOption(new OptionSelectionMenu.Option<>("Adresse",
                    Set.of("adresse"),
                    true,
                    AttributeSelector.ADDRESS,
                    null));
            menu.addOption(new OptionSelectionMenu.Option<>("Beenden", Set.of("beenden"), true, null, null));
            menu.activate();

            selection = menu.getSelectedOption();
            if (selection.isPresent() && selection.map(OptionSelectionMenu.Option::get).isPresent()) {
                editUnternehmenAttribute(unternehmen, selection.get().get());
            }
        }

    }

    public static void editUnternehmenAttribute(final Unternehmen unternehmen,
                                                final AttributeSelector attribute) {
        requireNonNull(unternehmen);
        requireNonNull(attribute);
        final var scanner = new Scanner(System.in);

        switch (attribute) {
            case NAME -> {
                System.out.println(
                        "Geben Sie einen neuen Namen für das Unternehmen " + unternehmen.getID() +
                                " ein.");
                String newName = null;
                while (isNull(newName) || newName.isBlank() || newName.length() > 30 || newName.length() < 2) {
                    System.out.print("Neuer Name: ");
                    while (!scanner.hasNextLine()) {
                        // Do nothing but wait
                        try {
                            Thread.sleep(Duration.ofSeconds(1));
                        } catch (final InterruptedException ie) {
                            break;
                        }
                    }
                    final var line = scanner.nextLine();
                    if (nonNull(line)) {
                        newName = line.strip();
                    }
                }
                unternehmen.setName(newName);
            }
            case ADDRESS -> {
                System.out.println(
                        "Geben Sie eine neue Adresse für das Unternehmen " + unternehmen.getID() +
                                " ein.");
                String newAddress = null;
                while (isNull(newAddress) || newAddress.isBlank() ||
                        newAddress.length() > 30 || newAddress.length() < 2) {
                    System.out.print("Neue Adresse: ");
                    while (!scanner.hasNextLine()) {
                        // Do nothing but wait
                        try {
                            Thread.sleep(Duration.ofSeconds(1));
                        } catch (final InterruptedException ie) {
                            break;
                        }
                    }
                    final var line = scanner.nextLine();
                    if (nonNull(line)) {
                        newAddress = line.strip();
                    }
                }
                unternehmen.setAddress(newAddress);
            }
        }
    }

    public enum AttributeSelector {
        NAME, ADDRESS
    }

    public static void main(final String[] args) {
        Unternehmen test = new Unternehmen(1,"Test AG","Teststraße.1");
        editUnternehmen(test);
    }
}
