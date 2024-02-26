/*
    SPDX-License-Identifier: AGPL-3.0-only
    SPDX-FileCopyrightText: 2024 Tim Beckmann <beckmann.tim@fh-swf.de>
    SPDX-FileCopyrightText: 2024 Jonas Tobias Hopusch <git@jotoho.de>
*/
package de.jotoho.fhswf.se.projectapp.ui.editmenu;

import de.jotoho.fhswf.se.projectapp.Ansprechpartner;
import de.jotoho.fhswf.se.projectapp.Unternehmen;
import de.jotoho.fhswf.se.projectapp.ui.menu.OptionSelectionMenu;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.Objects.*;

@SuppressWarnings("unused")
public class AnsprechpartnerEditMenu {
    private static final Pattern familyNamePattern =
            Pattern.compile("[a-z \\-]{2,30}", Pattern.CASE_INSENSITIVE);
    private static final Pattern firstNamePattern =
            Pattern.compile("[a-z \\-]{2,30}", Pattern.CASE_INSENSITIVE);

    public static void listAnsprechpartner(final Ansprechpartner ansprechpartner){
        requireNonNull(ansprechpartner);
        System.out.println("== Ansprechpartnerinformation ==");
        System.out.print("Ansprechpartner ID: ");
        System.out.println(ansprechpartner.getID());
        System.out.print("Vorname: ");
        System.out.println(ansprechpartner.getFirstName());
        System.out.print("Nachname: ");
        System.out.println(ansprechpartner.getFamilyName());
        System.out.print("Unternehmen ID: ");
        System.out.println(ansprechpartner.getOrganization().getID());
        System.out.print("Unternehmenname: ");
        System.out.println(ansprechpartner.getOrganization().getName());
    }
    public static void editAnsprechpartner(final Ansprechpartner ansprechpartner){
        requireNonNull(ansprechpartner);
        final ArrayList<OptionSelectionMenu.Option<AttributeSelector>> attributes = new ArrayList<>();
        Optional<OptionSelectionMenu.Option<AttributeSelector>> selection = Optional.empty();
        boolean firstTime = true;
        while (firstTime || selection.map(OptionSelectionMenu.Option::get).isPresent()) {
            listAnsprechpartner(ansprechpartner);
            firstTime = false;
            final OptionSelectionMenu<AttributeSelector> menu = new OptionSelectionMenu<>(
                    "Welches Attribut möchten Sie bearbeiten" + "? ('beenden' zum beenden)");
            menu.addOption(new OptionSelectionMenu.Option<>("Nachname",
                    Set.of("nachname"),
                    true,
                    AttributeSelector.LASTNAME,
                    null));
            menu.addOption(new OptionSelectionMenu.Option<>("Vorname",
                    Set.of("vorname"),
                    true,
                    AttributeSelector.FIRSTNAME,
                    null));
            menu.addOption(new OptionSelectionMenu.Option<>("Beenden", Set.of("beenden"), true, null, null));
            menu.activate();

            selection = menu.getSelectedOption();
            if (selection.isPresent() && selection.map(OptionSelectionMenu.Option::get).isPresent()) {
                editAnsprechpartnerAttribute(ansprechpartner, selection.get().get());
            }
        }
    }
    public static void editAnsprechpartnerAttribute(final Ansprechpartner ansprechpartner,
                                                    final AttributeSelector selector){
        requireNonNull(ansprechpartner);
        requireNonNull(selector);
        final var scanner = new Scanner(System.in);
        switch (selector) {
            case LASTNAME -> {
                System.out.println(
                        "Geben Sie einen neuen Nachnamen für Ansprechpartner " + ansprechpartner.getID() +
                                " ein.");
                String newName = null;
                while (isNull(newName) || newName.isBlank() ||
                        !familyNamePattern.matcher(newName).matches()) {
                    System.out.print("Neuer Nachname: ");
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
                ansprechpartner.setFamilyName(newName);
            }
            case FIRSTNAME -> {
                System.out.println(
                        "Geben Sie einen neuen Vornamen für Ansprechpartner " + ansprechpartner.getID() +
                                " ein.");
                String newName = null;
                while (isNull(newName) || newName.isBlank() ||
                        !firstNamePattern.matcher(newName).matches()) {
                    System.out.print("Neuer Vorname: ");
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
                ansprechpartner.setFirstName(newName);
            }
        }


    }
    public enum AttributeSelector{
        FIRSTNAME, LASTNAME, EDIT_ORGANIZATION, NEW_ORGANIZATION
    }

    public static void main(final String[] args) {
        Unternehmen test = new Unternehmen(1,"Test","Test");
        Ansprechpartner ansprechpartner = new Ansprechpartner(1,"Test","Test",test);
        listAnsprechpartner(ansprechpartner);
    }
}
