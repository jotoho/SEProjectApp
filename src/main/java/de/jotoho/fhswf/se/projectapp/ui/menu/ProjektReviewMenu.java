/*
    SPDX-License-Identifier: AGPL-3.0-only
    SPDX-FileCopyrightText: 2024 Tim Beckmann <beckmann.tim@fh-swf.de>
    SPDX-FileCopyrightText: 2024 Jonas Tobias Hopusch <git@jotoho.de>
*/
package de.jotoho.fhswf.se.projectapp.ui.menu;

import de.jotoho.fhswf.se.projectapp.Projekt;
import de.jotoho.fhswf.se.projectapp.Projekt.Status;
import de.jotoho.fhswf.se.projectapp.ui.menu.OptionSelectionMenu.Option;
import de.jotoho.fhswf.se.projectapp.ui.editmenu.ProjektEditMenu;
import de.jotoho.fhswf.se.projectapp.ui.editmenu.ProjektEditMenu.AttributeSelector;
import static de.jotoho.fhswf.se.projectapp.ui.editmenu.ProjektEditMenu.listProjekt;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.SequencedSet;
import java.util.Set;

public final class ProjektReviewMenu {
    private ProjektReviewMenu() {

    }

    private static final SequencedSet<Option<Status>> JUDGEMENTS;

    static {
        final var set = new LinkedHashSet<Option<Status>>(3);
        set.add(new Option<>("Projekt genehmigen",
                             Set.of("genehmigen"),
                             true,
                             Status.APPROVED,
                             null));
        set.add(new Option<>("Projekt ablehnen",
                             Set.of("ablehnen"),
                             true,
                             Status.DENIED,
                             null));
        set.add(new Option<>("Projekt zur Überarbeitung zurückleiten",
                             Set.of("überarbeiten", "ueberarbeiten"),
                             true,
                             Status.DRAFT,
                             null));
        JUDGEMENTS = Collections.unmodifiableSequencedSet(set);
    }

    public static void review(final Projekt projekt, final boolean allowRereview) {
        requireNonNull(projekt);
        if (!allowRereview && projekt.getStatus() != Status.IN_REVIEW)
            throw new IllegalArgumentException("Only projects in review, may be reviewed!");
        listProjekt(projekt);
        ProjektEditMenu.editProjektAttribute(projekt, AttributeSelector.FEEDBACK);
        final var selectMenu = new OptionSelectionMenu<>("Entscheiden Sie über den " +
                                                         "Projektvorschlag", JUDGEMENTS);
        selectMenu.activate();
        selectMenu.getSelectedOption()
                  .ifPresent(judgement -> {
            final var newStatus = judgement.get();
            if (nonNull(newStatus))
                projekt.setStatus(newStatus);
        });
    }

    public static void review(final Projekt projekt) {
        review(projekt, false);
    }

    public static void main(final String[] args) {
        final var projekt = new Projekt(1);
        projekt.setStatus(Status.IN_REVIEW);
        review(projekt);
        listProjekt(projekt);
    }
}
