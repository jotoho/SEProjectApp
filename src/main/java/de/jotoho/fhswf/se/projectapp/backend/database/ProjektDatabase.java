/*
    SPDX-License-Identifier: AGPL-3.0-only
    SPDX-FileCopyrightText: 2024 Tim Beckmann <beckmann.tim@fh-swf.de>
    SPDX-FileCopyrightText: 2024 Jonas Tobias Hopusch <git@jotoho.de>
*/
package de.jotoho.fhswf.se.projectapp.backend.database;

import de.jotoho.fhswf.se.projectapp.Ansprechpartner;
import de.jotoho.fhswf.se.projectapp.Projekt;
import de.jotoho.fhswf.se.projectapp.backend.stringgenerator.ProjektStringGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static de.jotoho.fhswf.se.projectapp.backend.database.Database.*;

@SuppressWarnings("unused")
public final class ProjektDatabase {
    private static final Set<Projekt> loadedProjekte = new HashSet<>();
    private static final SequencedSet<Long> freeIDsFromDatabase = new LinkedHashSet<>();

    private ProjektDatabase() {
    }

    public static void initProjekt(){
        loadedProjekte.addAll(loadProjektFromDatabase());
        freeIDsFromDatabase.addAll(loadAIDsFromDatabase());
    }

    public static Set<Projekt> getProjekte() {
        return Collections.unmodifiableSet(loadedProjekte);
    }

    public static Optional<Projekt> getProjekt(final long id) {
        for (final Projekt projekt : loadedProjekte) {
            if (projekt.getID() == id)
                return Optional.of(projekt);
        }
        return Optional.empty();
    }

    private static Set<Long> getProjektIDsFromDatabase() {
        final Set<Long> ids = new HashSet<>();
        try {
            final ResultSet results = executeQuery(ProjektStringGenerator.GET_ALL_IDS, getConnection());
            while (results.next()) {
                ids.add(results.getLong("P_ID"));
            }
        } catch (final SQLException exception) {
            System.err.println(exception.getMessage());
        }
        return ids;
    }

    public static boolean checkIfAnsprechpartnerExists(final long id) {
        return getProjektIDsFromDatabase().contains(id);
    }

    private static SequencedSet<Long> getFreeIDs() {
        freeIDsFromDatabase.addAll(loadAIDsFromDatabase());
        final SequencedSet<Long> usedIDs = new LinkedHashSet<>();
        loadedProjekte.stream().map(Projekt::getID).forEach(usedIDs::add);
        freeIDsFromDatabase.removeAll(usedIDs);
        return freeIDsFromDatabase;
    }

    public static Long getFreeID() {
        final SequencedSet<Long> ids = getFreeIDs();
        if (ids.isEmpty()) {
            executeStatement(ProjektStringGenerator.NEW_IDS);
            ids.addAll(getFreeIDs());
        }
        return ids.getFirst();
    }
    private static void fillProjektWithMember(final ResultSet results,final Projekt projekt) throws SQLException{
        for(int member = 1; member <= Projekt.MAX_MEMBERS;member++){
            if(results.getString("Student_"+member) == null)
                continue;
            if(StudentDatabase.getStudent(results.getLong("Student_"+member)).isEmpty())
                continue;
            projekt.addMember(StudentDatabase.getStudent(results.getLong("Student_"+member)).get());
        }
    }
    private static Projekt fillProjektWithResult(final ResultSet results) throws SQLException {
        Objects.requireNonNull(results);
        if (Projekt.getInstanceRef(results.getLong("P_ID")).isPresent())
            return null;

        final Optional<Ansprechpartner> ansprechpartner = AnsprechpartnerDatabase.getAnsprechpartner(results.getLong("Ansprechpartner"));
        if (ansprechpartner.isEmpty())
            return null;
        final Projekt newProjekt= new Projekt(results.getLong("P_ID"));

        newProjekt.setStatus(
            switch (results.getString("Status")){
                case "DRAFT" -> Projekt.Status.DRAFT;
                case "IN_REVIEW" -> Projekt.Status.IN_REVIEW;
                case "APPROVED" -> Projekt.Status.APPROVED;
                case "DENIED" -> Projekt.Status.DENIED;
                default -> throw new SQLException("Status from database is invalid!");
            });
        fillProjektWithMember(results,newProjekt);
        newProjekt.setContact(ansprechpartner.get());
        newProjekt.setTitle(results.getString("Title"));
        newProjekt.setDescription(results.getString("Beschreibung"));
        newProjekt.setContext(results.getString("Kontext"));
        newProjekt.setTextOutline(results.getString("Outline"));
        newProjekt.setFeedback(results.getString("Feedback"));

        return newProjekt;
    }

    private static Set<Projekt> loadProjektFromDatabase() {
        final Set<Projekt> loadedProjekt = new HashSet<>();
        try (final ResultSet results = Database.executeQuery(ProjektStringGenerator.SELECT_ALL, Database.getConnection())) {
            while (results.next()) {
                final Projekt projekt = fillProjektWithResult(results);
                if (projekt != null)
                    loadedProjekt.add(projekt);
            }
        } catch (final SQLException exception) {
            System.err.println("Unable to load Projekt!\n" + exception.getMessage());
        }
        return loadedProjekt;
    }

    private static Set<Long> loadAIDsFromDatabase() {
        final Set<Long> ids = new HashSet<>();
        try (final ResultSet results = Database.executeQuery(ProjektStringGenerator.FREE_IDS, Database.getConnection())) {
            while (results.next()) {
                ids.add(results.getLong("ID"));
            }
        } catch (final SQLException exception) {
            System.err.println("Unable to load P_IDs!\n" + exception.getMessage());
        }
        return ids;
    }

    public static void addProjekt(final Projekt projekt) {
        Objects.requireNonNull(projekt);
        loadedProjekte.add(projekt);
    }

    public static void removeProjekt(final Projekt projekt) {
        Objects.requireNonNull(projekt);
        loadedProjekte.remove(projekt);
    }

    public static void removeDeletedProjekt() {
        for (final long id : getProjektIDsFromDatabase())
            if (getProjekt(id).isEmpty())
                executeStatement(ProjektStringGenerator.deleteProjektString(id));
    }

    public static void updateProjekt() {
        for (final Projekt projekt : getProjekte())
            update(projekt);
    }

    public static void insertNewProjekt() {
        for (final Projekt projekt : loadedProjekte)
            if (!checkIfAnsprechpartnerExists(projekt.getID()))
                insert(projekt);
    }

    public static void saveProjekte() {
        removeDeletedProjekt();
        updateProjekt();
        insertNewProjekt();
    }

}
