/*
    SPDX-License-Identifier: AGPL-3.0-only
    SPDX-FileCopyrightText: 2024 Tim Beckmann <beckmann.tim@fh-swf.de>
    SPDX-FileCopyrightText: 2024 Jonas Tobias Hopusch <git@jotoho.de>
*/
package de.jotoho.fhswf.se.projectapp.backend.stringgenerator;

import de.jotoho.fhswf.se.projectapp.Ansprechpartner;

import java.util.Objects;

@SuppressWarnings("unused")
public class AnsprechpartnerStringGenerator {

    public static final String SELECT_ALL = "select * from Ansprechpartner";
    public static final String GET_ALL_IDS = "select A_ID from Ansprechpartner ";
    public static final String NUMBER_OF_A_IDS = "select count(*) from A_ID";
    public static final String FREE_IDS = "select * from A_ID";
    public static final String NEW_IDS = "insert into A_ID default values";


    private AnsprechpartnerStringGenerator(){}

    public static String selectAnsprechpartnerString(final long id) {
        return "select * from Ansprechpartner where A_ID = " + id;
    }

    public static String insertAnsprechpartnerString(final Ansprechpartner ansprechpartner) {
        Objects.requireNonNull(ansprechpartner);
        return "insert into Ansprechpartner(A_ID,Vorname,Nachname,Arbeitgeber) " +
                "values(" + ansprechpartner.getID() +
                ",\"" + ansprechpartner.getFirstName() + '\"' +
                ",\"" + ansprechpartner.getFamilyName() + '\"' +
                ',' + ansprechpartner.getOrganization().getID() + ");";
    }

    public static String updateAnsprechpartnerString(final Ansprechpartner ansprechpartner) {
        Objects.requireNonNull(ansprechpartner);
        return "update Ansprechpartner set " +
                "Vorname = \"" + ansprechpartner.getFirstName() + "\"," +
                "Nachname = \"" + ansprechpartner.getFamilyName() + "\"," +
                "Arbeitgeber = " + ansprechpartner.getOrganization().getID() +
                " where A_ID =" + ansprechpartner.getID();
    }

    public static String deleteAnsprechpartnerString(final long id) {
        return "delete from Ansprechpartner where A_ID =" + id;
    }
}
